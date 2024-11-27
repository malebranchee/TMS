package com.example.tms;

import com.example.tms.dtos.*;
import com.example.tms.exceptions.AppError;
import com.example.tms.exceptions.OkResponse;
import com.example.tms.repository.RoleRepository;
import com.example.tms.repository.TaskRepository;
import com.example.tms.repository.UserRepository;
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
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


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


    @Autowired
    @InjectMocks
    TaskService taskService;

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
    public void UserController_getAllMyTasksByMalebrancheShouldReturnNoTasks_200()
    {
        HttpEntity<String> request = new HttpEntity<>(setHeader(tokenUser_malebranche));

        ResponseEntity<OkResponse> response = testRestTemplate
                .exchange("/api/v1/panel/get/tasks/my", HttpMethod.GET, request, OkResponse.class);
        Assertions.assertEquals("No tasks to do, chill :)", response.getBody().getMessage());
    }

    @Order(6)
    @Test
    public void UserController_getAllTasksOfUserPabloByMalebranche_200()
    {
        HttpEntity<String> request = new HttpEntity<>(setHeader(tokenUser_malebranche));
        ResponseEntity<OkResponse> response = testRestTemplate
                .exchange("/api/v1/panel/get/tasks/of/pablo",
                        HttpMethod.GET,
                        request,
                        OkResponse.class);
        Assertions.assertEquals(response.getBody().getStatus(), HttpStatus.OK);

    }


    @Order(7)
    @Test
    public void AdminController_initAdminAuthoritiesToPablo()
    {
        User user = userService.findByNickname("pablo").orElseThrow();
        user.addRole(roleService.findByName("ROLE_ADMIN").orElseThrow());
        userService.save(user);
    }

    @Order(8)
    @Test
    public void AdminController_createTask_201()
    {
        List<String> l = new ArrayList<>();
        l.add("malebranche");
        TaskDto dto = new TaskDto("header", "description", "HIGH", l);
        HttpEntity<TaskDto> request = new HttpEntity<>(dto, setHeader(tokenAdmin_pablo));

        ResponseEntity<TaskDto> response = testRestTemplate
                .exchange("/api/v1/panel/admin/tasks/create",
                        HttpMethod.POST,
                        request,
                        TaskDto.class);

        Assertions.assertEquals(request.getBody(), response.getBody());
    }

    @Order(9)
    @Test
    public void AdminController_createTaskWithExistingHeader_400()
    {
        List<String> l = new ArrayList<>();
        l.add("malebranche");
        TaskDto dto = new TaskDto("header", "description", "HIGH", l);
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
                .exchange("/api/v1/panel/get/tasks/my",
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
        HttpHeaders headers = setHeader(tokenUser_malebranche);

        HttpEntity<?> request = new HttpEntity<>(setHeader(tokenUser_malebranche));

        String taskHeader = "Deploy";
        String status = "CLOSED";

        ResponseEntity<OkResponse> response = testRestTemplate
                .exchange("/api/v1/panel/task/Deploy/change/status/taskHeader=Deploy&status=CLOSED",
                        HttpMethod.PUT,
                        request,
                        OkResponse.class);
        Assertions.assertEquals(HttpStatus.OK, response.getBody().getStatus());
    }



}
