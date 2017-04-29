package com.besafx.app.rest;
import com.besafx.app.config.CustomException;
import com.besafx.app.config.EmailSender;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskOperation;
import com.besafx.app.entity.TaskOperationAttach;
import com.besafx.app.search.TaskSearch;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.TaskOperationAttachService;
import com.besafx.app.service.TaskOperationService;
import com.besafx.app.service.TaskService;
import com.besafx.app.util.DateConverter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

@RestController
@RequestMapping(value = "/api/taskOperation/")
public class TaskOperationRest {

    private final Logger log = LoggerFactory.getLogger(TaskOperationRest.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskOperationAttachService taskOperationAttachService;

    @Autowired
    private TaskSearch taskSearch;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailSender emailSender;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskOperation create(@RequestBody TaskOperation taskOperation, Principal principal) throws IOException {
        return create(taskOperation, personService.findByEmail(principal.getName()));
    }

    public TaskOperation create(TaskOperation taskOperation, Person person) throws IOException {
        if (taskOperation.getTask().getCloseType().equals(Task.CloseType.Manual)) {
            throw new CustomException("لا يمكن القيام بأي عمليات على مهام الارشيف.");
        }
        taskOperation.getTask().getTaskTos()
                .stream()
                .filter(taskTo -> taskTo.getPerson().getId().intValue() == person.getId() && taskTo.getClosed())
                .findAny().ifPresent(value -> {
            throw new CustomException("عفواً، تم إغلاق المهمة عليك ولن يمكنك اضافة حركات او تعليقات.");
        });
        if (taskOperation.getTask().getEndDate().before(new Date())) {
            throw new CustomException("لا يمكن اضافة حركات إلى مهمة مغلقة");
        }
        Integer maxCode = taskOperationService.findLastCodeByTask(taskOperation.getTask().getId());
        if (maxCode == null) {
            taskOperation.setCode(1);
        } else {
            taskOperation.setCode(maxCode + 1);
        }
        taskOperation.setDate(new Date());
        taskOperation.setSender(person);
        taskOperation.setType(TaskOperation.OperationType.Comment);
        taskOperation = taskOperationService.save(taskOperation);
        ListIterator<TaskOperationAttach> listIterator = taskOperation.getTaskOperationAttaches().listIterator();
        while (listIterator.hasNext()) {
            TaskOperationAttach taskOperationAttach = listIterator.next();
            taskOperationAttach.setTaskOperation(taskOperation);
            taskOperationAttachService.save(taskOperationAttach);
        }
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على المهام")
                .message("تم اضافة حركة جديدة بنجاح")
                .type("success")
                .icon("fa-black-tie")
                .build(), person.getName());
//        ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NewOperation.html");
//        String message = org.apache.commons.io.IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
//        message = message.replaceAll("OPERATION_SENDER", taskOperation.getSender().getName());
//        message = message.replaceAll("OPERATION_CODE", taskOperation.getCode().toString());
//        message = message.replaceAll("OPERATION_DATE", DateConverter.getHijriStringFromDateRTL(taskOperation.getDate()));
//        message = message.replaceAll("OPERATION_CONTENT", taskOperation.getContent());
//        message = message.replaceAll("TASK_CODE", taskOperation.getTask().getCode().toString());
//        message = message.replaceAll("TASK_TITLE", taskOperation.getTask().getTitle());
//        message = message.replaceAll("TASK_PERSON", taskOperation.getTask().getPerson().getName());
//
//        if (taskOperation.getSender().getId().longValue() != taskOperation.getTask().getPerson().getId().longValue()) {
//            emailSender.send("حركة جديدة على المهمة رقم: " + "(" + taskOperation.getTask().getCode() + ")", message, taskOperation.getTask().getPerson().getEmail());
//        }
        return taskOperation;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskOperation update(@RequestBody TaskOperation taskOperation, Principal principal) {
        if (taskOperation.getTask().getCloseType().equals(Task.CloseType.Manual)) {
            throw new CustomException("لا يمكن القيام بأي عمليات على مهام الارشيف.");
        }
        if (!taskOperation.getSender().getEmail().equals(principal.getName())) {
            throw new CustomException("لا يمكنك التعديل على حركة لم تضيفها");
        }
        TaskOperation object = taskOperationService.findOne(taskOperation.getId());
        if (object != null) {
            taskOperation = taskOperationService.save(taskOperation);
            return taskOperation;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable Long id, Principal principal) {
        TaskOperation object = taskOperationService.findOne(id);
        if (!object.getSender().getEmail().equals(principal.getName())) {
            throw new CustomException("لا يمكنك حذف حركة لم تضيفها");
        }
        if (object != null) {
            taskOperationService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskOperation> findAll() {
        return Lists.newArrayList(taskOperationService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskOperation findOne(@PathVariable Long id) {
        return taskOperationService.findOne(id);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return taskOperationService.count();
    }

    @RequestMapping(value = "findByTask/{taskId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskOperation> findByTask(@PathVariable(value = "taskId") Long taskId) {
        Task task = taskService.findOne(taskId);
        if (task == null) {
            throw new CustomException("لا توجد هذة المهمة، فضلاً تأكد من الرقم الصحيح");
        }
        return taskOperationService.findByTask(task);
    }

    @RequestMapping(value = "findIncomingOperationsForMe/{timeType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskOperation> findIncomingOperationsForMe(@PathVariable(value = "timeType") String timeType, Principal principal) {
        List<Task> tasks = taskSearch.getIncomingOpenedTasks("All", personService.findByEmail(principal.getName()).getId());
        return getTaskOperations(timeType, tasks);
    }

    @RequestMapping(value = "findOutgoingOperationsForMe/{timeType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskOperation> findOutgoingOperationsForMe(@PathVariable(value = "timeType") String timeType, Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        List<Task> tasks = taskSearch.getOutgoingOpenedTasks("All", person.getId());
        log.info("عدد المهام الصادرة من " + person.getNickname() + " / " + person.getName() + " تساوي " + tasks.size());
        return getTaskOperations(timeType, tasks);
    }

    public List<TaskOperation> getTaskOperations(String timeType, List<Task> tasks) {
        List<TaskOperation> taskOperations = new ArrayList<>();
        DateTime today = new DateTime().withTimeAtStartOfDay();
        DateTime tomorrow = new DateTime().plusDays(1).withTimeAtStartOfDay();
        switch (timeType) {
            case "Day":
                log.info("البحث عن حركات مهام اليوم");
                log.info(DateConverter.getDateInFormatWithTime(today.toDate()));
                log.info(DateConverter.getDateInFormatWithTime(tomorrow.toDate()));
                tasks.stream().forEach(task -> taskOperations.addAll(taskOperationService.findByTaskAndDateBetween(task, today.toDate(), tomorrow.toDate())));
                break;
            case "Week":
                Date weekStart = DateConverter.getCurrentWeekStart();
                Date weekEnd = DateConverter.getCurrentWeekEnd();
                log.info("البحث عن حركات مهام الأسبوع");
                log.info(DateConverter.getDateInFormatWithTime(weekStart));
                log.info(DateConverter.getDateInFormatWithTime(weekEnd));
                tasks.stream().forEach(task -> taskOperations.addAll(taskOperationService.findByTaskAndDateBetween(task, weekStart, weekEnd)));
                break;
            case "Month":
                DateTime monthStart = today.withDayOfMonth(1);
                DateTime monthEnd = monthStart.plusMonths(1).minusDays(1);
                log.info("البحث عن حركات مهام الشهر");
                log.info(DateConverter.getDateInFormatWithTime(monthEnd.toDate()));
                log.info(DateConverter.getDateInFormatWithTime(monthEnd.toDate()));
                tasks.stream().forEach(task -> taskOperations.addAll(taskOperationService.findByTaskAndDateBetween(task, monthStart.toDate(), monthEnd.toDate())));
                break;
            case "Year":
                DateTime yearStart = today.withDayOfYear(1);
                DateTime yearEnd = yearStart.plusYears(1).minusDays(1);
                log.info("البحث عن حركات مهام العام");
                log.info(DateConverter.getDateInFormatWithTime(yearStart.toDate()));
                log.info(DateConverter.getDateInFormatWithTime(yearEnd.toDate()));
                tasks.stream().forEach(task -> taskOperations.addAll(taskOperationService.findByTaskAndDateBetween(task, yearStart.toDate(), yearEnd.toDate())));
                break;
        }
        return taskOperations;
    }

}
