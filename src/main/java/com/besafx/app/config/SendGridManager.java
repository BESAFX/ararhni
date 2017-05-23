//package com.besafx.app.config;
//import com.sendgrid.*;
//import org.apache.commons.io.FileUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.AsyncResult;
//import org.springframework.stereotype.Service;
//import org.springframework.util.Base64Utils;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.concurrent.Future;
//import java.util.concurrent.ThreadLocalRandom;
//
//@Service
//public class SendGridManager {
//
//    private final static Logger log = LoggerFactory.getLogger(SendGridManager.class);
//
//    private static final String API_KEY = "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";
//
//    @Async("threadPoolEmailSender")
//    public void send(String title, String content, List<String> toEmailList) {
//        Mail mail = new Mail();
//        mail.setFrom(new Email("admin@ararhni.com", "المدير الذكي"));
//        mail.setSubject(title);
//        mail.addContent(new Content("text/html", content));
//        Personalization personalization = new Personalization();
//        personalization.addTo(new Email("anni4ksa@gmail.com"));
//        toEmailList.stream().forEach(e -> personalization.addTo(new Email(e)));
//        mail.addPersonalization(personalization);
//        SendGrid sg = new SendGrid(API_KEY);
//        callAPI(mail, sg);
//    }
//
//    @Async("threadPoolEmailSender")
//    public void send(String title, String content, List<String> toEmailList, List<File> files) {
//        Mail mail = new Mail();
//        mail.setFrom(new Email("admin@ararhni.com", "المدير الذكي"));
//        mail.setSubject(title);
//        mail.addContent(new Content("text/html", content));
//        files.stream().forEach(file -> {
//            initMailFile(mail, file);
//        });
//        Personalization personalization = new Personalization();
//        personalization.addTo(new Email("anni4ksa@gmail.com"));
//        toEmailList.stream().forEach(e -> personalization.addTo(new Email(e)));
//        mail.addPersonalization(personalization);
//        SendGrid sg = new SendGrid(API_KEY);
//        callAPI(mail, sg);
//    }
//
//    @Async("threadPoolEmailSender")
//    public void send(String title, String content, String email) {
//        Mail mail = new Mail();
//        mail.setFrom(new Email("admin@ararhni.com", "المدير الذكي"));
//        mail.setSubject(title);
//        mail.addContent(new Content("text/html", content));
//        Personalization personalization = new Personalization();
//        personalization.addTo(new Email("anni4ksa@gmail.com"));
//        personalization.addTo(new Email(email));
//        mail.addPersonalization(personalization);
//        SendGrid sg = new SendGrid(API_KEY);
//        callAPI(mail, sg);
//    }
//
//    @Async("threadPoolEmailSender")
//    public Future<Boolean> send(String title, String content, String email, List<File> files) {
//        Mail mail = new Mail();
//        mail.setFrom(new Email("admin@ararhni.com", "المدير الذكي"));
//        mail.setSubject(title);
//        mail.addContent(new Content("text/html", content));
//        files.stream().forEach(file -> {
//            initMailFile(mail, file);
//        });
//        Personalization personalization = new Personalization();
//        personalization.addTo(new Email("anni4ksa@gmail.com"));
//        personalization.addTo(new Email(email));
//        mail.addPersonalization(personalization);
//        SendGrid sg = new SendGrid(API_KEY);
//        return callAPI(mail, sg);
//    }
//
//    private void initMailFile(Mail mail, File file) {
//        try {
//            Attachments attachments = new Attachments();
//            attachments.setContent(Base64Utils.encodeToString(FileUtils.readFileToByteArray(file)));
//            attachments.setType(Files.probeContentType(Paths.get(file.getPath())));
//            attachments.setFilename(file.getName());
//            attachments.setDisposition("attachment");
//            attachments.setContentId("Attach-" + ThreadLocalRandom.current().nextInt(1, 5000));
//            mail.addAttachments(attachments);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Future<Boolean> callAPI(Mail mail, SendGrid sg) {
//        try {
//            Request request = new Request();
//            request.setMethod(Method.POST);
//            request.setEndpoint("mail/send");
//            request.setBody(mail.build());
//            Response response = sg.api(request);
//            log.info("Email Response Status Code: " + response.getStatusCode());
//            if (response.getStatusCode() >= 200) {
//                return new AsyncResult<>(true);
//            } else {
//                return new AsyncResult<>(false);
//            }
//        } catch (IOException ex) {
//            log.info(ex.getMessage());
//            return new AsyncResult<>(false);
//        }
//    }
//}
