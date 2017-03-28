package com.besafx.app.component;

import com.besafx.app.config.EmailSender;
import com.besafx.app.entity.Task;
import com.besafx.app.search.TaskSearch;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.TaskOperationService;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@Component
public class ScheduledTasks {

    private LocalDate today = new DateTime().withTimeAtStartOfDay().toLocalDate();

    private LocalDate tomorrow = new DateTime().plusDays(1).withTimeAtStartOfDay().toLocalDate();

    private String message = "";

    @Autowired
    private PersonService personService;

    @Autowired
    private TaskSearch taskSearch;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private EmailSender emailSender;

    @Scheduled(cron = "0 0/45 23 * * *")
    public void test() {
        System.out.println("ALL");
    }


    @Scheduled(cron = "0 0 21 * * *")
    public void rememberAllAboutUnCommentedTasks() {

        today = new DateTime().withTimeAtStartOfDay().toLocalDate();

        tomorrow = new DateTime().plusDays(1).withTimeAtStartOfDay().toLocalDate();

        ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NoTaskOperationsWarning.html");

        Lists.newArrayList(personService.findAll()).stream().forEach(person -> {
            //Get all opened incoming tasks for this person
            List<Task> tasks = taskSearch.search(null, null, null, null, null, null, null, true, true, "All", person.getId());
            tasks.stream().forEach(task -> {
                long operationsCountToday = taskOperationService.countByTaskAndSenderAndDateBetween(task, person, today.toDate(), tomorrow.toDate());
                if (operationsCountToday == 0) {
                    try {
                        message = org.apache.commons.io.IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    message = message.replaceAll("TASK_CODE", task.getCode().toString());
                    emailSender.send("تحذير بشأن عدم ارسال حركات على المهمة رقم: " + "(" + task.getCode().toString() + ")", message, person.getEmail());
                }
            });
        });
    }
}
