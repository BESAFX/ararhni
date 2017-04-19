package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.config.EmailSender;
import com.besafx.app.entity.Task;
import com.besafx.app.entity.TaskCloseRequest;
import com.besafx.app.entity.TaskOperation;
import com.besafx.app.entity.TaskTo;
import com.besafx.app.service.TaskCloseRequestService;
import com.besafx.app.service.TaskOperationService;
import com.besafx.app.service.TaskService;
import com.besafx.app.service.TaskToService;
import com.besafx.app.util.DateConverter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/task/")
public class TaskAction {

    private final static Logger log = LoggerFactory.getLogger(TaskAction.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskCloseRequestService taskCloseRequestService;

    @Autowired
    private TaskToService taskToService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailSender emailSender;

    @RequestMapping(value = "increaseEndDate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    public Task increaseEndDate(@RequestParam(value = "taskId") Long taskId, @RequestParam(value = "days") int days, @RequestParam(value = "message") String message, Principal principal) {
        Task task = taskService.findOne(taskId);
        if (task == null) {
            throw new CustomException("عفواً ، لا توجد هذة المهمة");
        } else {
            try {
                if (!task.getPerson().getEmail().equalsIgnoreCase(principal.getName())) {
                    throw new CustomException("عفواً، لا يمكنك التعديل على بيانات مهمة لم تضيفها");
                }
                task.setEndDate(new DateTime(task.getEndDate()).plusDays(days).toDate());
                task = taskService.save(task);
                notificationService.notifyOne(Notification
                        .builder()
                        .title("العمليات على المهام")
                        .message("تم تمديد تاريخ إستلام المهمة رقم: " + task.getCode() + " بنجاح")
                        .type("success")
                        .icon("fa-battery")
                        .build(), principal.getName());
                ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/ExtendTask.html");
                String email = IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
                email = email.replaceAll("TASK_CODE", task.getCode().toString());
                email = email.replaceAll("TASK_TITLE", task.getTitle());
                email = email.replaceAll("TASK_CONTENT", task.getContent());
                email = email.replaceAll("TASK_END_DATE", DateConverter.getHijriStringFromDateRTL(task.getEndDate()));
                email = email.replaceAll("TASK_PERSON", task.getPerson().getName());
                emailSender.send("تمديد تاريخ إستلام المهمة رقم: " + "(" + task.getCode() + ")", email, task.getTaskTos().stream().map(to -> to.getPerson().getEmail()).collect(Collectors.toList()));

                TaskOperation taskOperation = new TaskOperation();
                TaskOperation tempTaskOperation = taskOperationService.findTopByTaskIdOrderByCodeDesc(taskId);
                if (tempTaskOperation == null) {
                    taskOperation.setCode(1);
                } else {
                    taskOperation.setCode(tempTaskOperation.getCode() + 1);
                }
                taskOperation.setDate(new Date());
                taskOperation.setSender(task.getPerson());
                taskOperation.setTask(task);
                taskOperation.setType(TaskOperation.OperationType.IncreaseEndDate);
                taskOperation.setContent(message);
                taskOperationService.save(taskOperation);

                return task;

            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                return null;
            }
        }
    }

    @RequestMapping(value = "decreaseEndDate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    public Task decreaseEndDate(@RequestParam(value = "taskId") Long taskId, @RequestParam(value = "days") int days, @RequestParam(value = "message") String message, Principal principal) throws IOException {
        Task task = taskService.findOne(taskId);
        if (task == null) {
            throw new CustomException("عفواً ، لا توجد هذة المهمة");
        } else {
            if (!task.getPerson().getEmail().equalsIgnoreCase(principal.getName())) {
                throw new CustomException("عفواً، لا يمكنك التعديل على بيانات مهمة لم تضيفها");
            }
            task.setEndDate(new DateTime(task.getEndDate()).minusDays(days).toDate());
            task = taskService.save(task);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم تعجيل تاريخ إستلام المهمة رقم: " + task.getCode() + " بنجاح")
                    .type("success")
                    .icon("fa-battery")
                    .build(), principal.getName());
            ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/ExtendTask.html");
            String email = IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
            email = email.replaceAll("TASK_CODE", task.getCode().toString());
            email = email.replaceAll("TASK_TITLE", task.getTitle());
            email = email.replaceAll("TASK_CONTENT", task.getContent());
            email = email.replaceAll("TASK_END_DATE", DateConverter.getHijriStringFromDateRTL(task.getEndDate()));
            email = email.replaceAll("TASK_PERSON", task.getPerson().getName());
            emailSender.send("تعجيل تاريخ إستلام المهمة رقم: " + "(" + task.getCode() + ")", email, task.getTaskTos().stream().map(to -> to.getPerson().getEmail()).collect(Collectors.toList()));

            TaskOperation taskOperation = new TaskOperation();
            TaskOperation tempTaskOperation = taskOperationService.findTopByTaskIdOrderByCodeDesc(taskId);
            if (tempTaskOperation == null) {
                taskOperation.setCode(1);
            } else {
                taskOperation.setCode(tempTaskOperation.getCode() + 1);
            }
            taskOperation.setDate(new Date());
            taskOperation.setSender(task.getPerson());
            taskOperation.setTask(task);
            taskOperation.setType(TaskOperation.OperationType.DecreaseEndDate);
            taskOperation.setContent(message);
            taskOperationService.save(taskOperation);

            return task;
        }
    }

    @RequestMapping(value = "declineRequest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    public TaskCloseRequest declineRequest(@RequestParam(value = "requestId") Long requestId, Principal principal) throws IOException {
        TaskCloseRequest taskCloseRequest = taskCloseRequestService.findOne(requestId);
        if (taskCloseRequest == null) {
            throw new CustomException("عفواً ، لا يوجد هذا الطلب");
        } else {
            if (!taskCloseRequest.getTask().getPerson().getEmail().equals(principal.getName())) {
                throw new CustomException("عفواً، غير مصرح لك القيام بهذة العملية، فقط جهة التكليف مصرح لها رفض الطلب");
            }

            try {
                log.info("العمل على الطلب");
                taskCloseRequest.setApproved(false);
                taskCloseRequest.setApprovedDate(new Date());
                taskCloseRequest = taskCloseRequestService.save(taskCloseRequest);
                notificationService.notifyOne(Notification
                        .builder()
                        .title("العمليات على المهام")
                        .message("تم رفض الطلب بنجاح")
                        .type("success")
                        .icon("fa-battery")
                        .build(), principal.getName());
                log.info("نهاية العمل على الطلب");

                log.info("العمل على الحركة");
                TaskOperation taskOperation = new TaskOperation();
                TaskOperation tempTaskOperation = taskOperationService.findTopByTaskIdOrderByCodeDesc(taskCloseRequest.getTask().getId());
                if (tempTaskOperation == null) {
                    taskOperation.setCode(1);
                } else {
                    taskOperation.setCode(tempTaskOperation.getCode() + 1);
                }
                taskOperation.setDate(new Date());
                taskOperation.setSender(taskCloseRequest.getTask().getPerson());
                taskOperation.setTask(taskCloseRequest.getTask());
                if (taskCloseRequest.getType()) {
                    taskOperation.setType(TaskOperation.OperationType.DeclineCloseRequest);
                    taskOperation.setContent("تم رفض طلب إغلاق " + taskCloseRequest.getPerson().getNickname() + " / " + taskCloseRequest.getPerson().getName());
                } else {
                    taskOperation.setType(TaskOperation.OperationType.DeclineIncreaseEndDateRequest);
                    taskOperation.setContent("تم رفض طلب تمديد " + taskCloseRequest.getPerson().getNickname() + " / " + taskCloseRequest.getPerson().getName());
                }
                taskOperationService.save(taskOperation);
                log.info("نهاية العمل على الحركة");

                return taskCloseRequest;
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                return null;
            }
        }
    }

    @RequestMapping(value = "acceptRequest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    public TaskCloseRequest acceptRequest(@RequestParam(value = "requestId") Long requestId, Principal principal) {
        TaskCloseRequest taskCloseRequest = taskCloseRequestService.findOne(requestId);
        if (taskCloseRequest == null) {
            throw new CustomException("عفواً ، لا يوجد هذا الطلب");
        } else {
            if (!taskCloseRequest.getTask().getPerson().getEmail().equals(principal.getName())) {
                throw new CustomException("عفواً، غير مصرح لك القيام بهذة العملية، فقط جهة التكليف مصرح لها قبول الطلب");
            }

            try {
                log.info("العمل على الطلب");
                taskCloseRequest.setApproved(true);
                taskCloseRequest.setApprovedDate(new Date());
                taskCloseRequest = taskCloseRequestService.save(taskCloseRequest);
                notificationService.notifyOne(Notification
                        .builder()
                        .title("العمليات على المهام")
                        .message("تم قبول الطلب بنجاح")
                        .type("success")
                        .icon("fa-battery")
                        .build(), principal.getName());
                log.info("نهاية العمل على الطلب");

                log.info("العمل على الحركة");
                TaskOperation taskOperation = new TaskOperation();
                TaskOperation tempTaskOperation = taskOperationService.findTopByTaskIdOrderByCodeDesc(taskCloseRequest.getTask().getId());
                if (tempTaskOperation == null) {
                    taskOperation.setCode(1);
                } else {
                    taskOperation.setCode(tempTaskOperation.getCode() + 1);
                }
                taskOperation.setDate(new Date());
                taskOperation.setSender(taskCloseRequest.getTask().getPerson());
                taskOperation.setTask(taskCloseRequest.getTask());
                if (taskCloseRequest.getType()) {
                    taskOperation.setType(TaskOperation.OperationType.AcceptCloseRequest);
                    taskOperation.setContent("تم قبول طلب إغلاق " + taskCloseRequest.getPerson().getNickname() + " / " + taskCloseRequest.getPerson().getName());
                } else {
                    taskOperation.setType(TaskOperation.OperationType.AcceptIncreaseEndDateRequest);
                    taskOperation.setContent("تم قبول طلب تمديد " + taskCloseRequest.getPerson().getNickname() + " / " + taskCloseRequest.getPerson().getName());
                }
                taskOperationService.save(taskOperation);
                log.info("نهاية العمل على الحركة");

                return taskCloseRequest;

            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                return null;
            }
        }
    }

    @RequestMapping(value = "closeTaskOnPerson", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    public Task closeTaskOnPerson(@RequestParam(value = "taskId") Long taskId,
                                  @RequestParam(value = "personId") Long personId,
                                  @RequestParam(value = "message") String message,
                                  @RequestParam(value = "degree") TaskTo.PersonDegree degree,
                                  Principal principal) throws IOException {
        Task task = taskService.findOne(taskId);
        if (task == null) {
            throw new CustomException("عفواً ، لا توجد هذة المهمة");
        } else {
            if (!task.getPerson().getEmail().equals(principal.getName())) {
                throw new CustomException("عفواً، غير مصرح لك القيام بهذة العملية، فقط جهة التكليف المصرح لها بإغلاق المهمة.");
            }

            log.info("العمل على تحديث بيانات المهمة");
            TaskTo taskTo = taskToService.findByTaskIdAndPersonId(taskId, personId);
            taskTo.setCloseDate(new Date());
            taskTo.setClosed(true);
            taskTo.setDegree(degree);
            taskToService.save(taskTo);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تمت العملية بنجاح.")
                    .type("success")
                    .icon("fa-power-off")
                    .build(), principal.getName());
            log.info("إنهاء العمل على تحديث بيانات المهمة");

            log.info("فى حال كان الموظفون المكلفين تم إغلاق مهامهم");
            if (task.getTaskTos().stream().filter(to -> !to.getClosed()).collect(Collectors.toList()).isEmpty()) {

                task.setEndDate(new Date());
                taskService.save(task);

                log.info("اضافة حركة جديدة لإغلاق المهمة تلقائي");
                TaskOperation taskOperation = new TaskOperation();
                TaskOperation tempTaskOperation = taskOperationService.findTopByTaskIdOrderByCodeDesc(taskId);
                if (tempTaskOperation == null) {
                    taskOperation.setCode(1);
                } else {
                    taskOperation.setCode(tempTaskOperation.getCode() + 1);
                }
                taskOperation.setDate(new Date());
                taskOperation.setSender(task.getPerson());
                taskOperation.setTask(task);
                taskOperation.setType(TaskOperation.OperationType.CloseTaskAuto);
                taskOperation.setContent("تم إغلاق المهمة تلقائي نظراً لإغلاق المهمة على الموظف الوحيد المكلف.");
                taskOperationService.save(taskOperation);
                log.info("إنهاء العمل على الحركة");
            }

            log.info("العمل على الحركة");
            TaskOperation taskOperation = new TaskOperation();
            TaskOperation tempTaskOperation = taskOperationService.findTopByTaskIdOrderByCodeDesc(taskId);
            if (tempTaskOperation == null) {
                taskOperation.setCode(1);
            } else {
                taskOperation.setCode(tempTaskOperation.getCode() + 1);
            }
            taskOperation.setDate(new Date());
            taskOperation.setSender(task.getPerson());
            taskOperation.setTask(task);
            taskOperation.setType(TaskOperation.OperationType.CloseTaskOnPerson);
            taskOperation.setContent(message);
            taskOperationService.save(taskOperation);
            log.info("إنهاء العمل على الحركة");

            return task;
        }
    }
}
