package com.example.demo.Services.Customs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Service class for handling notifications via email.
 */
@Service
public class NotificationService {

    private final EmailUtility emailUtility;

    /**
     * Constructor for NotificationService.
     *
     * @param emailUtility Utility class for sending emails.
     */
    @Autowired
    public NotificationService(EmailUtility emailUtility) {
        this.emailUtility = emailUtility;
    }

    /**
     * Sends a welcome email to a new user.
     *
     * @param recipient Recipient's email address.
     */
    public void sendWelcomeEmail(String recipient) {
        String subject = "Welcome to Our Platform!";
        String body = "Hello, welcome to our platform. We're excited to have you on board!";
        emailUtility.sendPlainTextEmail(recipient, subject, body);
    }

    /**
     * Sends a monthly report email with an attachment.
     *
     * @param recipient Recipient's email address.
     * @param reportFile File to attach to the email.
     */
    public void sendMonthlyReport(String recipient, File reportFile) {
        String subject = "Monthly Report";
        String body = "Hello, please find your monthly report attached.";
        emailUtility.sendEmailWithAttachments(recipient, subject, body, false, List.of(reportFile));
    }

    /**
     * Sends a promotional email to a user.
     *
     * @param recipient Recipient's email address.
     */
    public void sendPromotionEmail(String recipient) {
        String subject = "Exclusive Offer Just for You!";
        String htmlBody = "<h1>Big Savings!</h1><p>Don't miss out on our exclusive offer.</p>";
        emailUtility.sendHtmlEmail(recipient, subject, htmlBody);
    }
}
