package com.besafx.app.component;

import com.besafx.app.config.EmailSender;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskOperation;
import com.besafx.app.search.TaskSearch;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.TaskOperationService;
import com.besafx.app.service.TaskService;
import com.besafx.app.util.DateConverter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

@Component
public class ScheduledTasks {

    private final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskSearch taskSearch;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private EmailSender emailSender;

    @Scheduled(cron = "0 0 2 * * SUN,MON,TUE,WED,THU")
    public void warnAllAboutUnCommentedTasksAtMidNight() {

        //Run Morning task (Time of execution = 2)
        //Round(Evening)
        DateTime startLast24Hour = new DateTime().minusDays(1).withTime(2, 0, 0, 0);
        DateTime startLast12Hour = new DateTime().minusDays(1).withTime(14, 0, 0, 0);
        DateTime endLast12Hour = new DateTime().withTime(2, 0, 0, 0);

        log.info("عدد المهام = " + taskService.count());

        log.info("عدد الافراد = " + personService.count());

        log.info("فحص كل فرد على حدا");

        chech(startLast24Hour, startLast12Hour, endLast12Hour);
    }

    @Scheduled(cron = "0 0 14 * * SUN,MON,TUE,WED,THU")
    public void warnAllAboutUnCommentedTasksAtAfternoon() {

        //Run evening task (Time of execution = 14)
        //Round(Morning)
        DateTime startLast24Hour = new DateTime().minusDays(1).withTime(14, 0, 0, 0);
        DateTime startLast12Hour = new DateTime().withTime(2, 0, 0, 0);
        DateTime endLast12Hour = new DateTime().withTime(14, 0, 0, 0);

        log.info("عدد المهام = " + taskService.count());

        log.info("عدد الافراد = " + personService.count());

        log.info("فحص كل فرد على حدا");

        chech(startLast24Hour, startLast12Hour, endLast12Hour);

    }

    private void chech(DateTime startLast24Hour, DateTime startLast12Hour, DateTime endLast12Hour) {
        personService.findAll().forEach(person -> {

            log.info("////////////////////////////////" + person.getName() + "////////////////////////////////////////");

            log.info("فحص المهام الواردة السارية للموظف / " + person.getName());

            List<Task> tasks = taskSearch.search(null, null, null, null, null, null, null, true, true, "All", person.getId());

            log.info("عدد المهام المكلف بها = " + tasks.size());

            log.info("فحص كل مهمة على حدا");

            tasks.stream().forEach(task -> {

                log.info("البحث عن عدد حركات الموظف " + person.getName() + " على المهمة رقم " + task.getCode());

                long numberOfOperations = taskOperationService.countByTaskAndSenderAndTypeAndDateAfterAndDateBefore(task, person, 1, startLast12Hour.toDate(), endLast12Hour.toDate());

                log.info("عدد الحركات فى الفترة = " + numberOfOperations);

                if (numberOfOperations == 0) {
                    long numberOfWarns = taskOperationService.countByTaskAndSenderAndTypeAndDateAfterAndDateBefore(task, person, 2, startLast24Hour.toDate(), startLast12Hour.toDate());

                    log.info("عدد التحذيرات فى الفترة = " + numberOfWarns);

                    if (numberOfWarns == 0) {
                        //Send Warn
                        try {
                            StringBuilder builder = new StringBuilder();
                            builder.append("تحذير بالخصم بشأن عدم التعامل مع المهمة رقم " + "(" + task.getCode() + ")");
                            builder.append(" ");
                            builder.append("للموظف / " + person.getName());
                            builder.append(" ");
                            builder.append("من الفترة / " + DateConverter.getHijriStringFromDateRTLWithTime(startLast12Hour.toDate()));
                            builder.append(" ");
                            builder.append("إلى الفترة / " + DateConverter.getHijriStringFromDateRTLWithTime(endLast12Hour.toDate()));
                            builder.append(" ");
                            builder.append("نأمل الإلتزام بالتعليق فى خلال مدة لا تزيد عن 12 ساعة.");
                            builder.append("-تجريبي-");
                            log.info("جاري إرسال التحذير...");
                            createEmail(task, builder.toString(), 2, task.getPerson(), person);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //Send Discount
                        try {
                            StringBuilder builder = new StringBuilder();
                            builder.append("خصم بمقدار 50 ريال سعودي بشأن عدم التعامل مع المهمة رقم " + "(" + task.getCode() + ")");
                            builder.append(" ");
                            builder.append("للموظف / " + person.getName());
                            builder.append(" ");
                            builder.append("نظراً لتحذيره سابقاً");
                            builder.append(" ");
                            builder.append("من الفترة / " + DateConverter.getHijriStringFromDateRTLWithTime(startLast24Hour.toDate()));
                            builder.append(" ");
                            builder.append("إلى الفترة / " + DateConverter.getHijriStringFromDateRTLWithTime(startLast12Hour.toDate()));
                            builder.append(" ");
                            builder.append("نأمل منه مراجعة جهة التكليف.");
                            builder.append("-تجريبي-");
                            createEmail(task, builder.toString(), 3, task.getPerson(), person);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            });

            log.info("////////////////////////////////" + person.getName() + "////////////////////////////////////////");

        });
    }


    public void createEmail(Task task, String content, Integer type, Person from, Person to) throws IOException {
        log.info("جاري إعداد الحركة وإدراجها...");
        TaskOperation taskOperation = new TaskOperation();
        Integer maxCode = taskOperationService.findLastCodeByTask(task.getId());
        if (maxCode == null) {
            taskOperation.setCode(1);
        } else {
            taskOperation.setCode(maxCode + 1);
        }
        taskOperation.setDate(new Date());
        taskOperation.setTask(task);
        taskOperation.setSender(from);
        taskOperation.setContent(content);
        taskOperation.setType(type);
        taskOperationService.save(taskOperation);
        log.info("تم حفظ الحركة الآلية باسم جهة التكليف");

        ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NoTaskOperationsWarning.html");
        String message = org.apache.commons.io.IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
        message = message.replaceAll("MESSAGE", content);

        String title = type.intValue() == 2 ? "تحذير بالخصم لعدم التعامل مع المهمة رقم " + "(" + task.getCode() + ")" : "خصم لعدم التعامل مع المهمة رقم " + "(" + task.getCode() + ")";

        emailSender.send(title, message, to.getEmail());
    }
}
