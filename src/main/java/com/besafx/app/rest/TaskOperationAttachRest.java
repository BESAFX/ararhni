package com.besafx.app.rest;

import com.besafx.app.entity.TaskOperationAttach;
import com.besafx.app.service.TaskOperationAttachService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/taskOperationAttach/")
public class TaskOperationAttachRest {

    @Autowired
    private TaskOperationAttachService taskOperationAttachService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskOperationAttach create(@RequestBody TaskOperationAttach taskOperationAttach) {
        return taskOperationAttachService.save(taskOperationAttach);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskOperationAttach update(@RequestBody TaskOperationAttach taskOperationAttach) {
        TaskOperationAttach object = taskOperationAttachService.findOne(taskOperationAttach.getId());
        if (object != null) {
            return taskOperationAttachService.save(taskOperationAttach);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable Long id) {
        TaskOperationAttach object = taskOperationAttachService.findOne(id);
        if (object != null) {
            taskOperationAttachService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskOperationAttach> findAll() {
        return Lists.newArrayList(taskOperationAttachService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskOperationAttach findOne(@PathVariable Long id) {
        return taskOperationAttachService.findOne(id);
    }

}
