package com.example.tms.services;

import com.example.tms.dtos.TaskDto;
import com.example.tms.exceptions.AppError;
import com.example.tms.repository.TaskRepository;
import com.example.tms.repository.entities.Comment;
import com.example.tms.repository.entities.Task;
import com.example.tms.repository.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
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

    Optional<Task> findByExecutor(User executor){
        return taskRepository.findByExecutor(executor);
    }

    Iterable<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    void save(Task task) {
        taskRepository.save(task);
    }

    // Просмотр своих задач
    public String showMyTasksToDo(Principal principal) {
        User user = userService.findByLogin(principal.getName()).get();
        if (user.getTasksToManage().isEmpty()) {
            return "No tasks to do, chill :)";
        }
        return "Tasks to do: " + user.getTasksToExecute().stream().map(Task::toString).toList()
                + "\n Tasks to manage: " + user.getTasksToManage().stream().map(Task::toString).toList();
    }

    // Просмотр чужих задач
    // по никнейму пользователя
    public String showAllTasksOfUser(String nickname) {
        try {
            User user = userService.findByNickname(nickname).orElseThrow(() -> new NoSuchElementException(
                    String.format("User with nickname '%s' doesnt exist", nickname)
            ));
            return "Tasks to do: " + user.getTasksToExecute().stream().map(Task::toString).toList()
                    + "\n Tasks to manage: " + user.getTasksToManage().stream().map(Task::toString).toList();
        } catch (NoSuchElementException e) {
            return e.getMessage();
        }
    }

    public ResponseEntity<?> changeTaskStatus(Principal principal, String taskHeader, String status) {
        try {
            User user = userService.findByLogin(principal.getName()).orElseThrow(() ->
                    new UsernameNotFoundException(String.format("No user with login '%s'", principal.getName())));
            if ((user.getTasksToExecute().contains(findByHeader(taskHeader).orElseThrow())
                    | user.getTasksToManage().contains(findByHeader(taskHeader).orElseThrow()))
                    &&
                    !findByHeader(taskHeader).orElseThrow().getStatus().equals(Task.Status.CLOSED)) {
                try {
                    Task task = findByHeader(taskHeader).orElseThrow(() ->
                            new NoSuchElementException("No such task!"));
                    try {
                        task.setStatus(Task.Status.valueOf(status));
                        task.getExecutors().clear();
                        user.getTasksToManage().remove(task);
                        task.setAuthor(null);
                        save(task);
                    } catch (IllegalArgumentException e) {
                        return new ResponseEntity<>(new AppError("No such status!\nAvailable: IN_PROGRESS, WAITING, CLOSED"), HttpStatus.BAD_REQUEST);
                    }
                } catch (NoSuchElementException noSuchElementException) {
                    return new ResponseEntity<>(new AppError(noSuchElementException.getMessage()), HttpStatus.BAD_REQUEST);
                }
            } else
                return new ResponseEntity<>(new AppError("You dont have permission to edit status!"), HttpStatus.METHOD_NOT_ALLOWED);
        } catch (UsernameNotFoundException notFoundException) {
            return new ResponseEntity<>(new AppError(notFoundException.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new AppError("Unknown response error!"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<?> addComment(Principal principal, String taskHeader, String text) {
        try {
            User user = userService.findByLogin(principal.getName()).orElseThrow();
            if ((user.getTasksToExecute().contains(findByHeader(taskHeader).orElseThrow())
                    | user.getTasksToManage().contains(findByHeader(taskHeader).orElseThrow()))
                && !findByHeader(taskHeader).orElseThrow().getStatus().equals(Task.Status.CLOSED)) {
                Task task = findByHeader(taskHeader).orElseThrow();
                task.getComments().add(new Comment(user, text));
                save(task);
            }
            return new ResponseEntity<>(new AppError("You dont have permission to add comments to this task!"), HttpStatus.METHOD_NOT_ALLOWED);
        } catch (NoSuchElementException noSuchElementException) {
            return new ResponseEntity<>(new AppError(noSuchElementException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> createTask(Principal principal, TaskDto taskDto) {
        try {
            User user = userService.findByLogin(principal.getName()).orElseThrow();
            List<Task> tasks = new ArrayList();
            getAllTasks().forEach(tasks::add);
            boolean ifTaskExists = tasks.stream().anyMatch(task -> task.getHeader().equals(taskDto.getHeader()));
            if (ifTaskExists) {
                Task foundTask = tasks
                        .stream()
                        .filter(task -> task.getHeader().equals(taskDto.getHeader()))
                        .toList()
                        .get(0);
                return new ResponseEntity<>(new AppError(String.format("Task with header '%s' already exists: \n" +
                        "%s", taskDto.getHeader(), foundTask.toString())), HttpStatus.BAD_REQUEST);
            } else {
                List<User> executors = new ArrayList<>();
                executors.addAll(taskDto.getExecutorNicknames()
                        .stream()
                        .filter(nickname -> userService.findByNickname(nickname).isPresent())
                        .map(nickname -> userService.findByNickname(nickname).get())
                        .toList());
                try {
                    Task task = new Task(taskDto.getHeader(), taskDto.getDescription(), Task.Status.CREATED, Task.Priority.valueOf(taskDto.getPriority()), executors, user);
                    save(task);
                    return ResponseEntity.ok(new TaskDto(task.getHeader(), task.getDescription(), task.getStatus().toString(),
                            task.getPriority().toString(), task.getExecutors().stream().map(User::toString).toList(), task.getAuthor()));
                } catch (IllegalArgumentException e) {
                    return new ResponseEntity<>(new AppError(String.format("Priority '%s' doesnt exist", taskDto.getPriority())), HttpStatus.BAD_REQUEST);
                }
            }
        } catch (NoSuchElementException noSuchElementException) {
            return new ResponseEntity<>(new AppError("No such user :("), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> changePriority(String taskHeader, String priority)
    {
        Task task = findByHeader(taskHeader).orElseThrow(() -> new NoSuchElementException(String.format("Task with such header '%s' doesnt exist", taskHeader)));
        try {
            task.setPriority(Task.Priority.valueOf(priority));
            save(task);
            return ResponseEntity.ok(new TaskDto(task.getHeader(), task.getDescription(), task.getStatus().toString(),
                    task.getPriority().toString(), task.getExecutors().stream().map(User::toString).toList(), task.getAuthor()));
        } catch (IllegalArgumentException illegalArgumentException)
        {
            return new ResponseEntity<>(new AppError(illegalArgumentException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> changeDescription(String taskHeader, String description)
    {
        Task task = findByHeader(taskHeader).orElseThrow(() -> new NoSuchElementException(String.format("Task with such header '%s' doesnt exist", taskHeader)));
        try{
            task.setDescription(description);
            save(task);
            return ResponseEntity.ok(new TaskDto(task.getHeader(), task.getDescription(), task.getStatus().toString(),
                    task.getPriority().toString(), task.getExecutors().stream().map(User::toString).toList(), task.getAuthor()));
        } catch (IllegalArgumentException illegalArgumentException)
        {
            return new ResponseEntity<>(new AppError(illegalArgumentException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
