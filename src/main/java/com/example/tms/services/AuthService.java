package com.example.tms.services;

import com.example.tms.dtos.*;
import com.example.tms.exceptions.AppError;
import com.example.tms.repository.entities.User;
import com.example.tms.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserService userService;
    private final MailService mailService;
    private final JwtUtils jwtUtils;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    // Расписать сервис аутентификации

    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest, HttpServletRequest request) {
        try {
            daoAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                    authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            logger.error("User {} with current data doesn't exists.", authRequest.getUsername());
            AppError unauthorized = new AppError(HttpStatus.UNAUTHORIZED.value(),
                    "Wrong login or password");
            return new ResponseEntity<>(unauthorized, HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtUtils.generateToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), userDetails);
        logger.info("User {} signed in.", userDetails.getUsername());
        return ResponseEntity.ok(new JwtResponse(token, refreshToken));
    }

    public ResponseEntity<?> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        UserDetails userDetails = userService.loadUserByUsername(jwtUtils.getUsername(
                refreshTokenRequest.getToken()
        ));

        if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), userDetails)) {
            var jwt = jwtUtils.generateToken(userService.loadUserByUsername(userDetails.getUsername()));

            return ResponseEntity.ok(new JwtResponse(jwt, refreshTokenRequest.getToken()));
        }
        return new ResponseEntity<>(new AppError("Wrong access!"), HttpStatusCode.valueOf(403));
    }

    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto, HttpServletRequest request) {
        if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                    "Password mismatch"), HttpStatus.BAD_REQUEST);
        }
        if (userService.ifUserNotExists(registrationUserDto.getLogin())) {

            registrationUserDto.setPassword(new BCryptPasswordEncoder()
                    .encode(registrationUserDto.getPassword()));
            User user = userService.save(registrationUserDto);
            logger.info("User {} signed up.", user.getLogin());
            return new ResponseEntity<>(new UserDto(user.getId(), user.getLogin()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new AppError("Such user already exists!"), HttpStatus.BAD_REQUEST);
        }
    }




}
