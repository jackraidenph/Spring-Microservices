package dev.miniposter.authserver.service;

import dev.miniposter.authserver.model.User;
import dev.miniposter.authserver.repository.UserRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

@Service
@Log
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public void create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        this.userRepository.save(user);
        log.log(Level.INFO, "Registered new user, email: " + user.getEmail());
    }

    public User getByUsername(String username) {
        return this.userRepository
                .findByUsername(username)
                .orElseGet(
                        () -> this.userRepository.findByEmail(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found!"))
                );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.getByUsername(username);
    }
}