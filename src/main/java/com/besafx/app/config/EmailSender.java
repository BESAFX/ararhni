package com.besafx.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Future;

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
            log.info("Preparing email service...");
            message = sender.createMimeMessage();
            helper = new MimeMessageHelper(message, true);
            helper.setFrom("anni4ksa@gmail.com");
            log.info("Login successfully");
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Async("threadPoolEmailSender")
    public void send(String title, String content, List<String> toEmailList) {
        try {
            log.info("Sleeping for 10 seconds");
            Thread.sleep(10000);
            String[] emails = new String[toEmailList.size()];
            emails = toEmailList.toArray(emails);
            log.info("Trying sending email to this destinations: " + Arrays.toString(emails));
            helper.setTo(emails);
            helper.setSubject(title);
            helper.setText(content, true);
            sender.send(message);
            log.info("Sending email successfully to this destinations: " + Arrays.toString(emails));
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Async("threadPoolEmailSender")
    public void send(String title, String content, List<String> toEmailList, List<FileSystemResource> files) {
        try {
            log.info("Sleeping for 10 seconds");
            Thread.sleep(10000);
            String[] emails = new String[toEmailList.size()];
            emails = toEmailList.toArray(emails);
            log.info("Trying sending email to this destinations: " + Arrays.toString(emails));
            helper.setTo(emails);
            helper.setSubject(title);
            helper.setText(content, true);
            ListIterator<FileSystemResource> fileSystemResourceListIterator = files.listIterator();
            while (fileSystemResourceListIterator.hasNext()) {
                FileSystemResource fileSystemResource = fileSystemResourceListIterator.next();
                helper.addAttachment(fileSystemResource.getFilename(), fileSystemResource);
            }
            sender.send(message);
            log.info("Sending email successfully to this destinations: " + Arrays.toString(emails));
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Async("threadPoolEmailSender")
    public void send(String title, String content, String email) {
        try {
            log.info("Sleeping for 10 seconds...");
            Thread.sleep(10000);
            log.info("Trying sending email to this destination: " + email);
            helper.setTo(email);
            helper.setSubject(title);
            helper.setText(content, true);
            sender.send(message);
            log.info("Sending email successfully to this destination: " + email);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Async("threadPoolEmailSender")
    public Future<Boolean> send(String title, String content, String email, List<FileSystemResource> files) {
        try {
            log.info("Trying sending email to this destination: " + email);
            Thread.sleep(1000 * 10);
            helper.setTo(email);
            helper.setSubject(title);
            helper.setText(content, true);
            ListIterator<FileSystemResource> fileSystemResourceListIterator = files.listIterator();
            while (fileSystemResourceListIterator.hasNext()) {
                FileSystemResource fileSystemResource = fileSystemResourceListIterator.next();
                helper.addAttachment(fileSystemResource.getFilename(), fileSystemResource);
            }
            sender.send(message);
            log.info("Sending email successfully to this destination: " + email);
            return new AsyncResult<>(true);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
            return new AsyncResult<>(false);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            return new AsyncResult<>(false);
        }
    }
}
