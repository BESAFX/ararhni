package com.besafx.app.rest;
import com.besafx.app.config.CustomException;
import com.besafx.app.config.EmailSender;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskWarn;
import com.besafx.app.search.TaskSearch;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.TaskService;
import com.besafx.app.service.TaskWarnService;
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

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/taskWarn/")
public class TaskWarnRest {

    private final Logger log = LoggerFactory.getLogger(TaskWarnRest.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskWarnService taskWarnService;

    @Autowired
    private TaskSearch taskSearch;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private PersonRest personRest;

    @RequestMapping(value = "clearCounters/{taskId}/{personId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean clearCounters(@PathVariable(value = "taskId") Long taskId, @PathVariable(value = "personId") Long personId, Principal principal) {
        Task task = taskService.findOne(taskId);
        if (!personRest.getPersonManager(task.getPerson()).getEmail().equals(principal.getName())) {
            if (!task.getPerson().getEmail().equals(principal.getName())) {
                throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف بإمكانه حذف التحذيرات");
            }
        }
        taskWarnService.delete(taskWarnService.findByTaskIdAndToPersonIdAndType(taskId, personId, TaskWarn.TaskWarnType.Auto));
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على المهام")
                .message("تم حذف كل التحذيرات الإلكترونية على الموظف بالنسبة لهذة المهمة")
                .type("success")
                .icon("fa-black-tie")
                .build(), principal.getName());
        return true;
    }

    @RequestMapping(value = "clearAllCounters/{taskId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean clearAllCounters(@PathVariable(value = "taskId") Long taskId, Principal principal) {
        Task task = taskService.findOne(taskId);
        if (!personRest.getPersonManager(task.getPerson()).getEmail().equals(principal.getName())) {
            if (!task.getPerson().getEmail().equals(principal.getName())) {
                throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف بإمكانه حذف التحذيرات");
            }
        }
        taskWarnService.delete(taskWarnService.findByTaskIdAndType(taskId, TaskWarn.TaskWarnType.Auto));
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على المهام")
                .message("تم حذف كل التحذيرات الإلكترونية لهذة المهمة")
                .type("success")
                .icon("fa-trash")
                .build(), principal.getName());
        return true;
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskWarn> findAll() {
        return Lists.newArrayList(taskWarnService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TaskWarn findOne(@PathVariable Long id) {
        return taskWarnService.findOne(id);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return taskWarnService.count();
    }

    @RequestMapping(value = "findByTask/{taskId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskWarn> findByTask(@PathVariable(value = "taskId") Long taskId) {
        Task task = taskService.findOne(taskId);
        if (task == null) {
            throw new CustomException("لا توجد هذة المهمة، فضلاً تأكد من الرقم الصحيح");
        }
        return taskWarnService.findByTask(task);
    }

    @RequestMapping(value = "findByTaskAndType/{taskId}/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskWarn> findByTaskAndType(@PathVariable(value = "taskId") Long taskId, @PathVariable(value = "type") TaskWarn.TaskWarnType type) {
        Task task = taskService.findOne(taskId);
        if (task == null) {
            throw new CustomException("لا توجد هذة المهمة، فضلاً تأكد من الرقم الصحيح");
        }
        return taskWarnService.findByTaskAndType(task, type);
    }

    @RequestMapping(value = "findIncomingWarnsForMe/{timeType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskWarn> findIncomingWarnsForMe(@PathVariable(value = "timeType") String timeType, Principal principal) {
        List<Task> tasks = taskSearch.getIncomingOpenedTasks("All", personService.findByEmail(principal.getName()).getId());
        return getTaskWarns(timeType, tasks);
    }

    @RequestMapping(value = "findOutgoingWarnsForMe/{timeType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskWarn> findOutgoingWarnsForMe(@PathVariable(value = "timeType") String timeType, Principal principal) {
        List<Task> tasks = taskSearch.getOutgoingOpenedTasks("All", personService.findByEmail(principal.getName()).getId());
        return getTaskWarns(timeType, tasks);
    }

    private List<TaskWarn> getTaskWarns(String timeType, List<Task> tasks) {
        List<TaskWarn> taskWarns = new ArrayList<>();
        DateTime today = new DateTime().withTimeAtStartOfDay();
        DateTime tomorrow = new DateTime().plusDays(1).withTimeAtStartOfDay();
        switch (timeType) {
            case "Day":
                taskWarns.addAll(taskWarnService.findByTaskInAndDateBetween(tasks, today.toDate(), tomorrow.toDate()));
                break;
            case "Week":
                Date weekStart = DateConverter.getCurrentWeekStart();
                Date weekEnd = DateConverter.getCurrentWeekEnd();
                log.info("البحث عن تحذيرات مهام الأسبوع");
                log.info(DateConverter.getDateInFormatWithTime(weekStart));
                log.info(DateConverter.getDateInFormatWithTime(weekEnd));
                taskWarns.addAll(taskWarnService.findByTaskInAndDateBetween(tasks, weekStart, weekEnd));
                break;
            case "Month":
                DateTime monthStart = today.withDayOfMonth(1);
                DateTime monthEnd = monthStart.plusMonths(1).minusDays(1);
                log.info("البحث عن تحذيرات مهام الشهر");
                log.info(DateConverter.getDateInFormatWithTime(monthEnd.toDate()));
                log.info(DateConverter.getDateInFormatWithTime(monthEnd.toDate()));
                taskWarns.addAll(taskWarnService.findByTaskInAndDateBetween(tasks, monthStart.toDate(), monthEnd.toDate()));
                break;
            case "Year":
                DateTime yearStart = today.withDayOfYear(1);
                DateTime yearEnd = yearStart.plusYears(1).minusDays(1);
                log.info("البحث عن تحذيرات مهام العام");
                log.info(DateConverter.getDateInFormatWithTime(yearStart.toDate()));
                log.info(DateConverter.getDateInFormatWithTime(yearEnd.toDate()));
                taskWarns.addAll(taskWarnService.findByTaskInAndDateBetween(tasks, yearStart.toDate(), yearEnd.toDate()));
                break;
        }
        return taskWarns;
    }

}
