package com.example.demo.Repositories;

import com.example.demo.DBModels.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find by name (optional helper method)
    Optional<Category> findByName(String name);
}
