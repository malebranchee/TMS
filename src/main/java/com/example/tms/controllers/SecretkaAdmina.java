package com.example.tms.controllers;

import com.example.tms.exceptions.AppError;
import com.example.tms.exceptions.OkResponse;
import com.example.tms.repository.entities.User;
import com.example.tms.services.AuthService;
import com.example.tms.services.RoleService;
import com.example.tms.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Secret controller for checking
 * NOT INTENDED FOR PRODUCTION USING!
 * @deprecated Marked for removal in production
 */
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/v1/panel/secreto4ka")
public class SecretkaAdmina {
    @Autowired
    AuthService authService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    /**
     *
     * @param principal Current user credentials
     */
    @Operation(description = "Секреточка для проверки функционала админа." +
            " Для выдачи роли админа нужно быть авторизованым под логином 'lil-dozhd@mail.ru'")
    @PutMapping
    public ResponseEntity<?> initTestAdmin(
            @Parameter(name = "Выдача админ роли юзеру", description = "Сразу же вернет токен для входа.")
            Principal principal)
    {
        if (principal.getName().equals("lil-dozhd@mail.ru"))
        {
            User user = userService.findByLogin(principal.getName()).orElseThrow();
            user.addRole(roleService.findByName("ROLE_ADMIN").orElseThrow());
            userService.save(user);
            return new ResponseEntity<>(new OkResponse("Vse okey, kommandui, bro)", HttpStatus.OK), HttpStatus.OK);
        }
        return new ResponseEntity<>(new AppError(404, "Wrong page!"), HttpStatus.NOT_FOUND);
    }
}
