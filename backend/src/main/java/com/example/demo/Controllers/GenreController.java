package com.example.demo.Controllers;

import com.example.demo.DBModels.Genre;
import com.example.demo.Exceptions.Models.DuplicateResourceException;
import com.example.demo.Exceptions.Models.GenreNotFoundException;
import com.example.demo.Models.ErrorResponse;
import com.example.demo.Services.DBServices.GenreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {
    private final static Logger LOGGER = LoggerFactory.getLogger(GenreController.class);
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }


    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();
        LOGGER.info("getAllGenres - genres found: {}", genres);
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable Long id) {
        Genre genre;
        try {
            genre = genreService.getGenreById(id);
        } catch (GenreNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    ex.getMessage(),
                    "Genre",
                    LocalDateTime.now()
            );
            LOGGER.error("getGenreById - genre not found: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        LOGGER.info("getGenreById - genre found: {}", genre);
        return new ResponseEntity<>(genre, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createGenre(@RequestBody Genre genre) {
        Genre newGenre;
        try {
            newGenre = genreService.addGenre(genre);
        } catch (DuplicateResourceException ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    ex.getMessage(),
                    "Genre",
                    LocalDateTime.now()
            );
            LOGGER.error("createGenre - genre already exists: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
        LOGGER.info("createGenre - genre found: {}", newGenre);
        return ResponseEntity.status(HttpStatus.CREATED).body(newGenre);
    }

    // Update an existing genre
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGenre(@PathVariable Long id, @RequestBody Genre genre) {
        Genre updatedGenre;
        try {
            updatedGenre = genreService.updateGenre(id, genre);
        } catch (GenreNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    ex.getMessage(),
                    "Genre",
                    LocalDateTime.now()
            );
            LOGGER.error("updateGenre - genre not found: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        LOGGER.info("updateGenre - genre found: {}", updatedGenre);
        return ResponseEntity.status(HttpStatus.OK).body(updatedGenre);
    }

    // Delete a genre
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGenre(@PathVariable Long id) {
        try {
            genreService.deleteGenre(id);
        } catch (GenreNotFoundException ex) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    ex.getMessage(),
                    "Genre",
                    LocalDateTime.now()
            );
            LOGGER.error("deleteGenre - genre not found: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        LOGGER.info("deleteGenre - genre found: {}", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
