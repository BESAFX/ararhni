package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.config.EmailSender;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskCloseRequest;
import com.besafx.app.entity.TaskTo;
import com.besafx.app.search.TaskSearch;
import com.besafx.app.service.*;
import com.besafx.app.util.DateConverter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/task/")
public class TaskRest {

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
    private NotificationService notificationService;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private TaskSearch taskSearch;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_CREATE')")
    public Task create(@RequestBody Task task, Principal principal) throws IOException {
        Person person = personService.findByEmail(principal.getName());
        Integer maxCode = taskService.findMaxCode();
        if (maxCode == null) {
            task.setCode(1);
        } else {
            task.setCode(maxCode + 1);
        }
        task.setStartDate(new Date());
        task.setPerson(person);
        task = taskService.save(task);
        ListIterator<TaskTo> listIterator = task.getTaskTos().listIterator();
        while (listIterator.hasNext()) {
            TaskTo taskTo = listIterator.next();
            taskTo.setTask(task);
            taskTo.setProgress(0);
            taskTo.setClosed(false);
            taskToService.save(taskTo);
        }
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على المهام")
                .message("تم اضافة مهمة جديدة رقم " + task.getCode() + " بنجاح")
                .type("success")
                .icon("fa-black-tie")
                .build(), principal.getName());
        notificationService.notifyAllExceptMe(Notification
                .builder()
                .title("العمليات على المهام")
                .message("تم اضافة مهمة جديدة رقم " + task.getCode() + " بواسطة" + person.getName())
                .type("warning")
                .icon("fa-black-tie")
                .build());
        ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NewTask.html");
        String message = IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
        message = message.replaceAll("TASK_CODE", task.getCode().toString());
        message = message.replaceAll("TASK_TITLE", task.getTitle());
        message = message.replaceAll("TASK_CONTENT", task.getContent());
        message = message.replaceAll("TASK_END_DATE", DateConverter.getHijriStringFromDateRTL(task.getEndDate()));
        message = message.replaceAll("TASK_PERSON", task.getPerson().getName());
        emailSender.send("مهمة جديدة رقم: " + "(" + task.getCode() + ")", message, task.getTaskTos().stream().map(to -> to.getPerson().getEmail()).collect(Collectors.toList()));
        return task;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    public Task update(@RequestBody Task task, Principal principal) {
        Task object = taskService.findOne(task.getId());
        if (object == null) {
            throw new CustomException("عفواً ، لا توجد هذة المهمة");
        } else {
            if (!object.getPerson().getEmail().equalsIgnoreCase(principal.getName())) {
                throw new CustomException("عفواً، لا يمكنك التعديل على بيانات مهمة لم تضيفها");
            }
            task = taskService.save(task);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم تعديل بيانات المهمة رقم " + task.getCode() + " بنجاح")
                    .type("success")
                    .icon("fa-black-tie")
                    .build(), principal.getName());
            notificationService.notifyAllExceptMe(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم تعديل بيانات مهمة رقم " + task.getCode() + " بواسطة " + personService.findByEmail(principal.getName()).getName())
                    .type("warning")
                    .icon("fa-black-tie")
                    .build());
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
            notificationService.notifyAllExceptMe(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم حذف المهمة رقم " + object.getCode() + " بواسطة " + personService.findByEmail(principal.getName()).getName())
                    .type("warning")
                    .icon("fa-black-tie")
                    .build());
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

    @RequestMapping(value = "requestClose", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void requestClose(@RequestBody Task task, Principal principal) throws IOException {
        Person person = personService.findByEmail(principal.getName());
        TaskCloseRequest taskCloseRequest = task.getTaskCloseRequests().get(0);
        taskCloseRequest.setTask(task);
        taskCloseRequest.setDate(new Date());
        taskCloseRequest.setPerson(person);
        taskCloseRequestService.save(taskCloseRequest);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على المهام")
                .message("تم إرسال الطلب بنجاح")
                .type("success")
                .icon("fa-power-off")
                .build(), principal.getName());
        notificationService.notifyAllExceptMe(Notification
                .builder()
                .title("العمليات على المهام")
                .message("تم ارسال طلب إغلاق المهمة رقم " + task.getCode() + " بواسطة" + person.getName())
                .type("warning")
                .icon("fa-power-off")
                .build());
        ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/TaskCloseRequest.html");
        String message = IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
        message = message.replaceAll("TASK_CODE", task.getCode().toString());
        message = message.replaceAll("TASK_CLOSE_REQUEST_PERSON", person.getName());
        message = message.replaceAll("TASK_CLOSE_REQUEST_NOTE", taskCloseRequest.getNote());
        emailSender.send("طلب إغلاق إلى المهمة رقم: " + task.getCode(), message, task.getPerson().getEmail());
    }

    @RequestMapping(value = "filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Task> filter(
            @RequestParam(value = "title", required = false) final String title,
            @RequestParam(value = "codeFrom", required = false) final Long codeFrom,
            @RequestParam(value = "codeTo", required = false) final Long codeTo,
            @RequestParam(value = "startDateFrom", required = false) final Long startDateFrom,
            @RequestParam(value = "startDateTo", required = false) final Long startDateTo,
            @RequestParam(value = "endDateFrom", required = false) final Long endDateFrom,
            @RequestParam(value = "endDateTo", required = false) final Long endDateTo,
            @RequestParam(value = "taskType") final Boolean taskType,
            @RequestParam(value = "isTaskOpen") final Boolean isTaskOpen,
            @RequestParam(value = "timeType") final String timeType,
            @RequestParam(value = "person") final Long person) {
        return taskSearch.search(title, codeFrom, codeTo, startDateFrom, startDateTo, endDateFrom, endDateTo, taskType, isTaskOpen, timeType, person);
    }

}
