package com.example.tms.services;

import com.example.tms.dtos.RegistrationUserDto;
import com.example.tms.repository.UserRepository;
import com.example.tms.repository.entities.Role;
import com.example.tms.repository.entities.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    public Optional<User> findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    };

    public Optional<User> findByMail(String mail)
    {
        return userRepository.findByMail(mail);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public User save(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setMail(registrationUserDto.getMail());
        user.setPassword(registrationUserDto.getPassword());
        /*user.addRole(roleService.findByName("ROLE_UNCONFIRMED").orElseThrow());
        mailService.sendMail(user.getMail(),
                "Confirmation of given email address",
                "Follow this link to confirm your email address:\n" +
                        "\thttp://localhost:8080/api/confirmed/" + user.getLogin());*/
        return userRepository.save(user);
    }

    @Transactional
    public void addRole(String roleName, String username) {
        Role role = roleService.findByName(roleName).orElseThrow();
        User user = findByUsername(username).orElseThrow();
        user.addRole(role);
        userRepository.save(user);
    }

    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(
                        String.format("User '%s' not found", username)
                ));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role ->
                                new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())

        );
    }


}
