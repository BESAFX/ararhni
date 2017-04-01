package com.besafx.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailSender {

    private final Logger log = LoggerFactory.getLogger(EmailSender.class);

    private MimeMessage message;

    private MimeMessageHelper helper;

    @Autowired
    private JavaMailSender sender;

    @PostConstruct
    public void init() {
        try {
            log.info("تهيئة خدمة البريد الإلكتروني...");
            message = sender.createMimeMessage();
            helper = new MimeMessageHelper(message, true);
            helper.setFrom("anni4ksa@gmail.com");
            log.info("تم الدخول بنجاح");
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Async("threadPoolTaskExecutor")
    public void send(String title, String content, List<String> toEmailList) {
        try {
            log.info("راحة لمدة 10 ثواني");
            Thread.sleep(10000);
            String[] emails = new String[toEmailList.size()];
            emails = toEmailList.toArray(emails);
            log.info("جاري إرسال الرسالة إلى الإيميلات الآتية: " + Arrays.toString(emails));
            helper.setTo(emails);
            helper.setSubject(title);
            helper.setText(content, true);
            sender.send(message);
            log.info("تم الإرسال بنجاح إلى كل من: " + Arrays.toString(emails));
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Async("threadPoolTaskExecutor")
    public void send(String title, String content, String email) {
        try {
            log.info("راحة لمدة 10 ثواني");
            Thread.sleep(10000);
            log.info("جاري إرسال الرسالة إلى البريد الإلكتروني: " + email);
            helper.setTo(email);
            helper.setSubject(title);
            helper.setText(content, true);
            sender.send(message);
            log.info("تم الإرسال بنجاح إلى العنوان الآتي: " + email);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
