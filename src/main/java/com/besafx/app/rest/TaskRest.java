package com.besafx.app.rest;
import com.besafx.app.config.CustomException;
import com.besafx.app.config.EmailSender;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskTo;
import com.besafx.app.entity.Views;
import com.besafx.app.search.TaskSearch;
import com.besafx.app.service.*;
import com.besafx.app.util.DateConverter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

@RestController
@RequestMapping(value = "/api/task/")
public class TaskRest {

    private final static Logger log = LoggerFactory.getLogger(TaskRest.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskToService taskToService;

    @Autowired
    private TaskCloseRequestService taskCloseRequestService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskOperationAttachService taskOperationAttachService;

    @Autowired
    private TaskWarnService taskWarnService;

    @Autowired
    private TaskDeductionService taskDeductionService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private TaskSearch taskSearch;

    @Autowired
    private PersonRest personRest;

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_CREATE')")
    public synchronized void create(
            @RequestBody Task task,
            @RequestParam(value = "personsList") List<Long> personsList,
            @RequestParam(value = "single") Boolean single,
            Principal principal) throws IOException {
        Person person = personService.findByEmail(principal.getName());
        if (single) {
            createSingle(task, personsList, principal, person);
        } else {
            createShare(task, personsList, principal, person);
        }
    }

    private void createShare(Task task, List<Long> personsList, Principal principal, Person person) throws IOException {
        log.info("المهمة المشتركة هي التى تجمع بين عدة موظفين");
        Task topTask = taskService.findTopByOrderByCodeDesc();
        if (topTask == null) {
            task.setCode(1);
        } else {
            task.setCode(topTask.getCode() + 1);
        }
        task.setStartDate(new Date());
        task.setEndDate(new DateTime(task.getEndDate()).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(59).toDate());
        task.setCloseType(Task.CloseType.Pending);
        task.setPerson(person);
        task = taskService.save(task);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على المهام")
                .message("تم اضافة مهمة جديدة رقم " + task.getCode() + " بنجاح")
                .type("success")
                .icon("fa-plus-circle")
                .build(), principal.getName());
        ListIterator<Long> listIterator = personsList.listIterator();
        while (listIterator.hasNext()) {
            Person toPerson = personService.findOne(listIterator.next());
            TaskTo taskTo = new TaskTo();
            taskTo.setTask(task);
            taskTo.setProgress(0);
            taskTo.setClosed(false);
            taskTo.setPerson(toPerson);
            taskToService.save(taskTo);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم إرسال التكليف الى الموظف  / " + toPerson.getName() + " بنجاح")
                    .type("success")
                    .icon("fa-plus-circle")
                    .build(), principal.getName());
            ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NewTask.html");
            String message = IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
            message = message.replaceAll("TASK_CODE", task.getCode().toString());
            message = message.replaceAll("TASK_TITLE", task.getTitle());
            message = message.replaceAll("TASK_CONTENT", task.getContent());
            message = message.replaceAll("TASK_END_DATE", DateConverter.getHijriStringFromDateRTL(task.getEndDate()));
            message = message.replaceAll("TASK_PERSON", task.getPerson().getName());
            emailSender.send("مهمة جديدة رقم: " + "(" + task.getCode() + ")", message, toPerson.getEmail());
        }
    }

    private void createSingle(Task task, List<Long> personsList, Principal principal, Person person) throws IOException {
        log.info("المهمة المنفردة تعني انشاء كل مهمة لكل فرد على حدا بارقام مختلفة");
        ListIterator<Long> listIterator = personsList.listIterator();
        while (listIterator.hasNext()) {
            Person toPerson = personService.findOne(listIterator.next());
            Task topTask = taskService.findTopByOrderByCodeDesc();
            if (topTask == null) {
                task.setCode(1);
            } else {
                task.setCode(topTask.getCode() + 1);
            }
            entityManager.detach(task);
            task.setId(null);
            task.setStartDate(new Date());
            task.setEndDate(new DateTime(task.getEndDate()).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(59).toDate());
            task.setCloseType(Task.CloseType.Pending);
            task.setPerson(person);
            task = taskService.save(task);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم اضافة مهمة جديدة رقم " + task.getCode() + " بنجاح")
                    .type("success")
                    .icon("fa-plus-circle")
                    .build(), principal.getName());
            log.info("توجية المهمة الى الموظف");
            TaskTo taskTo = new TaskTo();
            taskTo.setTask(task);
            taskTo.setProgress(0);
            taskTo.setClosed(false);
            taskTo.setPerson(toPerson);
            taskToService.save(taskTo);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم إرسال التكليف الى الموظف  / " + toPerson.getName() + " بنجاح")
                    .type("success")
                    .icon("fa-plus-circle")
                    .build(), principal.getName());
            ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NewTask.html");
            String message = IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
            message = message.replaceAll("TASK_CODE", task.getCode().toString());
            message = message.replaceAll("TASK_TITLE", task.getTitle());
            message = message.replaceAll("TASK_CONTENT", task.getContent());
            message = message.replaceAll("TASK_END_DATE", DateConverter.getHijriStringFromDateRTL(task.getEndDate()));
            message = message.replaceAll("TASK_PERSON", task.getPerson().getName());
            emailSender.send("مهمة جديدة رقم: " + "(" + task.getCode() + ")", message, toPerson.getEmail());
        }
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    public Task update(@RequestBody Task task, Principal principal) {
        Task object = taskService.findOne(task.getId());
        if (task.getCloseType().equals(Task.CloseType.Manual)) {
            throw new CustomException("لا يمكن القيام بأي عمليات على مهام الارشيف.");
        }
        if (object == null) {
            throw new CustomException("عفواً ، لا توجد هذة المهمة");
        } else {
            if (!personRest.getPersonManager(object.getPerson()).getEmail().equals(principal.getName())) {
                if (!object.getPerson().getEmail().equals(principal.getName())) {
                    throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف مصرح له بذلك.");
                }
            }
            task = taskService.save(task);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم تعديل بيانات المهمة رقم " + task.getCode() + " بنجاح")
                    .type("success")
                    .icon("fa-edit")
                    .build(), principal.getName());
            return task;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_DELETE')")
    public void delete(@PathVariable Long id, Principal principal) {
        Task object = taskService.findOne(id);
        if (object == null) {
            throw new CustomException("عفواً ، لا توجد هذة المهمة");
        } else {
            if (!object.getPerson().getEmail().equalsIgnoreCase(principal.getName())) {
                throw new CustomException("عفواً، لا يمكنك حذف مهمة لم تضيفها");
            }
            taskWarnService.delete(object.getTaskWarns());
            taskDeductionService.delete(object.getTaskDeductions());
            taskToService.delete(object.getTaskTos());
            taskCloseRequestService.delete(object.getTaskCloseRequests());
            object.getTaskOperations().stream().forEach(taskOperation -> taskOperationAttachService.delete(taskOperation.getTaskOperationAttaches()));
            taskOperationService.delete(object.getTaskOperations());
            taskService.delete(object);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم حذف المهمة رقم " + object.getCode() + " بنجاح")
                    .type("success")
                    .icon("fa-black-tie")
                    .build(), principal.getName());
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Task> findAll() {
        return Lists.newArrayList(taskService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Task findOne(@PathVariable Long id) {
        return taskService.findOne(id);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return taskService.count();
    }

    @RequestMapping(value = "filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @JsonView(Views.Summery.class)
    public List<Task> filter(
            @RequestParam(value = "title", required = false) final String title,
            @RequestParam(value = "importance", required = false) final Task.Importance importance,
            @RequestParam(value = "closeType", required = false) final Task.CloseType closeType,
            @RequestParam(value = "codeFrom", required = false) final Long codeFrom,
            @RequestParam(value = "codeTo", required = false) final Long codeTo,
            @RequestParam(value = "startDateFrom", required = false) final Long startDateFrom,
            @RequestParam(value = "startDateTo", required = false) final Long startDateTo,
            @RequestParam(value = "endDateFrom", required = false) final Long endDateFrom,
            @RequestParam(value = "endDateTo", required = false) final Long endDateTo,
            @RequestParam(value = "taskType") final Boolean taskType,
            @RequestParam(value = "isTaskOpen", required = false) final Boolean isTaskOpen,
            @RequestParam(value = "timeType") final String timeType,
            @RequestParam(value = "person") final Long person) {
        return taskSearch.search(title, importance, closeType, codeFrom, codeTo, startDateFrom, startDateTo, endDateFrom, endDateTo, taskType, isTaskOpen, timeType, person);
    }

}
