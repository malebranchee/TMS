package com.example.tms.services;

import com.example.tms.dtos.*;
import com.example.tms.exceptions.AppError;
import com.example.tms.exceptions.OkResponse;
import com.example.tms.repository.CommentRepository;
import com.example.tms.repository.TaskRepository;
import com.example.tms.repository.entities.Comment;
import com.example.tms.repository.entities.Task;
import com.example.tms.repository.entities.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.*;

@Service
@AllArgsConstructor
public class TaskService {
    @Autowired
    private final TaskRepository taskRepository;
    @Autowired
    private final UserService userService;
    @Autowired
    private CommentRepository commentRepository;

    /**
     *
     * @param header String of header
     * @return Optionally Task object
     * @throws NoSuchElementException
     */
    Optional<Task> findByHeader(String header) {
        return taskRepository.findByHeader(header);
    }

    /**
     *
     * @return List of tasks
     */
    List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     *
     * Adds task to the database
     */
    void save(Task task) {
        taskRepository.save(task);
    }

    /**
     *
     * @param status Requested status value
     * @param pageable Pageable parameter, containing: num of page, size of page
     *  @return ResponseEntity
     */
    @Transactional
    public ResponseEntity<?> searchTasksByStatus(String status, Pageable pageable)
    {
        return
                (status != null)
                ? new ResponseEntity<>(new PageableDto("Status", pageable.getPageNumber(), pageable.getPageSize(),
                        taskRepository.findAllByStatus(status, pageable).stream().map(Task::toString).toList()), HttpStatus.OK)
                : new ResponseEntity<>(new PageableDto(getAllTasks().stream().map(Task::toString).toList()), HttpStatus.OK);
    }

    /**
     *
     * @param priority Requested priority values
     * @param pageable Pageable parameter, containing: num of page, size of page
     * @return ResponseEntity
     */
    @Transactional
    public ResponseEntity<?> searchTasksByPriority(String priority, Pageable pageable)
    {
        return
                (priority != null)
                ? new ResponseEntity<>(new PageableDto("Priority", pageable.getPageNumber(), pageable.getPageSize(),
                        taskRepository.findAllByPriority(priority, pageable).stream().map(Task::toString).toList()), HttpStatus.OK)
                : new ResponseEntity<>(new PageableDto(getAllTasks().stream().map(Task::toString).toList()), HttpStatus.OK);
    }

    /**
     *
     * @param principal Current user credentials
     * @param pageable Pageable parameter, containing: num of page, size of page
     * @return ResponseEntity
     */
    @Transactional
    public ResponseEntity<?> showMyTasksToDo(Principal principal, Pageable pageable) {
        User user = userService.findByLogin(principal.getName()).orElseThrow(
                () -> new UsernameNotFoundException("No such user!"));
        if (user.getTasksToExecute().isEmpty()) {
            return new ResponseEntity<>(new OkResponse("No tasks to do, chill :)", HttpStatus.OK), HttpStatus.OK);
        }
        return getResponseByPageable(pageable, user);
    }


    /**
     * @param nickname Requested nickname value
     * @param pageable Pageable parameter, containing: num of page, size of page
     * @return ResponseEntity
     */
    @Transactional
    public ResponseEntity<?> showAllTasksOfUser(String nickname, Pageable pageable) {

        User user = userService.findByNickname(nickname).orElseThrow(() -> new NoSuchElementException(
                String.format("User with nickname '%s' doesnt exist", nickname)
        ));
        return getResponseByPageable(pageable, user);
    }


    /**
     * @param user User which task need to parse
     * @param pageable Pageable parameter, containing: num of page, size of page
     * @return ResponseEntity
     */
    private ResponseEntity<?> getResponseByPageable(Pageable pageable, User user) {
        return new ResponseEntity<>(new OkResponse("Tasks to do: \n%s\n Tasks to manage: %s".formatted(
                new PageableDto("MyTasks", pageable.getPageNumber(), pageable.getPageSize(),
                taskRepository.findAll(pageable).getContent().stream()
                        .filter(task -> task.getExecutors().contains(user))
                        .map(Task::toString).toList()).toString(),
                new PageableDto("MyTasks", pageable.getPageNumber(), pageable.getPageSize(),
                taskRepository.findAll(pageable).getContent().stream()
                        .filter(task -> task.getAuthor().equals(user))
                        .map(Task::toString).toList()).toString()), HttpStatus.OK), HttpStatus.OK);
    }

    /**
     * @param principal Current user credentials
     * @param taskHeader Requested header value
     * @param dto Requested change status data object
     * @return ResponseEntity
     */
    @Transactional
    public ResponseEntity<?> changeTaskStatus(Principal principal, String taskHeader, ChangeTaskStatusDTO dto) {
        try {
            User user = userService.findByLogin(principal.getName()).orElseThrow(() ->
                    new UsernameNotFoundException(String.format("No user with login '%s'", principal.getName())));
            Task task = findByHeader(taskHeader).orElseThrow();

            if ((user.getTasksToExecute().contains(task)
                    || user.getTasksToManage().contains(task)
                    )
                    && Arrays.stream(Task.Status.values())
                    .anyMatch(t -> t.equals(Task.Status.valueOf(dto.getStatus()))))
            {
                task.setStatus(dto.getStatus());
                save(task);
                return new ResponseEntity<>(new OkResponse("Status changed to " + task.getStatus(), HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new AppError(405, "You dont have permission to edit status!"), HttpStatus.METHOD_NOT_ALLOWED);
            }
        } catch (UsernameNotFoundException notFoundException) {
            return new ResponseEntity<>(new AppError(400, notFoundException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @param principal Current user credentials
     * @param taskHeader Requested task value
     * @param dto Requested dto to change comment fields
     * @return ResponseEntity<T>
     */
    @Transactional
    public ResponseEntity<?> addComment(Principal principal, String taskHeader, CommentDto dto) {
        try {
            User user = userService.findByLogin(principal.getName()).orElseThrow();
            if (
                    (user.getTasksToExecute().contains(findByHeader(taskHeader).orElseThrow())
                    | user.getTasksToManage().contains(findByHeader(taskHeader).orElseThrow()))
                    && !findByHeader(taskHeader).orElseThrow().getStatus().equals(Task.Status.CLOSED.toString())
            ) {
                Task task = findByHeader(taskHeader).orElseThrow();
                ArrayList<Comment> comments = new ArrayList(task.getComments());
                Comment comment = new Comment(user, dto.getComment());
                commentRepository.save(comment);
                comments.add(comment);
                task.setComments(comments);
                save(task);
                return new ResponseEntity<>(new OkResponse("Created new comment: " + dto.getComment(), HttpStatus.CREATED), HttpStatus.CREATED);
            }
            return new ResponseEntity<>(new AppError(405, "You dont have permission to add comments to this task!"), HttpStatus.METHOD_NOT_ALLOWED);
        } catch (NoSuchElementException noSuchElementException) {
            return new ResponseEntity<>(new AppError(400, noSuchElementException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @param principal Current user credentials
     * @param taskDto Requested body of task
     * @return ResponseEntity<T>
     */
    @Transactional
    public ResponseEntity<?> createTask(Principal principal, TaskDto taskDto) {
        try {
            User user = userService.findByLogin(principal.getName()).orElseThrow();
            List<Task> tasks = new ArrayList<>(getAllTasks());
            boolean ifTaskExists = tasks.stream().anyMatch(task -> task.getHeader().equals(taskDto.getHeader()));
            if (ifTaskExists) {
                Task foundTask = tasks
                        .stream()
                        .filter(task -> task.getHeader().equals(taskDto.getHeader()))
                        .toList()
                        .get(0);
                return new ResponseEntity<>(new AppError(400, String.format("Task with header '%s' already exists: \n" +
                        "%s", taskDto.getHeader(), foundTask.toString())), HttpStatus.BAD_REQUEST);
            } else {
                List<User> executors = new ArrayList<>();
                executors.addAll(taskDto.getExecutorNicknames()
                        .stream()
                        .filter(nickname -> userService.findByNickname(nickname).isPresent())
                        .map(nickname -> userService.findByNickname(nickname).orElseThrow())
                        .toList());

                try {
                    Task task = new Task(taskDto.getHeader(), taskDto.getDescription(), Task.Status.CREATED, Task.Priority.valueOf(taskDto.getPriority()), executors, user);
                    save(task);
                    return new ResponseEntity<>(new TaskDto(task.getHeader(), task.getDescription(), task.getStatus(), task.getPriority(),
                            task.getExecutors().stream().map(User::getNickname).toList(), task.getAuthor().getNickname(), task.getComments().stream().map(Comment::toString).toList()), HttpStatus.CREATED);
                } catch (IllegalArgumentException e) {
                    return new ResponseEntity<>(new AppError(400, String.format("Priority '%s' doesnt exist", taskDto.getPriority())), HttpStatus.BAD_REQUEST);
                }
            }
        } catch (NoSuchElementException noSuchElementException) {
            return new ResponseEntity<>(new AppError(400,"No such user :("), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @param taskHeader Requested task string
     * @param dto Requested data object
     * @return ResponseEntity<T>
     */
    @Transactional
    public ResponseEntity<?> changePriority(String taskHeader, ChangeTaskPriorityDTO dto)
    {
        try {
            Task task = findByHeader(taskHeader).orElseThrow(() -> new NoSuchElementException(String.format("Task with such header '%s' doesnt exist", taskHeader)));
            try {
                if (Arrays.stream(Task.Priority.values()).map(Enum::toString).toList().contains(dto.getPriority())) {
                    task.setPriority(dto.getPriority());
                    save(task);
                    return new ResponseEntity<>(new TaskDto(task.getHeader(), task.getDescription(), task.getStatus(), task.getPriority(),
                            task.getExecutors().stream().map(User::getNickname).toList(), task.getAuthor().getNickname(), task.getComments().stream().map(Comment::toString).toList()), HttpStatus.OK);
                } else {
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException illegalArgumentException) {
                return new ResponseEntity<>(new AppError(400, "No such priority. Available: HIGH, MEDIUM, LOW.\n"), HttpStatus.BAD_REQUEST);
            }
        } catch (NoSuchElementException e)
        {
            return new ResponseEntity<>(new AppError(400, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @param taskHeader Requested task string
     * @param dto Requested dto data object
     * @return ResponseEntity<T>
     */
    @Transactional
    public ResponseEntity<?> changeDescription(String taskHeader, ChangeTaskDescriptionDTO dto)
    {
        Task task = findByHeader(taskHeader).orElseThrow(() -> new NoSuchElementException(String.format("Task with such header '%s' doesnt exist", taskHeader)));
        try{
            task.setDescription(dto.getDescription());
            save(task);
            return new ResponseEntity<>(new TaskDto(task.getHeader(), task.getDescription(), task.getStatus(), task.getPriority(),
                    task.getExecutors().stream().map(User::getNickname).toList(), task.getAuthor().getNickname(), task.getComments().stream().map(Comment::toString).toList()), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException)
        {
            return new ResponseEntity<>(new AppError(illegalArgumentException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @param taskHeader Requested task string
     * @param dto Requested dto data object
     * @return ResponseEntity<T>
     */
    @Transactional
    public ResponseEntity<?> addExecutors(String taskHeader, ExecutorNamesDTO dto)
    {
        try{
            Task task = findByHeader(taskHeader).orElseThrow(() -> new NoSuchElementException(String.format("Task with such header '%s' doesnt exist", taskHeader)));
            List<User> executors = task.getExecutors();
            executors.addAll(dto.getExecutorNames()
                    .stream()
                    .filter(name -> userService.findByNickname(name).isPresent())
                    .map(name -> userService.findByNickname(name).get())
                    .toList());
            task.setExecutors(executors);
            if (task.getStatus().equals("WAITING") || task.getStatus().equals("CLOSED"))
            {
                task.setStatus("IN_PROGRESS");
            }
            save(task);
            return new ResponseEntity<>(new TaskDto(task.getHeader(), task.getDescription(), task.getStatus(), task.getPriority(),
                    task.getExecutors().stream().map(User::getNickname).toList(), task.getAuthor().getNickname(), task.getComments().stream().map(Comment::toString).toList()), HttpStatus.OK);
        } catch (NoSuchElementException noSuchElementException)
        {
            return new ResponseEntity<>(new AppError(noSuchElementException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @param taskHeader Requested task string
     * @param dto Requested dto data object
     * @return ResponseEntity<T>
     */
    @Transactional
    public ResponseEntity<?> deleteExecutors(String taskHeader, ExecutorNamesDTO dto)
    {
        try{
            Task task = findByHeader(taskHeader).orElseThrow(() -> new NoSuchElementException(String.format("Task with such header '%s' doesnt exist", taskHeader)));
            List<User> executors = task.getExecutors();
            dto.getExecutorNames()
                    .stream()
                    .filter(name -> userService.findByNickname(name).isPresent())
                    .map(name -> userService.findByNickname(name).get())
                    .toList()
                    .forEach(executors::remove);
            task.setExecutors(executors);
            if (executors.isEmpty())
            {
                task.setStatus("WAITING");
            }
            save(task);
            return new ResponseEntity<>(new TaskDto(task.getHeader(), task.getDescription(), task.getStatus(), task.getPriority(),
                    task.getExecutors().stream().map(User::getNickname).toList(), task.getAuthor().getNickname(), task.getComments().stream().map(Comment::toString).toList()), HttpStatus.OK);
        } catch (NoSuchElementException noSuchElementException)
        {
            return new ResponseEntity<>(new AppError(noSuchElementException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     *
     * @param taskHeader Requested task string
     * @return ResponseEntity<T>
     */
    @Transactional
    public ResponseEntity<?> deleteTask(String taskHeader) {
        try {
            Task task = findByHeader(taskHeader).orElseThrow();
            if (task.getStatus().equals("WAITING") || task.getStatus().equals("CLOSED")) {
                taskRepository.delete(task);
                return new ResponseEntity<>(new OkResponse(String.format("Task '%s' has been removed", taskHeader), HttpStatus.OK), HttpStatus.OK);
            }
            return new ResponseEntity<>(
                    new AppError(400,
                            String.format("You cant delete task '%s', status of task is %s", taskHeader, task.getStatus())), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(new AppError(400, "No such task!"), HttpStatus.BAD_REQUEST);
        }
    }
}
