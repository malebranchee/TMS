package com.example.tms;

import com.example.tms.controllers.AuthorizationController;
import com.example.tms.dtos.RegistrationUserDto;
import com.example.tms.dtos.UserDto;
import com.example.tms.services.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;



@ContextConfiguration(classes = TmsApplicationTests.class)
@RestClientTest(AuthorizationController.class)
class TmsApplicationTests
{

    @Test
    public void shouldReturnGoodResponse()
    {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<RegistrationUserDto> request = new HttpEntity<>(new RegistrationUserDto("pablo", "123", "123"));
        ResponseEntity<?> response = restTemplate.postForEntity("http://localhost:8080/registration", request, UserDto.class);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);

/*
        UserDto userDto = (UserDto)response.getBody();

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(userDto.getLogin(), "pablo");
*/

    }
}
