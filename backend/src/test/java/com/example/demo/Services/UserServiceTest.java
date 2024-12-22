package com.example.demo.Services;

import com.example.demo.DBModels.User;
import com.example.demo.Exceptions.Models.DuplicateResourceException;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.DBServices.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setup(){
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRole(User.Role.USER);
        user.setEmail("user@gmail.com");

    }

    @Test
    void registerUser_Success(){
        //Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User registeredUser = userService.registerUser(user);

        //Assert
        assertNotNull(registeredUser);
        assertEquals("testuser",registeredUser.getUsername());
        assertEquals("hashedPassword",registeredUser.getPassword());
        verify(userRepository,times(1)).save(user);

    }

    @Test
    void registerUser_DuplicateUsername(){
        //Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        //Act & Assert
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,() ->{
            userService.registerUser(user);
        });

        assertEquals("Username already exists",exception.getMessage());
        verify(userRepository,never()).save(any(User.class));
    }

    @Test
    void registerUser_DuplicateEmail(){
        //Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        //Act & Assert
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,() ->{
            userService.registerUser(user);
        });

        assertEquals("Email already exists",exception.getMessage());
        verify(userRepository,never()).save(any(User.class));
    }

    @Test
    void registerUser_DefaultRole(){
        //Arrange
        user.setRole(null);
        User savedUser = new User();
        savedUser.setUsername("testuser");
        savedUser.setEmail("user@gmail.com");
        savedUser.setPassword("hashedPassword");
        savedUser.setRole(User.Role.USER);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User registeredUser = userService.registerUser(user);

        assertNotNull(registeredUser);
        assertEquals(User.Role.USER,registeredUser.getRole(),"Default role should be USER");
        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    void registerUser_DatabaseError(){
        //Arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database Error"));

        //Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,() ->{
            userService.registerUser(user);
        });

        assertEquals("Database Error",exception.getMessage());
        verify(userRepository,times(1)).save(any(User.class));
    }

}
