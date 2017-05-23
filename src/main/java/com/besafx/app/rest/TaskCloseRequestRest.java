package com.besafx.app.rest;
import com.besafx.app.config.CustomException;
import com.besafx.app.config.EmailSender;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskCloseRequest;
import com.besafx.app.search.TaskCloseRequestSearch;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.TaskCloseRequestService;
import com.besafx.app.service.TaskService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/taskCloseRequest/")
public class TaskCloseRequestRest {

    private final static Logger log = LoggerFactory.getLogger(TaskCloseRequest.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonRest personRest;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskCloseRequestService taskCloseRequestService;

    @Autowired
    private TaskCloseRequestSearch taskCloseRequestSearch;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailSender emailSender;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskCloseRequest create(@RequestBody TaskCloseRequest taskCloseRequest, Principal principal) {
        if (taskCloseRequest.getTask().getCloseType().equals(Task.CloseType.Manual)) {
            throw new CustomException("لا يمكن القيام بأي عمليات على مهام الارشيف.");
        }
        if (!taskCloseRequest.getTask().getTaskTos().stream().map(to -> to.getPerson().getEmail()).collect(Collectors.toList()).contains(principal.getName())) {
            throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط الموظفين المكلفين بإمكانهم ذلك.");
        }
        try {
            Person person = personService.findByEmail(principal.getName());
            TaskCloseRequest tempTaskCloseRequest = taskCloseRequestService.findTopByTaskIdOrderByCodeDesc(taskCloseRequest.getTask().getId());
            if (tempTaskCloseRequest == null) {
                taskCloseRequest.setCode(1);
            } else {
                taskCloseRequest.setCode(tempTaskCloseRequest.getCode() + 1);
            }
            taskCloseRequest.setDate(new Date());
            taskCloseRequest.setPerson(person);
            taskCloseRequestService.save(taskCloseRequest);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم إرسال الطلب بنجاح")
                    .type("success")
                    .icon(taskCloseRequest.getType() ? "fa-power-off" : "fa-battery")
                    .build(), principal.getName());
            ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/TaskCloseRequest.html");
            String message = IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
            message = message.replaceAll("TASK_CODE", " [ " + taskCloseRequest.getTask().getCode() + " ] " + taskCloseRequest.getTask().getTitle());
            message = message.replaceAll("TASK_CLOSE_REQUEST_PERSON", person.getNickname() + " / " + person.getName());
            message = message.replaceAll("TASK_CLOSE_REQUEST_NOTE", taskCloseRequest.getNote());
            emailSender.send((taskCloseRequest.getType() ? "طلب إغلاق إلى المهمة رقم: " : "طلب تمديد للمهمة رقم: ") + "(" + taskCloseRequest.getTask().getCode() + ")" + " - " + person.getName(), message, taskCloseRequest.getTask().getPerson().getEmail());
            return taskCloseRequest;

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional
    public void delete(@PathVariable Long id, Principal principal) throws IOException {
        TaskCloseRequest object = taskCloseRequestService.findOne(id);
        if (object != null) {
            taskCloseRequestService.delete(object);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على طلبات الإغلاق/التمديد")
                    .message("تم حذف طلب الإغلاق بنجاح")
                    .type("success")
                    .icon("fa-trash")
                    .build(), principal.getName());
        } else {
            throw new CustomException("هذا الطلب لم يعد موجوداً، فضلاً قم بإعادة التحميل او التأكد من رقم الطلب المراد حذفه");
        }
    }

    @RequestMapping(value = "delete/{listId}", method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional
    public void delete(@PathVariable List<Long> listId, Principal principal) throws IOException {
        List<TaskCloseRequest> object = taskCloseRequestService.findByIdIn(listId);
        if (!object.isEmpty()) {
            taskCloseRequestService.delete(object);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على طلبات الإغلاق/التمديد")
                    .message("تم حذف طلبات الإغلاق بنجاح")
                    .type("success")
                    .icon("fa-trash")
                    .build(), principal.getName());
        } else {
            throw new CustomException("هذة الطلبات لم تعد موجودة، فضلاً قم بإعادة التحميل او التأكد من أرقام الطلبات المراد حذفها");
        }
    }

    @RequestMapping(value = "deleteByTaskAndType/{taskId}/{type}", method = RequestMethod.DELETE)
    @ResponseBody
    @Transactional
    public void deleteByTaskAndType(@PathVariable(value = "taskId") Long taskId, @PathVariable(value = "type") Boolean type, Principal principal) throws IOException {
        Task task = taskService.findOne(taskId);
        if (!personRest.getPersonManager(task.getPerson()).getEmail().equals(principal.getName())) {
            if (!task.getPerson().getEmail().equals(principal.getName())) {
                throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف بإمكانه ذلك.");
            }
        }
        List<TaskCloseRequest> object = taskCloseRequestService.findByTaskIdAndType(taskId, type);
        if (!object.isEmpty()) {
            taskCloseRequestService.delete(object);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على طلبات الإغلاق/التمديد")
                    .message("تم حذف الطلبات بنجاح")
                    .type("success")
                    .icon("fa-trash")
                    .build(), principal.getName());
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
