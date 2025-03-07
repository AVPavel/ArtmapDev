package com.example.demo.Controllers;

import com.example.demo.DBModels.Genre;
import com.example.demo.Exceptions.Models.DuplicateResourceException;
import com.example.demo.Exceptions.Models.GenreNotFoundException;
import com.example.demo.Models.ErrorResponse;
import com.example.demo.Services.DBServices.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreControllerTest {

    @Mock
    private GenreService genreService;

    @InjectMocks
    private GenreController genreController;

    private Genre testGenre;
    private Genre testGenre2;

    @BeforeEach
    void setUp() {
        testGenre = new Genre();
        testGenre.setId(1L);
        testGenre.setName("Rock");

        testGenre2 = new Genre();
        testGenre2.setId(2L);
        testGenre2.setName("Jazz");
    }

    @Test
    void getAllGenres_WhenGenresExist_ReturnsGenresList() {
        // Arrange
        List<Genre> genres = Arrays.asList(testGenre, testGenre2);
        when(genreService.getAllGenres()).thenReturn(genres);

        // Act
        ResponseEntity<List<Genre>> response = genreController.getAllGenres();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(genreService).getAllGenres();
    }

    @Test
    void getGenreById_WhenGenreExists_ReturnsGenre() {
        // Arrange
        when(genreService.getGenreById(1L)).thenReturn(testGenre);

        // Act
        ResponseEntity<?> response = genreController.getGenreById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testGenre, response.getBody());
        verify(genreService).getGenreById(1L);
    }

    @Test
    void getGenreById_WhenGenreNotFound_ReturnsError() {
        // Arrange
        when(genreService.getGenreById(1L)).thenThrow(new GenreNotFoundException("Genre not found"));

        // Act
        ResponseEntity<?> response = genreController.getGenreById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        verify(genreService).getGenreById(1L);
    }

    @Test
    void createGenre_WithValidGenre_ReturnsCreatedGenre() {
        // Arrange
        when(genreService.addGenre(testGenre)).thenReturn(testGenre);

        // Act
        ResponseEntity<?> response = genreController.createGenre(testGenre);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testGenre, response.getBody());
        verify(genreService).addGenre(testGenre);
    }

    @Test
    void createGenre_WithDuplicateGenre_ReturnsConflict() {
        // Arrange
        when(genreService.addGenre(testGenre)).thenThrow(new DuplicateResourceException("Genre exists"));

        // Act
        ResponseEntity<?> response = genreController.createGenre(testGenre);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        verify(genreService).addGenre(testGenre);
    }

    @Test
    void updateGenre_WithValidId_ReturnsUpdatedGenre() {
        // Arrange
        Genre updatedGenre = new Genre();
        updatedGenre.setId(1L);
        updatedGenre.setName("Progressive Rock");

        when(genreService.updateGenre(1L, testGenre)).thenReturn(updatedGenre);

        // Act
        ResponseEntity<?> response = genreController.updateGenre(1L, testGenre);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedGenre, response.getBody());
        verify(genreService).updateGenre(1L, testGenre);
    }

    @Test
    void updateGenre_WithInvalidId_ReturnsNotFound() {
        // Arrange
        when(genreService.updateGenre(1L, testGenre))
                .thenThrow(new GenreNotFoundException("Genre not found"));

        // Act
        ResponseEntity<?> response = genreController.updateGenre(1L, testGenre);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        verify(genreService).updateGenre(1L, testGenre);
    }

    @Test
    void deleteGenre_WithValidId_ReturnsNoContent() {
        // Arrange
        doNothing().when(genreService).deleteGenre(1L);

        // Act
        ResponseEntity<?> response = genreController.deleteGenre(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(genreService).deleteGenre(1L);
    }

    @Test
    void deleteGenre_WithInvalidId_ReturnsNotFound() {
        // Arrange
        doThrow(new GenreNotFoundException("Genre not found"))
                .when(genreService).deleteGenre(1L);

        // Act
        ResponseEntity<?> response = genreController.deleteGenre(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        verify(genreService).deleteGenre(1L);
    }
}