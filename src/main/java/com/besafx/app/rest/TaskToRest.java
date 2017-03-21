package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.TaskTo;
import com.besafx.app.service.TaskToService;
import com.besafx.app.util.NotifyCode;
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
    private TaskToService taskToService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskTo create(@RequestBody TaskTo taskTo, Principal principal) throws IOException {
        taskTo = taskToService.save(taskTo);
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
