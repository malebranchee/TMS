package com.example.tms;

import com.example.tms.dtos.*;
import com.example.tms.exceptions.AppError;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthorizationTests {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationTests.class);
    @Autowired
    TestRestTemplate testRestTemplate;



    private String tokenUser;

    private String tokenAdmin;

    private HttpHeaders setHeader(String token)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    @Order(1)
    @Test
    public void RegistrationTest_shouldReturnOnCreatingNotExistedUser_201(){

        ResponseEntity<UserDto> response = testRestTemplate.postForEntity(
                "/api/v1/registration",
                new RegistrationUserDto(
                        "paho@mail.ru",
                        "pablo",
                        "123",
                        "123"),
                UserDto.class);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("pablo", response.getBody().getNickname());
    }

    @Order(2)
    @Test
    public void RegistrationTest_shouldReturnOnExistsUserAuthorization_400() {
        ResponseEntity<AppError> response = testRestTemplate.postForEntity(
                "/api/v1/registration",
                new RegistrationUserDto(
                        "paho@mail.ru",
                        "pablo",
                        "123",
                        "123"),
                AppError.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Order(3)
    @Test
    public void AuthenticationTest_shouldReturnOkAndJwtTokensResponse_200()
    {
        ResponseEntity<JwtResponse> response = testRestTemplate.postForEntity(
                "/api/v1/auth",
                new JwtRequest(
                        "paho@mail.ru",
                        "123"
                ), JwtResponse.class);
        tokenUser = response.getBody().getToken();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Order(4)
    @Test
    public void UserController_getAllMyTasks_200()
    {

        HttpEntity<String> request = new HttpEntity<>(setHeader(tokenUser));

        ResponseEntity<String> response = testRestTemplate
                .exchange("/api/v1/panel/get/tasks/my", HttpMethod.GET, request, String.class);
        Assertions.assertEquals("No tasks to do, chill :)", response.getBody());
    }


}
