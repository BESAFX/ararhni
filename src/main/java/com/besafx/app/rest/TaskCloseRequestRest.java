package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskCloseRequest;
import com.besafx.app.entity.TaskOperation;
import com.besafx.app.search.TaskCloseRequestSearch;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.TaskCloseRequestService;
import com.besafx.app.service.TaskService;
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
@RequestMapping(value = "/api/taskCloseRequest/")
public class TaskCloseRequestRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskCloseRequestService taskCloseRequestService;

    @Autowired
    private TaskCloseRequestSearch taskCloseRequestSearch;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskCloseRequest create(@RequestBody TaskCloseRequest taskCloseRequest, Principal principal) throws IOException {
        taskCloseRequest.setDate(new Date());
        taskCloseRequest.setPerson(personService.findByEmail(principal.getName()));
        taskCloseRequest = taskCloseRequestService.save(taskCloseRequest);
        return taskCloseRequest;
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable Long id, Principal principal) throws IOException {
        TaskCloseRequest object = taskCloseRequestService.findOne(id);
        if(object != null){
            taskCloseRequestService.delete(object);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على طلبات الإغلاق")
                    .message("تم حذف طلب الإغلاق بنجاح")
                    .type("success")
                    .icon("fa-power-off")
                    .build(), principal.getName());
        }else{
            throw new CustomException("هذا الطلب لم يعد موجوداً، فضلاً قم بإعادة التحميل او التأكد من رقم الطلب المراد حذفه");
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskCloseRequest> findAll() {
        return Lists.newArrayList(taskCloseRequestService.findAll());
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return taskCloseRequestService.count();
    }

    @RequestMapping(value = "filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskCloseRequest> filter(
            @RequestParam(value = "timeType", required = false) final String timeType,
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            @RequestParam(value = "taskId", required = false) final Long taskId,
            @RequestParam(value = "taskPersonId", required = false) final Long taskPersonId,
            @RequestParam(value = "personId", required = false) final Long personId) {
        return taskCloseRequestSearch.search(timeType, dateFrom, dateTo, taskId, taskPersonId, personId);
    }

}
