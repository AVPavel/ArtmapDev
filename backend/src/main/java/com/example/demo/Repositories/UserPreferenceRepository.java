package com.example.demo.Repositories;

import com.example.demo.DBModels.Category;
import com.example.demo.DBModels.Genre;
import com.example.demo.DBModels.User;
import com.example.demo.DBModels.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByUserAndCategoryAndGenre(User user, Category category, Genre genre);
    List<UserPreference> findByUser(User user);
}