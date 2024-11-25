package com.example.tms;

import com.example.tms.controllers.AuthenticationController;
import com.example.tms.controllers.ErrorController;
import com.example.tms.dtos.*;
import com.example.tms.exceptions.AppError;
import com.example.tms.exceptions.EmailValidator;
import com.example.tms.repository.UserRepository;
import com.example.tms.services.AuthService;
import com.example.tms.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthorizationTests {

    @Autowired
    TestRestTemplate restTemplate;


    @Order(1)
    @Test
    public void RegistrationTest_shouldReturnCREATEDonCreatingNotExistedUser(){
        ResponseEntity<UserDto> response = restTemplate.postForEntity(
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
    public void RegistrationTest_shouldReturnBADREQUESTOnExistsUserAuthorization() {
        ResponseEntity<AppError> response = restTemplate.postForEntity(
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
    public void AuthenticationTest_shouldReturnOkAndJwtTokensResponse()
    {
        ResponseEntity<JwtResponse> response = restTemplate.postForEntity(
                "/api/v1/auth",
                new JwtRequest(
                        "paho@mail.ru",
                        "123"
                ), JwtResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Order(4)
    @Test
    public void AuthenticationTest_shouldReturn401OnBadCredentials() throws NullPointerException
    {
        ResponseEntity<AppError> response = restTemplate.postForEntity(
                "/api/v1/auth",
                new JwtRequest(
                        "paho@mail.ru",
                        "12"
                ), AppError.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertEquals("Wrong login or password", response.getBody().getMessage());
    }



}
