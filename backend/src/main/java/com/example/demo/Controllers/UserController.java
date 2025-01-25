package com.example.demo.Controllers;

import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Users.UserRegisterDTO;
import com.example.demo.DTOs.Users.UserResponseDTO;
import com.example.demo.Exceptions.Models.DuplicateResourceException;
import com.example.demo.Exceptions.Models.ResourceNotFoundException;
import com.example.demo.Exceptions.Models.UserNotFoundException;
import com.example.demo.Globals.GlobalLogger;
import com.example.demo.Models.ErrorResponse;
import com.example.demo.Services.Mappers.UserMapper;
import com.example.demo.Services.DBServices.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/users")
public class UserController {
    private static final Logger logger = GlobalLogger.getLogger(UserController.class);
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDTO userRegistrationDTO) {
        try {
            User user = userMapper.toEntity(userRegistrationDTO);
            User registerdUser = userService.registerUser(user);
            UserResponseDTO userDTO = userMapper.toResponseDTO(registerdUser);
            logger.info("registerUser() - Created user: {}", userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
        } catch (DuplicateResourceException exception) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    exception.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            logger.error("registerUser() - Duplicate user: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (IllegalArgumentException exception) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    exception.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            logger.error("registerUser() - Invalid user: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            logger.error("registerUser() - Unexpected error: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/organizers")
    public ResponseEntity<?> getOrganizers(
            @RequestParam User.Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        try {
            Page<User> organizers = userService.getUsersByRole(role, page, size, sortBy, sortDir);
            Page<UserResponseDTO> organizersDTO = organizers.map(userMapper::toResponseDTO);
            logger.info("Organizers count: {}", organizers.getTotalElements());
            return ResponseEntity.status(HttpStatus.OK).body(organizersDTO);
        } catch (ResourceNotFoundException | UserNotFoundException exception) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    exception.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            logger.error("getOrganizers - User not found: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            logger.error("getOrganizers - Unexpected error: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            UserResponseDTO userDTO = userMapper.toResponseDTO(user);
            return ResponseEntity.status(HttpStatus.OK).body(userDTO);
        } catch (UserNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            logger.error("getUserById - User not found: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (UserNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            logger.error("deleteUser - User not found: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam String searchTerm,
            @RequestParam(required = false) User.Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Page<User> usersPage = userService.searchUsers(searchTerm, role, page, size, sortBy, sortDir);
            Page<UserResponseDTO> userDTOPage = usersPage.map(userMapper::toResponseDTO);
            logger.info("searchUsers - Users count: {}", usersPage.getTotalElements());
            return ResponseEntity.status(HttpStatus.OK).body(userDTOPage);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            logger.error("searchUsers - Unexpected error: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "200") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Page<User> usersPage = userService.getAllUsers(page, size, sortBy, sortDir);
            Page<UserResponseDTO> userDTOPage = usersPage.map(userMapper::toResponseDTO);
            logger.info("getAllUsers - Users count: {}", usersPage.getTotalElements());
            return ResponseEntity.status(HttpStatus.OK).body(userDTOPage);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            logger.error("getAllUsers - Unexpected error: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try{
            User newUser = userService.updateUser(id, user);
            logger.info("updateUser - User updated: {}", newUser);
            return ResponseEntity.status(HttpStatus.OK).body(newUser);
        }
        catch(UserNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            logger.error("updateUser - User not found: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        catch (DuplicateResourceException e){
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    e.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            logger.error("updateUser - Duplicate user: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

}
