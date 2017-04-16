package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.config.EmailSender;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskDeduction;
import com.besafx.app.search.TaskSearch;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.TaskDeductionService;
import com.besafx.app.service.TaskService;
import com.besafx.app.util.DateConverter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/taskDeduction/")
public class TaskDeductionRest {

    private final Logger log = LoggerFactory.getLogger(TaskDeductionRest.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskDeductionService taskDeductionService;

    @Autowired
    private TaskSearch taskSearch;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailSender emailSender;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskDeduction create(@RequestBody TaskDeduction taskDeduction, Principal principal) {
        if (!taskDeduction.getTask().getPerson().getEmail().equals(principal.getName())) {
            throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف بإمكانه ارسال الخصومات");
        }
        try {
            Person person = personService.findByEmail(principal.getName());
            TaskDeduction tempTaskDeduction = taskDeductionService.findTopByTaskAndToPersonOrderByCodeDesc(taskDeduction.getTask(), taskDeduction.getToPerson());
            if (tempTaskDeduction == null) {
                taskDeduction.setCode(1);
            } else {
                taskDeduction.setCode(tempTaskDeduction.getCode() + 1);
            }
            taskDeduction.setDate(new Date());
            taskDeduction = taskDeductionService.save(taskDeduction);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم إرسال الخصم بنجاح")
                    .type("success")
                    .icon("fa-black-tie")
                    .build(), person.getName());
            ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NoTaskOperationsWarning.html");
            String message = org.apache.commons.io.IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
            message = message.replaceAll("MESSAGE", "خصم من " + taskDeduction.getTask().getPerson().getNickname() + " / " + taskDeduction.getTask().getPerson().getName() + " بشأن المهمة رقم " + "(" + taskDeduction.getTask().getCode() + ")" + " وفيما يلي محتوى الخصم" + "<br/>" + "<u>" + taskDeduction.getContent() + "</u>");
            String title = "خصم من " + taskDeduction.getTask().getPerson().getNickname() + " / " + taskDeduction.getTask().getPerson().getName() + " بقيمة " + taskDeduction.getDeduction() + " ريال سعودي";
            emailSender.send(title, message, taskDeduction.getToPerson().getEmail());
            return taskDeduction;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @RequestMapping(value = "clearCounters/{taskId}/{personId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean clearCounters(@PathVariable(value = "taskId") Long taskId, @PathVariable(value = "personId") Long personId, Principal principal) {
        if (!taskService.findOne(taskId).getPerson().getEmail().equals(principal.getName())) {
            throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف بإمكانه حذف الخصومات");
        }
        taskDeductionService.delete(taskDeductionService.findByTaskIdAndToPersonIdAndType(taskId, personId, TaskDeduction.TaskDeductionType.Auto));
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على المهام")
                .message("تم حذف كل الخصومات الإلكترونية على الموظف بالنسبة لهذة المهمة")
                .type("success")
                .icon("fa-black-tie")
                .build(), principal.getName());
        return true;
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskDeduction> findAll() {
        return Lists.newArrayList(taskDeductionService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskDeduction findOne(@PathVariable Long id) {
        return taskDeductionService.findOne(id);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return taskDeductionService.count();
    }

    @RequestMapping(value = "findByTask/{taskId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskDeduction> findByTask(@PathVariable(value = "taskId") Long taskId) {
        Task task = taskService.findOne(taskId);
        if (task == null) {
            throw new CustomException("لا توجد هذة المهمة، فضلاً تأكد من الرقم الصحيح");
        }
        return taskDeductionService.findByTask(task);
    }

    @RequestMapping(value = "findByTaskAndType/{taskId}/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskDeduction> findByTaskAndType(@PathVariable(value = "taskId") Long taskId, @PathVariable(value = "type") TaskDeduction.TaskDeductionType type) {
        Task task = taskService.findOne(taskId);
        if (task == null) {
            throw new CustomException("لا توجد هذة المهمة، فضلاً تأكد من الرقم الصحيح");
        }
        return taskDeductionService.findByTaskAndType(task, type);
    }

    @RequestMapping(value = "findIncomingWarnsForMe/{timeType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskDeduction> findIncomingWarnsForMe(@PathVariable(value = "timeType") String timeType, Principal principal) {
        List<Task> tasks = taskSearch.getIncomingOpenedTasks("All", personService.findByEmail(principal.getName()).getId());
        return getTaskDeductions(timeType, tasks);
    }

    @RequestMapping(value = "findOutgoingWarnsForMe/{timeType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskDeduction> findOutgoingWarnsForMe(@PathVariable(value = "timeType") String timeType, Principal principal) {
        List<Task> tasks = taskSearch.getOutgoingOpenedTasks("All", personService.findByEmail(principal.getName()).getId());
        return getTaskDeductions(timeType, tasks);
    }

    private List<TaskDeduction> getTaskDeductions(String timeType, List<Task> tasks) {
        List<TaskDeduction> taskDeductions = new ArrayList<>();
        DateTime today = new DateTime().withTimeAtStartOfDay();
        DateTime tomorrow = new DateTime().plusDays(1).withTimeAtStartOfDay();
        switch (timeType) {
            case "Day":
                taskDeductions.addAll(taskDeductionService.findByTaskInAndDateBetween(tasks, today.toDate(), tomorrow.toDate()));
                break;
            case "Week":
                Date weekStart = DateConverter.getCurrentWeekStart();
                Date weekEnd = DateConverter.getCurrentWeekEnd();
                log.info("البحث عن خصومات مهام الأسبوع");
                log.info(DateConverter.getDateInFormatWithTime(weekStart));
                log.info(DateConverter.getDateInFormatWithTime(weekEnd));
                taskDeductions.addAll(taskDeductionService.findByTaskInAndDateBetween(tasks, weekStart, weekEnd));
                break;
            case "Month":
                DateTime monthStart = today.withDayOfMonth(1);
                DateTime monthEnd = monthStart.plusMonths(1).minusDays(1);
                log.info("البحث عن خصومات مهام الشهر");
                log.info(DateConverter.getDateInFormatWithTime(monthEnd.toDate()));
                log.info(DateConverter.getDateInFormatWithTime(monthEnd.toDate()));
                taskDeductions.addAll(taskDeductionService.findByTaskInAndDateBetween(tasks, monthStart.toDate(), monthEnd.toDate()));
                break;
            case "Year":
                DateTime yearStart = today.withDayOfYear(1);
                DateTime yearEnd = yearStart.plusYears(1).minusDays(1);
                log.info("البحث عن خصومات مهام العام");
                log.info(DateConverter.getDateInFormatWithTime(yearStart.toDate()));
                log.info(DateConverter.getDateInFormatWithTime(yearEnd.toDate()));
                taskDeductions.addAll(taskDeductionService.findByTaskInAndDateBetween(tasks, yearStart.toDate(), yearEnd.toDate()));
                break;
        }
        return taskDeductions;
    }

}
