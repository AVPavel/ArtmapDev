package com.example.demo.Services.DBServices;

import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.User;
import com.example.demo.Exceptions.Models.DuplicateResourceException;
import com.example.demo.Exceptions.Models.ResourceNotFoundException;
import com.example.demo.Exceptions.Models.UserNotFoundException;
import com.example.demo.Repositories.EventRepository;
import com.example.demo.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventRepository eventRepository;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventRepository = eventRepository;
    }

    //Inregistrare utilizator
    @Transactional
    public User registerUser(User user) {
        //Caut un user similar deja existent
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already exists");
        }

        //hash pentru parola
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null || user.getRole().toString().trim().isEmpty()) {
            user.setRole(User.Role.USER);
        }

        return userRepository.save(user);

    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    /*@Transactional(readOnly = true)
    public User getUserByIdAndRole(Long id, User.Role role) {
        return userRepository.findByIdAndRole(id, role)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public User getUserByUsernameAndRole(String username, User.Role role) {
        return userRepository.findByUsernameAndRole(username, role)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }*/

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.findByUsername(updatedUser.getUsername()).isPresent()) {
                throw new DuplicateResourceException("Username already exists");
            }
        }
        if (updatedUser.getEmail() != null && updatedUser.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
                throw new DuplicateResourceException("Email already exists");
            }
        }

        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        if (updatedUser.getRole() != null) {
            existingUser.setRole(updatedUser.getRole());
        }

        return userRepository.save(existingUser);
    }
    @Transactional
    public Page<User> getUsersByRole(User.Role role, int page, int size, String sortBy, String sortDirection){
        Pageable pageable = PageRequest.of(page, size,
                sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        return userRepository.findByRole(role, pageable);
    }

    @Transactional
    public void deleteUser(Long id) {
        User existingUser = getUserById(id);
        userRepository.delete(existingUser);
    }

    @Transactional(readOnly = true)
    public Page<User> searchUsers(String searchTerm, User.Role role, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

        if (searchTerm != null && !searchTerm.isEmpty()) {
            return userRepository.searchUsers(searchTerm, pageable);
        }

        if (role != null) {
            return userRepository.findByRole(role, pageable);
        }

        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        return userRepository.findAll(pageable);
    }
    // Add this method to your UserService
    @Transactional
    public User registerSocialUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists");
        }

        // No password encoding needed for social users
        user.setPassword(user.getPassword()); // Store as-is

        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }

        return userRepository.save(user);
    }

    public User getUserWithFavoriteEvents(String username) {
        // This method should fetch the user along with their eventsParticipating
        return userRepository.findByUsernameWithEventsParticipating(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    @Transactional
    public void addFavoriteEvent(String username, Long eventId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        user.getEventsParticipating().add(event);
        userRepository.save(user);
    }
    @Transactional
    public void removeFavoriteEvent(String username, Long eventId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        user.getEventsParticipating().remove(event);
        userRepository.save(user);
    }
}
