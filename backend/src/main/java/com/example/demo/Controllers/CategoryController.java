package com.example.demo.Controllers;

import com.example.demo.DBModels.Category;
import com.example.demo.Exceptions.Models.CategoryNotFoundException;
import com.example.demo.Exceptions.Models.DuplicateResourceException;
import com.example.demo.Models.ErrorResponse;
import com.example.demo.Services.DBServices.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final static Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        Category category;
        try{
             category = categoryService.getCategoryById(id);
        }
        catch (CategoryNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "Category",
                    LocalDateTime.now()
            );
            LOGGER.error("getCategoryById - category not found:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        LOGGER.info("getCategoryById - Category found with id {}", id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        LOGGER.info("getAllCategories - Categories found");
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) { 
        try {
            Category newCategory = categoryService.addCategory(category);
            LOGGER.info("createCategory - Category added with id {}", newCategory.getId());
            return ResponseEntity.status(HttpStatus.OK).body(newCategory);
        } catch (DuplicateResourceException ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "Resource already exists: " + ex.getMessage(),
                    "Category",
                    LocalDateTime.now()
            );
            LOGGER.error("createCategory - Category already exists: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error: " + ex.getMessage(),
                    "Category",
                    LocalDateTime.now()
            );
            LOGGER.error("createCategory - unexpected error: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category updatedCategory) {
        Category category;
        try{
            category = categoryService.updateCategory(id, updatedCategory);
        }
        catch (CategoryNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "Category",
                    LocalDateTime.now()
            );
            LOGGER.error("updateCategory - Category not found:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        LOGGER.info("updateCategory - Category found with id {}", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try{
            categoryService.deleteCategory(id);
        }
        catch (CategoryNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "Category",
                    LocalDateTime.now()
            );
            LOGGER.error("deleteCategory - Category not found:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        LOGGER.info("deleteCategory - Category found with id {}", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
