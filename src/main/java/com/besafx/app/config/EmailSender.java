package com.besafx.app.config;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailSender {

    private final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    @Autowired
    private JavaMailSender sender;

    public void send(String title, String content, List<String> toEmailList) {
        toEmailList.stream().forEach(email -> send(title, content, email));
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
