package com.example.tms.services;

import com.example.tms.dtos.*;
import com.example.tms.exceptions.AppError;
import com.example.tms.exceptions.OkResponse;
import com.example.tms.repository.TaskRepository;
import com.example.tms.repository.entities.Comment;
import com.example.tms.repository.entities.Task;
import com.example.tms.repository.entities.User;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.*;


@Service
@AllArgsConstructor
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    @Autowired
    private final TaskRepository taskRepository;
    @Autowired
    private final UserService userService;
    @Autowired
    private final RoleService roleService;

    Optional<Task> findByHeader(String header) {
        return taskRepository.findByHeader(header);
    }

    Optional<Task> findByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority);
    }

    Optional<Task> findByAuthor(User author) {

        return taskRepository.findByAuthor(author);
    }

    Optional<Task> findByStatus(Task.Status status) {
        return taskRepository.findByStatus(status);
    }

    Optional<Task> findByExecutor(List<User> executors){
        return taskRepository.findByExecutors(executors);
    }

    Iterable<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    void save(Task task) {
        taskRepository.save(task);
    }

    public ResponseEntity<?> showMyTasksToDo(Principal principal) {
        User user = userService.findByLogin(principal.getName()).orElseThrow(
                () -> new UsernameNotFoundException("No such user!"));
        if (user.getTasksToExecute().isEmpty()) {
            return new ResponseEntity<>(new OkResponse("No tasks to do, chill :)", HttpStatus.OK), HttpStatus.OK);
        }
        return new ResponseEntity<>(new OkResponse("Tasks to do: " + user.getTasksToExecute().stream().map(Task::toString).toList()
                + "\n Tasks to manage: " + user.getTasksToManage().stream().map(Task::toString).toList(), HttpStatus.OK), HttpStatus.OK);
    }

    public ResponseEntity<?> showAllTasksOfUser(String nickname) {

            User user = userService.findByNickname(nickname).orElseThrow(() -> new NoSuchElementException(
                    String.format("User with nickname '%s' doesnt exist", nickname)
            ));
            return new ResponseEntity<>(new OkResponse("Tasks to do: " + user.getTasksToExecute().stream().map(Task::toString).toList()
                    + "\n Tasks to manage: " + user.getTasksToManage().stream().map(Task::toString).toList(), HttpStatus.OK), HttpStatus.OK);

    }

    public ResponseEntity<?> changeTaskStatus(Principal principal, String taskHeader, ChangeTaskStatusDTO dto) {
        try {
            User user = userService.findByLogin(principal.getName()).orElseThrow(() ->
                    new UsernameNotFoundException(String.format("No user with login '%s'", principal.getName())));
            Task task = findByHeader(taskHeader).orElseThrow();

            if ((user.getTasksToExecute().contains(task)
                    || user.getTasksToManage().contains(task)
                    || user.getRoles().contains(roleService.findByName("ROLE_ADMIN").orElseThrow()))
                    && Arrays.stream(Task.Status.values())
                    .anyMatch(t -> t.equals(Task.Status.valueOf(dto.getStatus()))))
            {
                        if (dto.getStatus().equals("CLOSED"))
                        {
                            task.setStatus(Task.Status.CLOSED.name());
                            user.getTasksToExecute().remove(task);
                            user.getTasksToManage().remove(task);
                        }
                        else {
                            task.setStatus(dto.getStatus());
                        }
                        save(task);
                        return new ResponseEntity<>(new OkResponse("Status changed to " + dto.getStatus(), HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new AppError(405, "You dont have permission to edit status!"), HttpStatus.METHOD_NOT_ALLOWED);
            }
        } catch (UsernameNotFoundException notFoundException) {
            return new ResponseEntity<>(new AppError(400, notFoundException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> addComment(Principal principal, String taskHeader, CommentDto dto) {
        try {
            User user = userService.findByLogin(principal.getName()).orElseThrow();
            if ((user.getTasksToExecute().contains(findByHeader(taskHeader).orElseThrow())
                    | user.getTasksToManage().contains(findByHeader(taskHeader).orElseThrow())
                    | user.getRoles().contains(roleService.findByName("ROLE_ADMIN").orElseThrow()))
                && !findByHeader(taskHeader).orElseThrow().getStatus().equals(Task.Status.CLOSED.toString())) {
                Task task = findByHeader(taskHeader).orElseThrow();
                task.getComments().add(new Comment(user, dto.getComment()));
                save(task);
                return new ResponseEntity<>(new OkResponse("Created new comment: " + dto.getComment(), HttpStatus.CREATED), HttpStatus.CREATED);
            }
            return new ResponseEntity<>(new AppError(405, "You dont have permission to add comments to this task!"), HttpStatus.METHOD_NOT_ALLOWED);
        } catch (NoSuchElementException noSuchElementException) {
            return new ResponseEntity<>(new AppError(400, noSuchElementException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> createTask(Principal principal, TaskDto taskDto) {
        try {
            User user = userService.findByLogin(principal.getName()).orElseThrow();
            List<Task> tasks = new ArrayList<>();
            getAllTasks().forEach(tasks::add);
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
                    return new ResponseEntity<>(new TaskDto(task.getHeader(), task.getDescription(), task.getPriority(), task.getExecutors().stream().map(User::getNickname).toList()), HttpStatus.CREATED);
                } catch (IllegalArgumentException e) {
                    return new ResponseEntity<>(new AppError(400, String.format("Priority '%s' doesnt exist", taskDto.getPriority())), HttpStatus.BAD_REQUEST);
                }
            }
        } catch (NoSuchElementException noSuchElementException) {
            return new ResponseEntity<>(new AppError(400,"No such user :("), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> changePriority(String taskHeader, ChangeTaskPriorityDTO dto)
    {
        try {
            Task task = findByHeader(taskHeader).orElseThrow(() -> new NoSuchElementException(String.format("Task with such header '%s' doesnt exist", taskHeader)));
            try {
                if (Arrays.stream(Task.Priority.values()).map(Enum::toString).toList().contains(dto.getPriority())) {
                    task.setPriority(dto.getPriority());
                    save(task);
                    return new ResponseEntity<>(new TaskDto(task.getHeader(), task.getDescription(), task.getPriority(), task.getExecutors().stream().map(User::getNickname).toList()), HttpStatus.OK);
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

    public ResponseEntity<?> changeDescription(String taskHeader, ChangeTaskDescriptionDTO dto)
    {
        Task task = findByHeader(taskHeader).orElseThrow(() -> new NoSuchElementException(String.format("Task with such header '%s' doesnt exist", taskHeader)));
        try{
            task.setDescription(dto.getDescription());
            save(task);
            return new ResponseEntity<>(new TaskDto(task.getHeader(), task.getDescription(), task.getPriority(), task.getExecutors().stream().map(User::getNickname).toList()), HttpStatus.OK);
        } catch (IllegalArgumentException illegalArgumentException)
        {
            return new ResponseEntity<>(new AppError(illegalArgumentException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

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
            return new ResponseEntity<>(new TaskDto(task.getHeader(), task.getDescription(), task.getPriority(), task.getExecutors().stream().map(User::getNickname).toList()), HttpStatus.OK);
        } catch (NoSuchElementException noSuchElementException)
        {
            return new ResponseEntity<>(new AppError(noSuchElementException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

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
            return new ResponseEntity<>(new TaskDto(task.getHeader(), task.getDescription(), task.getPriority(), task.getExecutors().stream().map(User::getNickname).toList()), HttpStatus.OK);
        } catch (NoSuchElementException noSuchElementException)
        {
            return new ResponseEntity<>(new AppError(noSuchElementException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
