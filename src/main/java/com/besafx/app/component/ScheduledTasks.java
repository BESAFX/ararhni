package com.besafx.app.component;

import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskOperation;
import com.besafx.app.entity.TaskTo;
import com.besafx.app.rest.TaskOperationRest;
import com.besafx.app.search.TaskSearch;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.TaskOperationService;
import com.besafx.app.service.TaskToService;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class ScheduledTasks {

    private LocalDate yesterday;

    private LocalDate today;

    private LocalDate tomorrow;

    @Autowired
    private PersonService personService;

    @Autowired
    private TaskSearch taskSearch;

    @Autowired
    private TaskToService taskToService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskOperationRest taskOperationRest;

    @Scheduled(cron = "0 0 2 * * *")
    public void warnAllAboutUnCommentedTasksAtMidNight() {

        if (new DateTime().dayOfWeek().getAsText().equalsIgnoreCase("Friday")) {
            return;
        }

        yesterday = new DateTime().minusDays(1).withTimeAtStartOfDay().toLocalDate();
        today = new DateTime().withTimeAtStartOfDay().toLocalDate();

        Lists.newArrayList(personService.findAll()).stream().forEach(person -> {
            //Get all opened incoming tasks for this person
            List<Task> tasks = taskSearch.search(null, null, null, null, null, null, null, true, true, "All", person.getId());
            tasks.stream().forEach(task -> {
                long operationsCountToday = taskOperationService.countByTaskAndSenderAndDateBetween(task, person, yesterday.toDate(), today.toDate());
                if (operationsCountToday == 0) {
                    try {
                        TaskOperation taskOperation = new TaskOperation();
                        taskOperation.setContent("تحذير بالخصم للموظف / " + person.getName() + " وذلك لعدم التفاعل مع المهمة رقم " + task.getCode() + " اليوم، نرجو منه الالتزام باضافة المنجزات يومياً وإلا سيتم خصم 50 ريال سعودي عن كل يوم بدون تفاعل.");
                        taskOperation.setTask(task);
                        taskOperationRest.create(taskOperation, task.getPerson());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    @Scheduled(cron = "0 0/10 14 * * *")
    public void warnAllAboutUnCommentedTasksAtAfternoon() {

//        if (new DateTime().dayOfWeek().getAsText().equalsIgnoreCase("Friday")) {
//            return;
//        }

        //Run evening task (Time of execution = 14)
        //Round(Morning)
        DateTime startLast24Hour = new DateTime().minusDays(1).withTime(14, 0, 0, 0);
        DateTime startLast12Hour = new DateTime().withTime(2, 0, 0, 0);
        DateTime endLast12Hour = new DateTime().withTime(14, 0, 0, 0);

        //Get all opened tasks
        List<Task> tasks = taskSearch.search(null, null, null, null, null, null, null, true, true, "All", null);
        tasks.stream().forEach(task -> {
            taskToService.findByTask(task).stream().map(TaskTo::getPerson).forEach(person -> {
                //Count operations for this person on this task
                long numberOfOperations = taskOperationService.countByTaskAndSenderAndTypeAndDateAfterAndDateBefore(task, person, 1, startLast12Hour.toDate(), endLast12Hour.toDate());
                if (numberOfOperations == 0) {
                    long numberOfWarns = taskOperationService.countByTaskAndSenderAndTypeAndDateAfterAndDateBefore(task, person, 2, startLast24Hour.toDate(), startLast12Hour.toDate());
                    if (numberOfWarns == 0) {
                        //Send Warn
                        try {
                            TaskOperation taskOperation = new TaskOperation();
                            StringBuilder builder = new StringBuilder();
                            builder.append("تحذير بالخصم بشأن عدم التعامل مع المهمة رقم / " + task.getCode());
                            builder.append(" ");
                            builder.append("للموظف / " + person.getName());
                            builder.append(" ");
                            builder.append("من الفترة / " + new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(startLast12Hour.toDate()));
                            builder.append(" ");
                            builder.append("إلى الفترة / " + new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(endLast12Hour.toDate()));
                            builder.append(" ");
                            builder.append("نأمل الإلتزام بالتعليق فى خلال مدة لا تزيد عن 12 ساعة.");
                            taskOperation.setContent(builder.toString());
                            taskOperation.setType(2);
                            taskOperation.setTask(task);
                            taskOperationRest.create(taskOperation, task.getPerson());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //Send Discount
                        try {
                            TaskOperation taskOperation = new TaskOperation();
                            StringBuilder builder = new StringBuilder();
                            builder.append("خصم بمقدار 50 ريال سعودي بشأن عدم التعامل مع المهمة رقم / " + task.getCode());
                            builder.append(" ");
                            builder.append("للموظف / " + person.getName());
                            builder.append(" ");
                            builder.append("نظراً لتحذيره سابقاً");
                            builder.append(" ");
                            builder.append("من الفترة / " + new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(startLast24Hour.toDate()));
                            builder.append(" ");
                            builder.append("إلى الفترة / " + new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(startLast12Hour.toDate()));
                            builder.append(" ");
                            builder.append("نأمل منه مراجعة جهة التكليف.");
                            taskOperation.setContent(builder.toString());
                            taskOperation.setType(3);
                            taskOperation.setTask(task);
                            taskOperationRest.create(taskOperation, task.getPerson());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
    }
}
