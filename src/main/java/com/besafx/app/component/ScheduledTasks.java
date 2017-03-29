package com.besafx.app.component;

import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskOperation;
import com.besafx.app.rest.TaskOperationRest;
import com.besafx.app.search.TaskSearch;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.TaskOperationService;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ScheduledTasks {

    private LocalDate today = new DateTime().withTimeAtStartOfDay().toLocalDate();

    private LocalDate tomorrow = new DateTime().plusDays(1).withTimeAtStartOfDay().toLocalDate();

    @Autowired
    private PersonService personService;

    @Autowired
    private TaskSearch taskSearch;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskOperationRest taskOperationRest;

    @Scheduled(cron = "0 0 21 * * *")
    public void rememberAllAboutUnCommentedTasks() {

        today = new DateTime().withTimeAtStartOfDay().toLocalDate();

        tomorrow = new DateTime().plusDays(1).withTimeAtStartOfDay().toLocalDate();

        Lists.newArrayList(personService.findAll()).stream().forEach(person -> {
            //Get all opened incoming tasks for this person
            List<Task> tasks = taskSearch.search(null, null, null, null, null, null, null, true, true, "All", person.getId());
            tasks.stream().forEach(task -> {
                long operationsCountToday = taskOperationService.countByTaskAndSenderAndDateBetween(task, person, today.toDate(), tomorrow.toDate());
                if (operationsCountToday == 0) {
                    try {
                        TaskOperation taskOperation = new TaskOperation();
                        taskOperation.setContent("تم وقوع خصومات بمقدار 50 ريال سعودي على " + person.getName() + " وذلك لعدم التفاعل مع المهمة رقم " + task.getCode() + " اليوم، نرجو منه مراجعة جهة التكليف.");
                        taskOperation.setTask(task);
                        taskOperationRest.create(taskOperation, task.getPerson());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }
}
