package com.example.tms.services;

import com.example.tms.dtos.RegistrationUserDto;
import com.example.tms.repository.UserRepository;
import com.example.tms.repository.entities.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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

    /**
     *
     * @param username Requested login by which need to find entity
     * @return User
     *
     */
    public Optional<User> findByLogin(String username)
    {
        return userRepository.findByLogin(username);
    }

    /**
     *
     * @param nickname Requested nickname by which need to find entity
     * @return User
     *
     */
    public Optional<User> findByNickname(String nickname)
    {
        return userRepository.findByNickname(nickname);
    }

    /**
     *
     * @param user User needs to be saved in database
     */
    public void save(User user) {
        userRepository.save(user);
    }

    /**
     *
     * @param registrationUserDto New user data object
     * @return User
     */
    public User save(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setLogin(registrationUserDto.getLogin());
        user.setNickname(registrationUserDto.getNickname());
        user.setPassword(registrationUserDto.getPassword());
        user.addRole(roleService.findByName("ROLE_USER").orElseThrow());
        return userRepository.save(user);
    }

    /**
     *
     * @param login Requested login by which need to check if user exists
     * @return true: if users not exists, false: if one exists
     */
    public boolean ifUserNotExists(String login)
    {
        try{
            findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("No such user!"));
            return false;
        } catch (UsernameNotFoundException e)
        {
            return true;
        }
    }

    /**
     *
     * @param username Requested username (login) by which need to load user
     * @return UserDetails - User credentials management class
     * @see UserDetails Returned class
     * @throws UsernameNotFoundException
     */
    @Override
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
