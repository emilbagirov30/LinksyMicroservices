package com.emil.linksy_email.service;

import com.emil.linksy_email.model.EmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Value("${email_username}")
    private String username;

    @Value("${email_password}")
    private String password;

    @KafkaListener(topics = "emails", groupId = "group_id")
    public void consume(EmailRequest emailRequest) {
        sendEmail(emailRequest.getTo(), emailRequest.getTitle(), emailRequest.getBody());
    }

    public void sendEmail(String to, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "connect.smtp.bz");
        props.put("mail.smtp.port", "25");

        props.put("mail.smtp.ssl.trust", "connect.smtp.bz");
        props.put("mail.smtp.ssl.checkserveridentity", "false");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
