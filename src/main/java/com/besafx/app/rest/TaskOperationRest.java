package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.config.EmailSender;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskOperation;
import com.besafx.app.entity.TaskOperationAttach;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.TaskOperationAttachService;
import com.besafx.app.service.TaskOperationService;
import com.besafx.app.service.TaskService;
import com.besafx.app.util.DateConverter;
import com.besafx.app.util.IOUtils;
import com.besafx.app.util.NotifyCode;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/taskOperation/")
public class TaskOperationRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskOperationAttachService taskOperationAttachService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailSender emailSender;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskOperation create(@RequestBody TaskOperation taskOperation, Principal principal) throws IOException {
        if(taskOperation.getTask().getEndDate().before(new Date())){
            throw new CustomException("لا يمكن اضافة حركات إلى مهمة مغلقة");
        }
        Person person = personService.findByEmail(principal.getName());
        Integer maxCode = taskOperationService.findLastCodeByTask(taskOperation.getTask().getId());
        if (maxCode == null) {
            taskOperation.setCode(1);
        } else {
            taskOperation.setCode(maxCode + 1);
        }
        taskOperation.setDate(new Date());
        taskOperation.setSender(person);
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
                .build(), principal.getName());
        notificationService.notifyAllExceptMe(Notification
                .builder()
                .title("العمليات على المهام")
                .message("تم اضافة حركة جديدة بواسطة " +  personService.findByEmail(principal.getName()).getName() + " على المهمة رقم " + taskOperation.getTask().getCode())
                .type("warning")
                .icon("fa-black-tie")
                .build());
        ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NewOperation.html");
        String message = org.apache.commons.io.IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
        message = message.replaceAll("OPERATION_SENDER", taskOperation.getSender().getName());
        message = message.replaceAll("OPERATION_CODE", taskOperation.getCode().toString());
        message = message.replaceAll("OPERATION_DATE", DateConverter.getHijriStringFromDateRTL(taskOperation.getDate()));
        message = message.replaceAll("OPERATION_CONTENT", taskOperation.getContent());
        message = message.replaceAll("TASK_CODE", taskOperation.getTask().getCode().toString());
        message = message.replaceAll("TASK_TITLE", taskOperation.getTask().getTitle());
        message = message.replaceAll("TASK_PERSON", taskOperation.getTask().getPerson().getName());

        final String emailTemp = taskOperation.getSender().getEmail();
        List<String> emails = new ArrayList<>();
        emails.add(taskOperation.getTask().getPerson().getEmail());
        emails.addAll(taskOperation
                .getTask()
                .getTaskTos()
                .stream()
                .filter(to -> !to.getPerson().getEmail().equals(emailTemp))
                .map(to -> to.getPerson().getEmail())
                .collect(Collectors.toList()));
        emailSender.send("حركة جديدة على المهمة رقم: " + "(" + taskOperation.getTask().getCode() + ")", message, emails);
        return taskOperation;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskOperation update(@RequestBody TaskOperation taskOperation, Principal principal) {
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

}
