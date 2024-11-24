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
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final RoleService roleService;
    private final DaoAuthenticationProvider daoAuthenticationProvider;

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

    public String showMyTasksToDo(Principal principal) {

        User user = userService.findByLogin(principal.getName()).orElseThrow(
                () -> new UsernameNotFoundException("No such user!"));
        if (user.getTasksToManage().isEmpty()) {
            return "No tasks to do, chill :)";
        }
        return "Tasks to do: " + user.getTasksToExecute().stream().map(Task::toString).toList()
                + "\n Tasks to manage: " + user.getTasksToManage().stream().map(Task::toString).toList();
    }

    public String showAllTasksOfUser(String nickname) {

            User user = userService.findByNickname(nickname).orElseThrow(() -> new NoSuchElementException(
                    String.format("User with nickname '%s' doesnt exist", nickname)
            ));
            return "Tasks to do: " + user.getTasksToExecute().stream().map(Task::toString).toList()
                    + "\n Tasks to manage: " + user.getTasksToManage().stream().map(Task::toString).toList();

    }

    public ResponseEntity<?> changeTaskStatus(Principal principal, String taskHeader, String status) {
        try {
            User user = userService.findByLogin(principal.getName()).orElseThrow(() ->
                    new UsernameNotFoundException(String.format("No user with login '%s'", principal.getName())));
            if ((user.getTasksToExecute().contains(findByHeader(taskHeader).orElseThrow())
                    | user.getTasksToManage().contains(findByHeader(taskHeader).orElseThrow())
                    | user.getRoles().contains(roleService.findByName("ROLE_ADMIN").orElseThrow()))
                    &&
                    !findByHeader(taskHeader).orElseThrow().getStatus().equals(Task.Status.CLOSED.toString())) {
                try {
                    Task task = findByHeader(taskHeader).orElseThrow(() ->
                            new NoSuchElementException("No such task!"));
                    try {
                        task.setStatus(status);
                        task.getExecutors().clear();
                        user.getTasksToManage().remove(task);
                        task.setAuthor(null);
                        save(task);
                    } catch (IllegalArgumentException e) {
                        return new ResponseEntity<>(new AppError(400, "No such status!\nAvailable: IN_PROGRESS, WAITING, CLOSED"), HttpStatus.BAD_REQUEST);
                    }
                } catch (NoSuchElementException noSuchElementException) {
                    return new ResponseEntity<>(new AppError(400, noSuchElementException.getMessage()), HttpStatus.BAD_REQUEST);
                }
            } else
                return new ResponseEntity<>(new AppError(405, "You dont have permission to edit status!"), HttpStatus.METHOD_NOT_ALLOWED);
        } catch (UsernameNotFoundException notFoundException) {
            return new ResponseEntity<>(new AppError(400, notFoundException.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new AppError("Unknown response error!"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<?> addComment(Principal principal, String taskHeader, String text) {
        try {
            User user = userService.findByLogin(principal.getName()).orElseThrow();
            if ((user.getTasksToExecute().contains(findByHeader(taskHeader).orElseThrow())
                    | user.getTasksToManage().contains(findByHeader(taskHeader).orElseThrow())
                    | user.getRoles().contains(roleService.findByName("ROLE_ADMIN").orElseThrow()))
                && !findByHeader(taskHeader).orElseThrow().getStatus().equals(Task.Status.CLOSED.toString())) {
                Task task = findByHeader(taskHeader).orElseThrow();
                task.getComments().add(new Comment(user, text));
                save(task);
            }
            return new ResponseEntity<>(new AppError(405, "You dont have permission to add comments to this task!"), HttpStatus.METHOD_NOT_ALLOWED);
        } catch (NoSuchElementException noSuchElementException) {
            return new ResponseEntity<>(new AppError(400, noSuchElementException.getMessage()), HttpStatus.BAD_REQUEST);
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
                    return ResponseEntity.ok(new TaskDto(task.getHeader(), task.getDescription(), task.getStatus(),
                            task.getPriority(), task.getExecutors().stream().map(User::toString).toList(), task.getAuthor()));
                } catch (IllegalArgumentException e) {
                    return new ResponseEntity<>(new AppError(400, String.format("Priority '%s' doesnt exist", taskDto.getPriority())), HttpStatus.BAD_REQUEST);
                }
            }
        } catch (NoSuchElementException noSuchElementException) {
            return new ResponseEntity<>(new AppError(400,"No such user :("), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> changePriority(String taskHeader, String priority)
    {
        Task task = findByHeader(taskHeader).orElseThrow(() -> new NoSuchElementException(String.format("Task with such header '%s' doesnt exist", taskHeader)));
        try {
            task.setPriority(priority);
            save(task);
            return ResponseEntity.ok(new TaskDto(task.getHeader(), task.getDescription(), task.getStatus(),
                    task.getPriority(), task.getExecutors().stream().map(User::toString).toList(), task.getAuthor()));
        } catch (IllegalArgumentException illegalArgumentException)
        {
            return new ResponseEntity<>(new AppError(400, "No such priority. Available: HIGH, MEDIUM, LOW.\n"), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> changeDescription(String taskHeader, String description)
    {
        Task task = findByHeader(taskHeader).orElseThrow(() -> new NoSuchElementException(String.format("Task with such header '%s' doesnt exist", taskHeader)));
        try{
            task.setDescription(description);
            save(task);
            return ResponseEntity.ok(new TaskDto(task.getHeader(), task.getDescription(), task.getStatus(),
                    task.getPriority(), task.getExecutors().stream().map(User::toString).toList(), task.getAuthor()));
        } catch (IllegalArgumentException illegalArgumentException)
        {
            return new ResponseEntity<>(new AppError(illegalArgumentException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> addExecutors(String taskHeader, List<String> executorNames)
    {
        try{
            Task task = findByHeader(taskHeader).orElseThrow(() -> new NoSuchElementException(String.format("Task with such header '%s' doesnt exist", taskHeader)));
            List<User> executors = task.getExecutors();
            executors.addAll(executorNames
                    .stream()
                    .filter(name -> userService.findByNickname(name).isPresent())
                    .map(name -> userService.findByNickname(name).get())
                    .toList());
            task.setExecutors(executors);
            save(task);
            return ResponseEntity.ok(new TaskDto(task.getHeader(), task.getDescription(), task.getStatus(),
                    task.getPriority(), task.getExecutors().stream().map(User::toString).toList(), task.getAuthor()));
        } catch (NoSuchElementException noSuchElementException)
        {
            return new ResponseEntity<>(new AppError(noSuchElementException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> deleteExecutors(String taskHeader, List<String> executorNames)
    {
        try{
            Task task = findByHeader(taskHeader).orElseThrow(() -> new NoSuchElementException(String.format("Task with such header '%s' doesnt exist", taskHeader)));
            List<User> executors = task.getExecutors();
            executorNames
                    .stream()
                    .filter(name -> userService.findByNickname(name).isPresent())
                    .map(name -> userService.findByNickname(name).get())
                    .toList()
                    .forEach(executors::remove);
            task.setExecutors(executors);
            save(task);
            return ResponseEntity.ok(new TaskDto(task.getHeader(), task.getDescription(), task.getStatus(),
                    task.getPriority(), task.getExecutors().stream().map(User::toString).toList(), task.getAuthor()));
        } catch (NoSuchElementException noSuchElementException)
        {
            return new ResponseEntity<>(new AppError(noSuchElementException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
