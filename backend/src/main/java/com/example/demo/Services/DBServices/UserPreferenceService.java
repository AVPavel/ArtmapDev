package com.example.demo.Services.DBServices;

import com.example.demo.DBModels.Category;
import com.example.demo.DBModels.Genre;
import com.example.demo.DBModels.User;
import com.example.demo.DBModels.UserPreference;
import com.example.demo.Repositories.CategoryRepository;
import com.example.demo.Repositories.GenreRepository;
import com.example.demo.Repositories.UserPreferenceRepository;
import com.example.demo.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// UserPreferenceService.java
@Service
public class UserPreferenceService {
    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final GenreRepository genreRepository;

    @Autowired
    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository,
                                 UserRepository userRepository,
                                 CategoryRepository categoryRepository,
                                 GenreRepository genreRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.genreRepository = genreRepository;
    }

    @Transactional
    public UserPreference addUserPreference(Long userId, Long categoryId, Long genreId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        // Check if combination already exists
        if (userPreferenceRepository.findByUserAndCategoryAndGenre(user, category, genre).isPresent()) {
            throw new RuntimeException("Preference combination already exists");
        }

        UserPreference preference = new UserPreference();
        preference.setUser(user);
        preference.setCategory(category);
        preference.setGenre(genre);

        return userPreferenceRepository.save(preference);
    }

    public List<UserPreference> getUserPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userPreferenceRepository.findByUser(user);
    }
}
