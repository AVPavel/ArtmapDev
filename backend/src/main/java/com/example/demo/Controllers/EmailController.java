package com.example.demo.Controllers;

import com.example.demo.Services.Customs.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * REST controller for handling email-related requests.
 */
@RestController
@RequestMapping("/api/emails")
public class EmailController {
    private final NotificationService notificationService;

    /**
     * Constructor for EmailController.
     *
     * @param notificationService Service for sending notifications.
     */
    @Autowired
    public EmailController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Endpoint to send a welcome email.
     *
     * @param to Recipient's email address.
     * @return Response message.
     */
    @PostMapping("/welcome")
    public ResponseEntity<String> sendWelcomeEmail(@RequestParam String to) {
        notificationService.sendWelcomeEmail(to);
        return ResponseEntity.status(HttpStatus.CREATED).body("Welcome email sent successfully to " + to);
    }

    /**
     * Endpoint to send a monthly report email with an attachment.
     *
     * @param to         Recipient's email address.
     * @param filePath   Path to the report file to attach.
     * @return Response message.
     */
    @PostMapping("/monthly-report")
    public ResponseEntity<String> sendMonthlyReport(
            @RequestParam String to,
            @RequestParam String filePath) {

        try {
            String decodedFilePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);

            if (to == null || to.isBlank() || decodedFilePath == null || decodedFilePath.isBlank()) {
                return ResponseEntity.badRequest().body("Invalid request parameters");
            }

            Path baseDir = Paths.get("reports").toAbsolutePath().normalize();
            Path resolvedPath = baseDir.resolve(decodedFilePath).normalize();

            if (!resolvedPath.startsWith(baseDir)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access to requested file path is denied");
            }

            File reportFile = resolvedPath.toFile();

            if (!reportFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("File not found: " + resolvedPath);
            }

            if (!reportFile.isFile()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Path is not a regular file");
            }

            if (!reportFile.getName().toLowerCase().endsWith(".pdf")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Only XML files are allowed");
            }

            notificationService.sendMonthlyReport(to, reportFile);
            return ResponseEntity.ok("Monthly report email sent successfully to " + to);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid file path format");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing request");
        }
    }

    /**
     * Endpoint to send a promotional HTML email.
     *
     * @param to Recipient's email address.
     * @return Response message.
     */
    @PostMapping("/promotion")
    public ResponseEntity<String> sendPromotionEmail(@RequestParam String to) {
        notificationService.sendPromotionEmail(to);
        return ResponseEntity.status(HttpStatus.CREATED).body("Promotional email sent successfully to " + to);
    }
}
