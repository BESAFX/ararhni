package com.besafx.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailSender {

    private final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    @Autowired
    private JavaMailSender sender;

    @Async
    public void send(String title, String content, List<String> toEmailList) {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("anni4ksa@gmail.com");
            String[] emails = new String[toEmailList.size()];
            emails = toEmailList.toArray(emails);
            logger.info("Trying Sending to this emails: " + Arrays.toString(emails));
            helper.setTo(emails);
            helper.setSubject(title);
            helper.setText(content, true);
            sender.send(message);
            logger.info("Sent Successfully to: " + Arrays.toString(emails));
            // Artificial delay of 1s for demonstration purposes
            Thread.sleep(1000 * 10);
        } catch (MessagingException e) {
            logger.error("Sent Failed", e);
            throw new CustomException(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void send(String title, String content, String email) {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("anni4ksa@gmail.com");
            helper.setTo(email);
            helper.setSubject(title);
            helper.setText(content, true);
            sender.send(message);
            logger.info("Sent Successfully to: " + email);
        } catch (MessagingException e) {
            logger.error("Sent Failed", e);
            throw new CustomException(e.getMessage());
        }
    }
}
