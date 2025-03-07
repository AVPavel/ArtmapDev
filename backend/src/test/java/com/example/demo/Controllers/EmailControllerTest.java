package com.example.demo.Controllers;

import com.example.demo.Services.Customs.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EmailController emailController;

    @Test
    void sendWelcomeEmail_ValidRequest_ReturnsCreated() {
        ResponseEntity<String> response = emailController.sendWelcomeEmail("test@example.com");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Welcome email sent successfully to test@example.com", response.getBody());
        verify(notificationService).sendWelcomeEmail("test@example.com");
    }

    @Test
    void sendPromotionEmail_ValidRequest_ReturnsCreated() {
        ResponseEntity<String> response = emailController.sendPromotionEmail("test@example.com");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Promotional email sent successfully to test@example.com", response.getBody());
        verify(notificationService).sendPromotionEmail("test@example.com");
    }

    // Monthly Report Tests
    @Test
    void sendMonthlyReport_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        String validPath = getValidTestFilePath();

        // Act
        ResponseEntity<String> response = emailController.sendMonthlyReport(
                "test@example.com",
                URLEncoder.encode(validPath, StandardCharsets.UTF_8.toString())
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(notificationService).sendMonthlyReport(anyString(), any(File.class));
    }

    @Test
    void sendMonthlyReport_MissingTo_ReturnsBadRequest() {
        ResponseEntity<String> response = emailController.sendMonthlyReport("", "valid-path.pdf");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request parameters", response.getBody());
        verifyNoInteractions(notificationService);
    }

    @Test
    void sendMonthlyReport_InvalidPath_ReturnsForbidden() throws UnsupportedEncodingException {
        ResponseEntity<String> response = emailController.sendMonthlyReport(
                "test@example.com",
                URLEncoder.encode("../secret.txt", StandardCharsets.UTF_8.toString()) // Corrected encoding
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().contains("Access to requested file path is denied"));
        verifyNoInteractions(notificationService);
    }

    @Test
    void sendMonthlyReport_NonExistentFile_ReturnsNotFound() throws UnsupportedEncodingException {
        String nonExistentPath = "nonexistent.pdf";

        ResponseEntity<String> response = emailController.sendMonthlyReport(
                "test@example.com",
                URLEncoder.encode(nonExistentPath, StandardCharsets.UTF_8.toString()) // Corrected encoding
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("File not found"));
        verifyNoInteractions(notificationService);
    }

    @Test
    void sendMonthlyReport_DirectoryPath_ReturnsBadRequest() throws UnsupportedEncodingException {
        String directoryPath = getTestDirectoryPath();

        ResponseEntity<String> response = emailController.sendMonthlyReport(
                "test@example.com",
                URLEncoder.encode(directoryPath, StandardCharsets.UTF_8.toString()) // Corrected encoding
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Path is not a regular file"));
        verifyNoInteractions(notificationService);
    }

    @Test
    void sendMonthlyReport_InvalidFileType_ReturnsUnsupportedMedia() throws Exception {
        // Arrange
        String invalidFile = getInvalidFileTypePath();

        // Act
        ResponseEntity<String> response = emailController.sendMonthlyReport(
                "test@example.com",
                URLEncoder.encode(invalidFile, StandardCharsets.UTF_8.toString())
        );

        // Assert
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertTrue(response.getBody().contains("Only XML files are allowed"));
        verifyNoInteractions(notificationService);
    }

    @Test
    void sendMonthlyReport_InvalidPathEncoding_ReturnsBadRequest() {
        ResponseEntity<String> response = emailController.sendMonthlyReport(
                "test@example.com",
                "invalid%zzPath"
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid file path format", response.getBody());
        verifyNoInteractions(notificationService);
    }

    @Test
    void sendMonthlyReport_GeneralException_ReturnsInternalError() throws Exception {
        // Arrange
        String validPath = getValidTestFilePath();
        doThrow(new RuntimeException("Service failure")).when(notificationService)
                .sendMonthlyReport(anyString(), any(File.class));

        // Act
        ResponseEntity<String> response = emailController.sendMonthlyReport(
                "test@example.com",
                URLEncoder.encode(validPath, StandardCharsets.UTF_8.toString())
        );

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error processing request", response.getBody());
        verify(notificationService).sendMonthlyReport(anyString(), any(File.class));
    }

    // Helper methods
    private String getValidTestFilePath() {
        Path path = Paths.get("reports").resolve("valid-report.xml").toAbsolutePath();
        File file = path.toFile();
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create test file", e);
        }
        return path.getFileName().toString();
    }

    private String getTestDirectoryPath() {
        Path path = Paths.get("reports").resolve("testdir").toAbsolutePath();
        path.toFile().mkdirs();
        return path.getFileName().toString();
    }

    private String getInvalidFileTypePath() {
        Path path = Paths.get("reports").resolve("invalid.txt").toAbsolutePath();
        try {
            path.toFile().createNewFile();
        } catch (Exception ignored) {}
        return path.getFileName().toString();
    }
}