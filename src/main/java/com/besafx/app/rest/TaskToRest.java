package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskTo;
import com.besafx.app.service.TaskService;
import com.besafx.app.service.TaskToService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/taskTo/")
public class TaskToRest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskToService taskToService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskTo create(@RequestBody TaskTo taskTo, Principal principal) throws IOException {
        taskTo = taskToService.save(taskTo);
        return taskTo;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskTo update(@RequestBody TaskTo taskTo, Principal principal) throws IOException {
        if (!taskTo.getPerson().getEmail().equalsIgnoreCase(principal.getName())) {
            throw new CustomException("عفواً، لا يمكنك تعديل نسبة إنجاز مهام موظف آخر.");
        }
        taskTo = taskToService.save(taskTo);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على المهام")
                .message("تمت العملية بنجاح.")
                .type("success")
                .icon("fa-hourglass-2")
                .build(), principal.getName());
        return taskTo;
    }

    @RequestMapping(value = "setClosed", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskTo setClosed(@RequestBody TaskTo taskTo, Principal principal) throws IOException {
        Task task = taskService.findOne(taskTo.getTask().getId());
        if (!task.getPerson().getEmail().equalsIgnoreCase(principal.getName())) {
            throw new CustomException("عفواً، مسموح فقط لجهة التكليف بإغلاق المهمة.");
        }
        taskTo.setCloseDate(taskTo.getClosed() ? new Date() : null);
        taskTo = taskToService.save(taskTo);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على المهام")
                .message("تمت العملية بنجاح.")
                .type("success")
                .icon("fa-power-off")
                .build(), principal.getName());
        return taskTo;
    }


    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable Long id, Principal principal) {
        TaskTo object = taskToService.findOne(id);
        if (object == null) {
            throw new CustomException("عفواً ، لا يوجد هذا الموظف المكلف");
        } else {
            taskToService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskTo> findAll() {
        return Lists.newArrayList(taskToService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskTo findOne(@PathVariable Long id) {
        return taskToService.findOne(id);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return taskToService.count();
    }

}
