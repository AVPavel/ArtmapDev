package com.example.demo.Controllers;

import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Users.UserRegisterDTO;
import com.example.demo.DTOs.Users.UserResponseDTO;
import com.example.demo.Exceptions.Models.DuplicateResourceException;
import com.example.demo.Exceptions.Models.ResourceNotFoundException;
import com.example.demo.Exceptions.Models.UserNotFoundException;
import com.example.demo.Models.ErrorResponse;
import com.example.demo.Services.Mappers.UserMapper;
import com.example.demo.Services.DBServices.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/users")
public class UserController {
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
            return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
        } catch (DuplicateResourceException exception) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    exception.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (IllegalArgumentException exception) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    exception.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/organizers")
    public ResponseEntity<Page<UserResponseDTO>> getOrganizers(
            @RequestParam User.Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        try {
            Page<User> organizers = userService.getUsersByRole(role, page, size, sortBy, sortDir);
            Page<UserResponseDTO> organizersDTO = organizers.map(userMapper::toResponseDTO);
            return new ResponseEntity<>(organizersDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException | UserNotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            UserResponseDTO userDTO = userMapper.toResponseDTO(user);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserResponseDTO>> searchUsers(
            @RequestParam String searchTerm,
            @RequestParam(required = false) User.Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Page<User> usersPage = userService.searchUsers(searchTerm, role, page, size, sortBy, sortDir);
            Page<UserResponseDTO> userDTOPage = usersPage.map(userMapper::toResponseDTO);
            return new ResponseEntity<>(userDTOPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/all")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "200") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Page<User> usersPage = userService.getAllUsers(page, size, sortBy, sortDir);
            Page<UserResponseDTO> userDTOPage = usersPage.map(userMapper::toResponseDTO);
            return new ResponseEntity<>(userDTOPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
