package com.example.demo.Services.Customs;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Utility class for sending emails.
 */
@Component
public class EmailUtility {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    /**
     * Constructor for EmailUtility.
     *
     * @param host     SMTP server host.
     * @param port     SMTP server port.
     * @param username SMTP username.
     * @param password SMTP password.
     */
    public EmailUtility(
            @Value("${spring.mail.host}") String host,
            @Value("${spring.mail.port}") int port,
            @Value("${spring.mail.username}") String username,
            @Value("${spring.mail.password}") String password
    ) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * Creates a mail session with SMTP configurations.
     *
     * @return Configured mail session.
     */
    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true"); // Enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS
        props.put("mail.smtp.host", host); // SMTP host
        props.put("mail.smtp.port", port); // SMTP port

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    /**
     * Sends a plain text email.
     *
     * @param to      Recipient email address.
     * @param subject Email subject.
     * @param body    Email body (plain text).
     */
    public void sendPlainTextEmail(String to, String subject, String body) {
        try {
            Message message = createMimeMessage(to, subject, body, false, null);
            Transport.send(message);
            System.out.println("Plain text email sent successfully to " + to);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Sends an HTML email.
     *
     * @param to       Recipient email address.
     * @param subject  Email subject.
     * @param htmlBody Email body (HTML content).
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            Message message = createMimeMessage(to, subject, htmlBody, true, null);
            Transport.send(message);
            System.out.println("HTML email sent successfully to " + to);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Sends an email with attachments.
     *
     * @param to          Recipient email address.
     * @param subject     Email subject.
     * @param body        Email body (plain text or HTML).
     * @param isHtml      Whether the body is HTML content.
     * @param attachments List of files to attach to the email.
     */
    public void sendEmailWithAttachments(String to, String subject, String body, boolean isHtml, List<File> attachments) {
        try {
            Message message = createMimeMessage(to, subject, body, isHtml, attachments);
            Transport.send(message);
            System.out.println("Email with attachments sent successfully to " + to);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Creates a MIME message.
     *
     * @param to          Recipient email address.
     * @param subject     Email subject.
     * @param body        Email body.
     * @param isHtml      Whether the body is HTML content.
     * @param attachments List of files to attach.
     * @return Configured MIME message.
     * @throws MessagingException If there's an error in message creation.
     * @throws IOException        If there's an error attaching files.
     */
    private Message createMimeMessage(String to, String subject, String body, boolean isHtml, List<File> attachments) throws MessagingException, IOException {
        Session session = createSession();
        MimeMessage mimeMessage = new MimeMessage(session);

        // Set From
        mimeMessage.setFrom(new InternetAddress(username));

        // Set To
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

        // Set Subject
        mimeMessage.setSubject(subject);

        // Create the message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        if (isHtml) {
            messageBodyPart.setContent(body, "text/html");
        } else {
            messageBodyPart.setText(body);
        }

        // Create multipart
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Add attachments if any
        if (attachments != null && !attachments.isEmpty()) {
            for (File file : attachments) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(file);
                multipart.addBodyPart(attachmentPart);
            }
        }

        // Set the complete message parts
        mimeMessage.setContent(multipart);

        return mimeMessage;
    }
}
