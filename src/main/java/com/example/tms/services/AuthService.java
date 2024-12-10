package com.example.tms.services;

import com.example.tms.dtos.*;
import com.example.tms.exceptions.AppError;
import com.example.tms.repository.entities.User;
import com.example.tms.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
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

/**
 * @return ResponseEntity<?>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final DaoAuthenticationProvider daoAuthenticationProvider;


    /**
     *
     * @param authRequest  JWT request body
     *
     */
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authRequest.getLogin(),
                    authRequest.getPassword());
            daoAuthenticationProvider.authenticate(token);
        } catch (BadCredentialsException e) {
            AppError unauthorized = new AppError(HttpStatus.UNAUTHORIZED.value(),
                    "Wrong login or password");
            return new ResponseEntity<>(unauthorized, HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getLogin());
        String token = jwtUtils.generateToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), userDetails);
        return ResponseEntity.ok(new JwtResponse(token, refreshToken));
    }

    /**
     *
     * @param refreshTokenRequest Refresh JWT body
     */
    public ResponseEntity<?> refreshToken(RefreshTokenDto refreshTokenRequest) {
        UserDetails userDetails = userService.loadUserByUsername(jwtUtils.getUsername(
                refreshTokenRequest.getToken()
        ));

        if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), userDetails)) {
            var jwt = jwtUtils.generateToken(userService.loadUserByUsername(userDetails.getUsername()));

            return ResponseEntity.ok(new JwtResponse(jwt, refreshTokenRequest.getToken()));
        }
        return new ResponseEntity<>(new AppError("Wrong access!"), HttpStatusCode.valueOf(403));
    }

    /**
     *
     * @param registrationUserDto User registration data class
     */
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                    "Password mismatch"), HttpStatus.BAD_REQUEST);
        }


        if (userService.ifUserNotExists(registrationUserDto.getLogin()) && userService.findByNickname(registrationUserDto.getNickname()).isEmpty()) {

            registrationUserDto.setPassword(new BCryptPasswordEncoder()
                    .encode(registrationUserDto.getPassword()));
            User user = userService.save(registrationUserDto);
            return new ResponseEntity<>(new UserDto(user.getId(), user.getLogin(), user.getNickname()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new AppError("Such user already exists!"), HttpStatus.BAD_REQUEST);
        }
    }

}
