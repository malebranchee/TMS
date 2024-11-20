package com.example.tms.services;

import com.example.tms.dtos.RegistrationUserDto;
import com.example.tms.exceptions.AppError;
import com.example.tms.repository.UserRepository;
import com.example.tms.repository.entities.Role;
import com.example.tms.repository.entities.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final MailService mailService;

    public Optional<User> findByLogin(String username)
    {
        return userRepository.findByLogin(username);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public User save(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setLogin(registrationUserDto.getLogin());
        user.setPassword(registrationUserDto.getPassword());
        user.addRole(roleService.findByName("ROLE_UNCONFIRMED").orElseThrow());
        mailService.sendMail(user.getLogin(),
                "Confirmation of given email address",
                "Follow this link to confirm your email address:\n" +
                        "\thttp://localhost:8080/api/confirmed/" + user.getLogin());
        return userRepository.save(user);
    }

    @Transactional
    public void addRole(String roleName, String login) {
        Role role = roleService.findByName(roleName).orElseThrow();
        User user = findByLogin(login).orElseThrow();
        user.addRole(role);
        save(user);
    }

    @Transactional
    public void deleteRole(String roleName, String login)
    {
        Role role = roleService.findByName(roleName).orElseThrow();
        User user = findByLogin(login).orElseThrow();
        user.deleteRole(role);
        save(user);
    }

    public boolean ifUserNotExists(String login)
    {
        try{
            User user = findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("No such user!"));
            return false;
        } catch (UsernameNotFoundException e)
        {
            return true;
        }
    }

    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByLogin(username).orElseThrow(() ->
                new UsernameNotFoundException(
                        String.format("User '%s' not found", username)
                ));
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                user.getRoles().stream().map(role ->
                                new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())

        );
    }





}
