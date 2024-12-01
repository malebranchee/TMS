package com.example.tms;

import com.example.tms.dtos.*;
import com.example.tms.exceptions.AppError;
import com.example.tms.exceptions.OkResponse;
import com.example.tms.repository.RoleRepository;
import com.example.tms.repository.TaskRepository;
import com.example.tms.repository.UserRepository;
import com.example.tms.repository.entities.Task;
import com.example.tms.repository.entities.User;
import com.example.tms.services.RoleService;
import com.example.tms.services.TaskService;
import com.example.tms.services.UserService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Headers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;



// Выполнение тестов подразумевает под собой пустую базу данных.
// Таблицы БД создаются после старта сервера из бина DataBaseUtilConfig
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthorizationTests {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationTests.class);
    @Autowired
    TestRestTemplate testRestTemplate;
    // malebranche - nickname of test user entity USER
    private static String tokenUser_malebranche;
    // pablo - nickname of test user entity ADMIN
    private static String tokenAdmin_pablo;
    private static String tokenUser_zero;

    @Mock
    RoleRepository repository;

    @Mock
    TaskRepository taskRepository;

    @Autowired
    @InjectMocks
    RoleService roleService;

    @Autowired
    @InjectMocks
    UserService userService;

    private HttpHeaders setHeader(String token)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // REG NEW USER
    @Order(1)
    @Test
    public void RegistrationTest_shouldReturnOnExistsUserAuthorization_200() {
        ResponseEntity<UserDto> response = testRestTemplate.postForEntity(
                "/api/v1/registration",
                new RegistrationUserDto(
                        "malebranche@mail.ru",
                        "malebranche",
                        "123",
                        "123"),
                UserDto.class);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    // malebranche user
    @Order(2)
    @Test
    public void AuthenticationTest_shouldReturnOkAndJwtTokensResponse_200()
    {
        ResponseEntity<JwtResponse> response = testRestTemplate.postForEntity(
                "/api/v1/auth",
                new JwtRequest(
                        "malebranche@mail.ru",
                        "123"
                ), JwtResponse.class);
        tokenUser_malebranche = response.getBody().getAccess_token();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // pablo ADMIN
    @Order(3)
    @Test
    public void RegistrationTest_regNewUser_201()
    {
        ResponseEntity<UserDto> response = testRestTemplate.postForEntity(
                "/api/v1/registration",
                new RegistrationUserDto(
                        "paho@mail.ru",
                        "pablo",
                        "123",
                        "123"),
                UserDto.class);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    // ADMIN
    @Order(4)
    @Test
    public void AuthenticationTest_initAdminUser_200()
    {
        ResponseEntity<JwtResponse> response = testRestTemplate.postForEntity(
                "/api/v1/auth",
                new JwtRequest(
                        "paho@mail.ru",
                        "123"
                ), JwtResponse.class);
        tokenAdmin_pablo = response.getBody().getAccess_token();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }




    @Order(5)
    @Test
    public void AdminController_initAdminAuthoritiesToPablo()
    {
        User user = userService.findByNickname("pablo").orElseThrow();
        user.addRole(roleService.findByName("ROLE_ADMIN").orElseThrow());
        userService.save(user);
    }

    @Order(6)
    @Test
    public void AdminController_createTask_201()
    {
        List<String> l = new ArrayList<>();
        l.add("malebranche");
        List<String> c = new ArrayList<>();
        TaskDto dto = new TaskDto("header", "description", "CREATED", "HIGH", l, "pablo", c );
        HttpEntity<TaskDto> request = new HttpEntity<>(dto, setHeader(tokenAdmin_pablo));

        ResponseEntity<TaskDto> response = testRestTemplate
                .exchange("/api/v1/panel/admin/tasks/create",
                        HttpMethod.POST,
                        request,
                        TaskDto.class);

        Assertions.assertEquals(request.getBody(), response.getBody());

        dto = new TaskDto("Deploy", "This is description", "WAITING", "LOW", l, "", c );
        request = new HttpEntity<>(dto, setHeader(tokenAdmin_pablo));
        ResponseEntity<TaskDto> response2 = testRestTemplate
                .exchange("/api/v1/panel/admin/tasks/create",
                        HttpMethod.POST,
                        request,
                        TaskDto.class);

    }

    @Order(7)
    @Test
    public void UserController_getAllMyTasksShouldReturnTasks_200()
    {
        HttpEntity<String> request = new HttpEntity<>(setHeader(tokenUser_malebranche));
        ResponseEntity<OkResponse> response = testRestTemplate
                .exchange("/api/v1/panel/tasks/get/my",
                        HttpMethod.GET,
                        request,
                        OkResponse.class);
        log.info(response.getBody().getMessage());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Order(8)
    @Test
    public void UserController_getAllTasksOfUserPabloByMalebranche_200()
    {
        HttpEntity<String> request = new HttpEntity<>(setHeader(tokenUser_malebranche));
        ResponseEntity<OkResponse> response = testRestTemplate
                .exchange("/api/v1/panel/tasks/get/of/pablo?page=0&size=3",
                        HttpMethod.GET,
                        request,
                        OkResponse.class);
        log.info(response.getBody().getMessage());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Order(9)
    @Test
    public void AdminController_createTaskWithExistingHeader_400()
    {
        List<String> l = new ArrayList<>();
        l.add("malebranche");
        List<String> c = new ArrayList<>();
        TaskDto dto = new TaskDto("header", "description", "CREATED", "HIGH", l, "", c);
        HttpEntity<TaskDto> request = new HttpEntity<>(dto, setHeader(tokenAdmin_pablo));

        ResponseEntity<AppError> response = testRestTemplate
                .exchange("/api/v1/panel/admin/tasks/create",
                        HttpMethod.POST,
                        request,
                        AppError.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        Assertions.assertTrue(response.getBody().getMessage().contains("already exists"));
    }


    @Order(10)
    @Test
    public void UserController_getAllTasksOfUser_200()
    {
        HttpEntity<OkResponse> request = new HttpEntity<>(setHeader(tokenUser_malebranche));
        ResponseEntity<OkResponse> response = testRestTemplate
                .exchange("/api/v1/panel/tasks/get/my",
                        HttpMethod.GET,
                        request,
                        OkResponse.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        log.info(response.getBody().getMessage());
    }

    @Order(11)
    @Test
    public void UserController_changeAvailableTaskStatus_200()
    {
        ChangeTaskStatusDTO dto = new ChangeTaskStatusDTO("IN_PROGRESS");

        HttpEntity<ChangeTaskStatusDTO> request = new HttpEntity<>(dto, setHeader(tokenUser_malebranche));

        ResponseEntity<OkResponse> response = testRestTemplate
                .exchange("/api/v1/panel/task/header/change/status",
                        HttpMethod.PUT,
                        request,
                        OkResponse.class);
        log.info(response.getBody().getMessage());
        Assertions.assertEquals(HttpStatus.OK, response.getBody().getStatus());
    }

    @Order(12)
    @Test
    public void AdminController_changeAvailableTaskPriority_200()
    {
        ChangeTaskPriorityDTO dto = new ChangeTaskPriorityDTO("LOW");
        dto.setPriority("LOW");
        HttpEntity<ChangeTaskPriorityDTO> request = new HttpEntity<>(dto, setHeader(tokenAdmin_pablo));

        ResponseEntity<TaskDto> response = testRestTemplate
                .exchange("/api/v1/panel/admin/task/header/change/priority",
                        HttpMethod.PUT,
                        request,
                        TaskDto.class);
        log.info(response.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Order(13)
    @Test
    public void AdminController_changeAvailableTaskPriorityWithWrongPriority_400()
    {
        ChangeTaskPriorityDTO dto = new ChangeTaskPriorityDTO("Normalno");
        HttpEntity<ChangeTaskPriorityDTO> request = new HttpEntity<>(dto, setHeader(tokenAdmin_pablo));

        ResponseEntity<AppError> response = testRestTemplate
                .exchange("/api/v1/panel/admin/task/header/change/priority",
                        HttpMethod.PUT,
                        request,
                        AppError.class);
        log.info(response.getBody().toString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST  , response.getStatusCode());
    }

    @Order(14)
    @Test
    public void AdminController_changeAvailableTaskDescription_200()
    {
        ChangeTaskDescriptionDTO dto = new ChangeTaskDescriptionDTO("Ny vot takaya zadacha yoooo");
        HttpEntity<ChangeTaskDescriptionDTO> request = new HttpEntity<>(dto, setHeader(tokenAdmin_pablo));

        ResponseEntity<TaskDto> response = testRestTemplate
                .exchange("/api/v1/panel/admin/task/header/change/description",
                        HttpMethod.PUT,
                        request,
                        TaskDto.class);
        log.info(response.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Order(15)
    @Test
    public void AdminController_regNewUserZero_201()
    {
        ResponseEntity<UserDto> response = testRestTemplate.postForEntity(
                "/api/v1/registration",
                new RegistrationUserDto(
                        "zero@mail.ru",
                        "zero",
                        "123",
                        "123"),
                UserDto.class);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    // ADMIN
    @Order(16)
    @Test
    public void AuthenticationTest_initUserZero_200() {
        ResponseEntity<JwtResponse> response = testRestTemplate.postForEntity(
                "/api/v1/auth",
                new JwtRequest(
                        "zero@mail.ru",
                        "123"
                ), JwtResponse.class);
        tokenUser_zero = response.getBody().getAccess_token();
    }

    @Order(17)
    @Test
    public void AdminController_addExecutorsToTask_200()
    {
        List<String> executors = new ArrayList<>();
        executors.add("zero");
        ExecutorNamesDTO dto = new ExecutorNamesDTO(executors);
        HttpEntity<ExecutorNamesDTO> request = new HttpEntity<>(dto, setHeader(tokenAdmin_pablo));

        ResponseEntity<TaskDto> response = testRestTemplate
                .exchange("/api/v1/panel/admin/task/header/add/executors",
                        HttpMethod.PUT,
                        request,
                        TaskDto.class);
        log.info(response.getBody().toString());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Order(18)
    @Test
    public void UserController_addCommentOnTask_201()
    {
        CommentDto commentDto = new CommentDto("Good job)");
        HttpEntity<CommentDto> request = new HttpEntity<>(commentDto, setHeader(tokenAdmin_pablo));

        ResponseEntity<OkResponse> response = testRestTemplate
                .exchange("/api/v1/panel/task/header/add/comment",
                        HttpMethod.POST,
                        request,
                        OkResponse.class);
        log.info(response.getBody().getMessage());
        Assertions.assertTrue(response.getBody().getMessage().contains(commentDto.getComment()));
    }

    @Order(19)
    @Test
    public void getAllTasksByStatusPaging_200()
    {
        HttpEntity<?> request = new HttpEntity<>(setHeader(tokenAdmin_pablo));
        ResponseEntity<PageableDto> response = testRestTemplate
                .exchange("/api/v1/panel/tasks/get/by/status?status=IN_PROGRESS",
                        HttpMethod.GET,
                        request,
                        PageableDto.class);
        response.getBody().getObjectList().forEach(log::info);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getBody().getObjectList().stream().anyMatch(o -> o.contains("IN_PROGRESS")));

    }


    @Order(20)
    @Test
    public void AdminController_removeExecutorsFromTask_200()
    {
        List<String> executors = new ArrayList<>();
        executors.add("zero");
        executors.add("pablo");
        ExecutorNamesDTO dto = new ExecutorNamesDTO(executors);
        HttpEntity<ExecutorNamesDTO> request = new HttpEntity<>(dto, setHeader(tokenAdmin_pablo));

        ResponseEntity<TaskDto> response = testRestTemplate
                .exchange("/api/v1/panel/admin/task/header/remove/executors",
                        HttpMethod.DELETE,
                        request,
                        TaskDto.class);
        log.info(response.getBody().toString());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Order(21)
    @Test
    public void getAllTasksByPriorityPaging_page()
    {
        HttpEntity<?> request = new HttpEntity<>(setHeader(tokenAdmin_pablo));
        ResponseEntity<PageableDto> response = testRestTemplate
                .exchange("/api/v1/panel/tasks/get/by/priority?priority=LOW",
                        HttpMethod.GET,
                        request,
                        PageableDto.class);
        response.getBody().getObjectList().forEach(log::info);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getBody().getObjectList().stream().anyMatch(o -> o.contains("LOW")));
    }
}
