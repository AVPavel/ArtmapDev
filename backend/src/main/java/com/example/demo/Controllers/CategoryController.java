package com.example.demo.Controllers;

import com.example.demo.DBModels.Category;
import com.example.demo.Exceptions.Models.CategoryNotFoundException;
import com.example.demo.Exceptions.Models.DuplicateResourceException;
import com.example.demo.Models.ErrorResponse;
import com.example.demo.Services.DBServices.CategoryService;
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) { 
        try {
            Category newCategory = categoryService.addCategory(category);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(newCategory.getId())
                    .toUri();
            return ResponseEntity.created(location).body(newCategory);
        } catch (DuplicateResourceException ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "Resource already exists: " + ex.getMessage(),
                    "Category",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error: " + ex.getMessage(),
                    "Category",
                    LocalDateTime.now()
            );
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
