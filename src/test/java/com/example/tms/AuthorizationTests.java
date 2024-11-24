package com.example.tms;

import com.example.tms.controllers.AuthenticationController;
import com.example.tms.dtos.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@ContextConfiguration(classes = AuthorizationTests.class)
@RestClientTest(AuthenticationController.class)
class AuthorizationTests
{
    private static final Logger log = LoggerFactory.getLogger(AuthorizationTests.class);
    private final RestClient restClient = RestClient.create();

    @Test
    public void RegistrationTest_shouldReturn201onCreatingNotExistedUser()
    {
        String uri = "http://localhost:8080/api/v1/registration";
        RegistrationUserDto registrationUserDto = new RegistrationUserDto("pablo", "pavlik","123", "123");
        ResponseEntity<?> response = restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(registrationUserDto).retrieve().toEntity(UserDto.class);
        HttpEntity<UserDto> user = new HttpEntity(response.getBody(), response.getHeaders());
        Assertions.assertEquals(user.getBody().getLogin(), registrationUserDto.getLogin());
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }

    @Test
    public void RegistrationTest_shouldReturnBadRequestOnExistsUserAuthorization()
    {
        String uri = "http://localhost:8080/api/v1/registration";
        RegistrationUserDto registrationUserDto = new RegistrationUserDto("pablo", "pavlik","123", "123");
        try {
            ResponseEntity<?> response = restClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(registrationUserDto).retrieve().toEntity(UserDto.class);
        } catch (HttpClientErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void AuthenticationTest_shouldReturnOkAndJWT()
    {
        String uri = "http://localhost:8080/api/v1/auth";
        JwtRequest jwtRequest = new JwtRequest("pablo", "123");
        ResponseEntity<?> response = restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jwtRequest).retrieve().toEntity(JwtResponse.class);
        HttpEntity<JwtResponse> jwtResponseHttpEntity = new HttpEntity(response.getBody(), response.getHeaders());
        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(jwtResponseHttpEntity.hasBody());

    }
}
