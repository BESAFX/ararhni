package com.besafx.app.rest;
import com.besafx.app.config.CustomException;
import com.besafx.app.config.EmailSender;
import com.besafx.app.entity.*;
import com.besafx.app.service.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/task/")
public class TaskAction {

    private final static Logger log = LoggerFactory.getLogger(TaskAction.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private PersonService personService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskCloseRequestService taskCloseRequestService;

    @Autowired
    private TaskToService taskToService;

    @Autowired
    private TaskWarnService taskWarnService;

    @Autowired
    private TaskDeductionService taskDeductionService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PersonRest personRest;

    @Autowired
    private EmailSender emailSender;

    @RequestMapping(value = "increaseEndDate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    @Transactional
    public Task increaseEndDate(@RequestParam(value = "taskId") Long taskId, @RequestParam(value = "days") int days, @RequestParam(value = "message") String message, Principal principal) {
        Person caller = personService.findByEmail(principal.getName());
        Task task = taskService.findOne(taskId);
        if (task == null) {
            throw new CustomException("عفواً ، لا توجد هذة المهمة");
        } else {
            if (!personRest.getPersonManager(task.getPerson()).getEmail().equals(principal.getName())) {
                if (!task.getPerson().getEmail().equals(principal.getName())) {
                    throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف مصرح له بذلك.");
                }
            }
            try {
                log.info("تعديل بيانات المهمة وتغيير تاريخ الاستلام واضافة الايام اليها.");
                task.setEndDate(new DateTime().plusDays(days).toDate());
                task.setCloseType(Task.CloseType.Pending);
                task = taskService.save(task);
                log.info("حفظ المهمة بنجاح.");
                log.info("فحص كل طلبات التمديد المعلقة وقبولها...");
                taskCloseRequestService.findByTaskIdAndTypeAndApprovedIsNull(taskId, false).stream().forEach(taskCloseRequest -> {
                    taskCloseRequest.setApproved(true);
                    taskCloseRequest.setApprovedDate(new Date());
                    taskCloseRequestService.save(taskCloseRequest);
                });
                log.info("فحص كل طلبات الإغلاق المعلقة ورفضها...");
                taskCloseRequestService.findByTaskIdAndTypeAndApprovedIsNull(taskId, true).stream().forEach(taskCloseRequest -> {
                    taskCloseRequest.setApproved(false);
                    taskCloseRequest.setApprovedDate(new Date());
                    taskCloseRequestService.save(taskCloseRequest);
                });
                log.info("فحص الافراد المكلفين، ومعرفة اذا كان عددهم اقل من شخصين يتم وكانت مغلقة عليهم مسبقاً يتم فتحها علي الشخص المكلف تلقائي.");
                List<TaskTo> taskTos = taskToService.findByTask(task);
                if (taskTos.size() == 1) {
                    if (taskTos.get(0).getClosed()) {
                        taskTos.get(0).setClosed(false);
                        taskTos.get(0).setCloseDate(null);
                        taskTos.get(0).setProgress(0);
                        taskTos.get(0).setDegree(null);
                        taskToService.save(taskTos.get(0));
                        log.info("ارسال حركة جديدة بشأن فتح المهمة تلقائي على الشخص الوحيد المكلف بهذة المهمة.");
                        TaskOperation taskOperation = new TaskOperation();
                        TaskOperation tempTaskOperation = taskOperationService.findTopByTaskIdOrderByCodeDesc(taskId);
                        if (tempTaskOperation == null) {
                            taskOperation.setCode(1);
                        } else {
                            taskOperation.setCode(tempTaskOperation.getCode() + 1);
                        }
                        taskOperation.setDate(new Date());
                        taskOperation.setSender(caller);
                        taskOperation.setTask(task);
                        taskOperation.setType(TaskOperation.OperationType.OpenTaskOnPersonAuto);
                        taskOperation.setContent("فتح المهمة تلقائي على الموظف / " + taskTos.get(0).getPerson().getName() + " بعد تمديد تاريخ استلام المهمة بمقدار " + days + " أيام.");
                        taskOperationService.save(taskOperation);
                        log.info("حفظ الحركة الجديدة بنجاح.");
                    }
                }
                log.info("ارسال استعلام للعميل بالانتهاء من العملية بنجاح.");
                notificationService.notifyOne(Notification
                        .builder()
                        .title("العمليات على المهام")
                        .message("تم تمديد تاريخ إستلام المهمة رقم: " + task.getCode() + " بنجاح")
                        .type("success")
                        .icon("fa-battery")
                        .build(), principal.getName());
                log.info("ارسال رسالة الى المكلفين بالمهمة بشأن تمديد استلام المهمة والتاريخ الجديد.");
//                ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/ExtendTask.html");
//                String email = IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
//                email = email.replaceAll("TASK_CODE", task.getCode().toString());
//                email = email.replaceAll("TASK_TITLE", task.getTitle());
//                email = email.replaceAll("TASK_CONTENT", task.getContent());
//                email = email.replaceAll("TASK_END_DATE", DateConverter.getHijriStringFromDateRTL(task.getEndDate()));
//                email = email.replaceAll("TASK_PERSON", task.getPerson().getName());
//                emailSender.send("تمديد تاريخ إستلام المهمة رقم: " + "(" + task.getCode() + ")", email, task.getTaskTos().stream().map(to -> to.getPerson().getEmail()).collect(Collectors.toList()));
                log.info("تسجيل حركة جديدة عن تمديد تاريخ استلام المهمة وفتحها.");
                TaskOperation taskOperation = new TaskOperation();
                TaskOperation tempTaskOperation = taskOperationService.findTopByTaskIdOrderByCodeDesc(taskId);
                if (tempTaskOperation == null) {
                    taskOperation.setCode(1);
                } else {
                    taskOperation.setCode(tempTaskOperation.getCode() + 1);
                }
                taskOperation.setDate(new Date());
                taskOperation.setSender(caller);
                taskOperation.setTask(task);
                taskOperation.setType(TaskOperation.OperationType.IncreaseEndDate);
                taskOperation.setContent(message);
                taskOperationService.save(taskOperation);
                log.info("حفظ الحركة الجديدة بنجاح.");
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
    @Transactional
    public Task decreaseEndDate(@RequestParam(value = "taskId") Long taskId, @RequestParam(value = "days") int days, @RequestParam(value = "message") String message, Principal principal) throws IOException {
        Task task = taskService.findOne(taskId);
        if (task == null) {
            throw new CustomException("عفواً ، لا توجد هذة المهمة");
        } else {
            if (task.getCloseType().equals(Task.CloseType.Manual)) {
                throw new CustomException("لا يمكن القيام بأي عمليات على مهام الارشيف.");
            }
            if (!personRest.getPersonManager(task.getPerson()).getEmail().equals(principal.getName())) {
                if (!task.getPerson().getEmail().equals(principal.getName())) {
                    throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف مصرح له بذلك.");
                }
            }
            try {
                task.setEndDate(new DateTime(task.getEndDate()).minusDays(days).toDate());
                task = taskService.save(task);
                notificationService.notifyOne(Notification
                        .builder()
                        .title("العمليات على المهام")
                        .message("تم تعجيل تاريخ إستلام المهمة رقم: " + task.getCode() + " بنجاح")
                        .type("success")
                        .icon("fa-battery")
                        .build(), principal.getName());
//                ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/ExtendTask.html");
//                String email = IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
//                email = email.replaceAll("TASK_CODE", task.getCode().toString());
//                email = email.replaceAll("TASK_TITLE", task.getTitle());
//                email = email.replaceAll("TASK_CONTENT", task.getContent());
//                email = email.replaceAll("TASK_END_DATE", DateConverter.getHijriStringFromDateRTL(task.getEndDate()));
//                email = email.replaceAll("TASK_PERSON", task.getPerson().getName());
//                emailSender.send("تعجيل تاريخ إستلام المهمة رقم: " + "(" + task.getCode() + ")", email, task.getTaskTos().stream().map(to -> to.getPerson().getEmail()).collect(Collectors.toList()));
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
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                return null;
            }
        }
    }

    @RequestMapping(value = "declineRequest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    @Transactional
    public TaskCloseRequest declineRequest(@RequestParam(value = "requestId") Long requestId, Principal principal) throws IOException {
        TaskCloseRequest taskCloseRequest = taskCloseRequestService.findOne(requestId);
        if (taskCloseRequest == null) {
            throw new CustomException("عفواً ، لا يوجد هذا الطلب");
        } else {
            if (taskCloseRequest.getTask().getCloseType().equals(Task.CloseType.Manual)) {
                throw new CustomException("لا يمكن القيام بأي عمليات على مهام الارشيف.");
            }
            if (!personRest.getPersonManager(taskCloseRequest.getTask().getPerson()).getEmail().equals(principal.getName())) {
                if (!taskCloseRequest.getTask().getPerson().getEmail().equals(principal.getName())) {
                    throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف مصرح له بذلك.");
                }
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
    @Transactional
    public TaskCloseRequest acceptRequest(@RequestParam(value = "requestId") Long requestId, Principal principal) {
        TaskCloseRequest taskCloseRequest = taskCloseRequestService.findOne(requestId);
        if (taskCloseRequest == null) {
            throw new CustomException("عفواً ، لا يوجد هذا الطلب");
        } else {
//            if (taskCloseRequest.getTask().getCloseType().equals(Task.CloseType.Manual)) {
//                throw new CustomException("لا يمكن القيام بأي عمليات على مهام الارشيف.");
//            }
            if (!personRest.getPersonManager(taskCloseRequest.getTask().getPerson()).getEmail().equals(principal.getName())) {
                if (!taskCloseRequest.getTask().getPerson().getEmail().equals(principal.getName())) {
                    throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف مصرح له بذلك.");
                }
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
    @Transactional
    public Task closeTaskOnPerson(@RequestParam(value = "taskId") Long taskId, @RequestParam(value = "personId") Long personId, @RequestParam(value = "message") String message, @RequestParam(value = "degree") TaskTo.PersonDegree degree, Principal principal) throws IOException {
        Task task = taskService.findOne(taskId);
        if (task == null) {
            throw new CustomException("عفواً ، لا توجد هذة المهمة");
        } else {
            if (task.getCloseType().equals(Task.CloseType.Manual)) {
                throw new CustomException("لا يمكن القيام بأي عمليات على مهام الارشيف.");
            }
            if (!personRest.getPersonManager(task.getPerson()).getEmail().equals(principal.getName())) {
                if (!task.getPerson().getEmail().equals(principal.getName())) {
                    throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف مصرح له بذلك.");
                }
            }
            try {
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
                {
                    log.info("انشاء حركة لإغلاق المهمة على الموظف");
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
                }
                log.info("فى حال كان الموظفون المكلفين تم إغلاق مهامهم");
                if (task.getTaskTos().stream().filter(to -> !to.getClosed()).collect(Collectors.toList()).isEmpty()) {
                    task.setEndDate(new Date());
                    task.setCloseType(Task.CloseType.Manual);
                    taskService.save(task);
                    log.info("اضافة حركة جديدة لإغلاق المهمة ونقلها إلى الارشيف");
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
                    taskOperation.setType(TaskOperation.OperationType.CloseTaskCompletely);
                    taskOperation.setContent("تم نقل المهمة إلى الارشيف نظراً لإغلاق المهمة على كل الموظفين.");
                    taskOperationService.save(taskOperation);
                    log.info("إنهاء العمل على الحركة");
                }
                log.info("البحث عن طلبات الاغلاق المعلقة والموافقة عليها...");
                taskCloseRequestService.findByTaskIdAndPersonIdAndTypeAndApprovedIsNull(taskId, personId, true).stream().forEach(taskCloseRequest -> {
                    taskCloseRequest.setApproved(true);
                    taskCloseRequest.setApprovedDate(new Date());
                    taskCloseRequestService.save(taskCloseRequest);
                });
                log.info("البحث عن طلبات التمديد المعلقة ورفضها...");
                taskCloseRequestService.findByTaskIdAndPersonIdAndTypeAndApprovedIsNull(taskId, personId, false).stream().forEach(taskCloseRequest -> {
                    taskCloseRequest.setApproved(false);
                    taskCloseRequest.setApprovedDate(new Date());
                    taskCloseRequestService.save(taskCloseRequest);
                });
                return task;
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                return null;
            }
        }
    }

    @RequestMapping(value = "closeTaskCompletely", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    @Transactional
    public Task closeTaskCompletely(@RequestParam(value = "taskId") Long taskId, Principal principal) {
        Task task = taskService.findOne(taskId);
        if (task == null) {
            throw new CustomException("عفواً ، لا توجد هذة المهمة");
        } else {
            if (task.getCloseType().equals(Task.CloseType.Manual)) {
                throw new CustomException("لا يمكن القيام بأي عمليات على مهام الارشيف.");
            }
            if (!personRest.getPersonManager(task.getPerson()).getEmail().equals(principal.getName())) {
                if (!task.getPerson().getEmail().equals(principal.getName())) {
                    throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف مصرح له بذلك.");
                }
            }
            try {
                log.info("اضافة حركة جديدة لإغلاق المهمة نهائي");
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
                taskOperation.setType(TaskOperation.OperationType.CloseTaskCompletely);
                taskOperation.setContent("إغلاق المهمة نهائياً - نقل إلى الارشيف");
                taskOperationService.save(taskOperation);
                log.info("إنهاء العمل على الحركة");
                log.info("تحديث تاريخ استلام المهمة الى تاريخ اليوم وهو وقت الاغلاق");
                task.setEndDate(new Date());
                task.setCloseType(Task.CloseType.Manual);
                taskService.save(task);
                log.info("الموافقة على كل طلبات الاغلاق الخاصة بهذة المهمة");
                taskCloseRequestService.findByTaskIdAndTypeAndApprovedIsNull(taskId, true).forEach(request -> {
                    request.setApproved(true);
                    request.setApprovedDate(new Date());
                    taskCloseRequestService.save(request);
                });
                log.info("رفض كل طلبات التمديد الخاصة بهذة المهمة");
                taskCloseRequestService.findByTaskIdAndTypeAndApprovedIsNull(taskId, false).forEach(request -> {
                    request.setApproved(false);
                    request.setApprovedDate(new Date());
                    taskCloseRequestService.save(request);
                });
                log.info("إغلاق المهمة على كل الموظفين");
                taskToService.findByTask(task).stream().filter(taskTo -> !taskTo.getClosed()).forEach(taskTo -> {
                    taskTo.setClosed(true);
                    taskTo.setCloseDate(new Date());
                    taskTo.setDegree(TaskTo.PersonDegree.C);
                    taskToService.save(taskTo);
                });
                log.info("اعلام المستخدم بنجاح العملية.");
                notificationService.notifyOne(Notification
                        .builder()
                        .title("العمليات على المهام")
                        .message("تم نقل المهمة الى الارشيف بنجاح.")
                        .type("success")
                        .icon("fa-archive")
                        .build(), principal.getName());
                return task;
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                return null;
            }
        }
    }

    @RequestMapping(value = "addPerson", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    @Transactional
    public Task addPerson(@RequestParam(value = "taskId") Long taskId, @RequestParam(value = "personId") Long personId, @RequestParam(value = "message") String message, Principal principal) throws IOException {
        Task task = taskService.findOne(taskId);
        if (task.getCloseType().equals(Task.CloseType.Manual)) {
            throw new CustomException("لا يمكن القيام بأي عمليات على مهام الارشيف.");
        }
        Person person = personService.findOne(personId);
        if (!personRest.getPersonManager(task.getPerson()).getEmail().equals(principal.getName())) {
            if (!task.getPerson().getEmail().equals(principal.getName())) {
                throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف مصرح له بذلك.");
            }
        }
        if (taskToService.findByTaskAndPerson(task, person) != null) {
            throw new CustomException("هذا الموظف مكلف بالفعل بهذة المهمة.");
        }
        try {
            TaskTo taskTo = new TaskTo();
            taskTo.setDegree(null);
            taskTo.setProgress(0);
            taskTo.setClosed(false);
            taskTo.setCloseDate(null);
            taskTo.setTask(task);
            taskTo.setPerson(person);
            taskToService.save(taskTo);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم اضافة التكليف الجديد بنجاح")
                    .type("success")
                    .icon("fa-black-tie")
                    .build(), principal.getName());
            ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NewTask.html");
            String email = IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
            email = email.replaceAll("TASK_CODE", task.getCode().toString());
            email = email.replaceAll("TASK_TITLE", task.getTitle());
            email = email.replaceAll("TASK_CONTENT", task.getContent());
            email = email.replaceAll("TASK_END_DATE", DateConverter.getHijriStringFromDateRTL(task.getEndDate()));
            email = email.replaceAll("TASK_PERSON", task.getPerson().getName());
            emailSender.send("مهمة جديدة رقم: " + "(" + task.getCode() + ")", email, person.getEmail());
            log.info("اضافة الحركة الخاصة بالتحويل الى موظف جديد");
            TaskOperation taskOperation = new TaskOperation();
            TaskOperation tempTaskOperation = taskOperationService.findTopByTaskIdOrderByCodeDesc(task.getId());
            if (tempTaskOperation == null) {
                taskOperation.setCode(1);
            } else {
                taskOperation.setCode(tempTaskOperation.getCode() + 1);
            }
            taskOperation.setDate(new Date());
            taskOperation.setSender(task.getPerson());
            taskOperation.setTask(task);
            taskOperation.setType(TaskOperation.OperationType.AddPerson);
            taskOperation.setContent("تحويل المهمة إلى " + person.getNickname() + " / " + person.getName() + " [ " + message + " ] ");
            taskOperationService.save(taskOperation);
            log.info("تم اضافة الحركة الجديدة بنجاح.");
            return task;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @RequestMapping(value = "removePerson", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    @Transactional
    public Boolean removePerson(@RequestParam(value = "taskId") Long taskId, @RequestParam(value = "personId") Long personId, @RequestParam(value = "message") String message, Principal principal) throws IOException {
        Task task = taskService.findOne(taskId);
        if (task.getCloseType().equals(Task.CloseType.Manual)) {
            throw new CustomException("لا يمكن القيام بأي عمليات على مهام الارشيف.");
        }
        Person person = personService.findOne(personId);
        if (!personRest.getPersonManager(task.getPerson()).getEmail().equals(principal.getName())) {
            if (!task.getPerson().getEmail().equals(principal.getName())) {
                throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف مصرح له بذلك.");
            }
        }
        TaskTo taskTo = taskToService.findByTaskAndPerson(task, person);
        if (taskTo == null) {
            throw new CustomException("هذا الموظف غير مكلف بهذة المهمة.");
        }
        try {
            taskToService.delete(taskTo);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم حذف التكليف بنجاح")
                    .type("success")
                    .icon("fa-trash")
                    .build(), principal.getName());
            log.info("اضافة الحركة الخاصة بحذف موظف من التكليف");
            TaskOperation taskOperation = new TaskOperation();
            TaskOperation tempTaskOperation = taskOperationService.findTopByTaskIdOrderByCodeDesc(task.getId());
            if (tempTaskOperation == null) {
                taskOperation.setCode(1);
            } else {
                taskOperation.setCode(tempTaskOperation.getCode() + 1);
            }
            taskOperation.setDate(new Date());
            taskOperation.setSender(task.getPerson());
            taskOperation.setTask(task);
            taskOperation.setType(TaskOperation.OperationType.AddPerson);
            taskOperation.setContent("حذف تكليف المهمة من " + person.getNickname() + " / " + person.getName() + " [ " + message + " ] ");
            taskOperationService.save(taskOperation);
            log.info("تم اضافة الحركة الجديدة بنجاح.");
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    @RequestMapping(value = "addWarn", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    @Transactional
    public TaskWarn addWarn(@RequestParam(value = "taskId") Long taskId, @RequestParam(value = "personId") Long personId, @RequestParam(value = "message") String message, Principal principal) throws IOException {
        Task task = taskService.findOne(taskId);
        if (task.getCloseType().equals(Task.CloseType.Manual)) {
            throw new CustomException("لا يمكن القيام بأي عمليات على مهام الارشيف.");
        }
        Person person = personService.findOne(personId);
        if (!personRest.getPersonManager(task.getPerson()).getEmail().equals(principal.getName())) {
            if (!task.getPerson().getEmail().equals(principal.getName())) {
                throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف مصرح له بذلك.");
            }
        }
        try {
            TaskWarn taskWarn = new TaskWarn();
            TaskWarn tempTaskWarn = taskWarnService.findTopByTaskAndToPersonOrderByCodeDesc(task, person);
            if (tempTaskWarn == null) {
                taskWarn.setCode(1);
            } else {
                taskWarn.setCode(tempTaskWarn.getCode() + 1);
            }
            taskWarn.setDate(new Date());
            taskWarn.setType(TaskWarn.TaskWarnType.Manual);
            taskWarn.setToPerson(person);
            taskWarn.setTask(task);
            taskWarn.setContent(message);
            taskWarn = taskWarnService.save(taskWarn);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم إرسال التحذير بنجاح")
                    .type("success")
                    .icon("fa-black-tie")
                    .build(), person.getName());
            ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NoTaskOperationsWarning.html");
            String mail = org.apache.commons.io.IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
            mail = mail.replaceAll("MESSAGE", "تحذير من " + task.getPerson().getNickname() + " / " + task.getPerson().getName() + " بشأن المهمة رقم " + "(" + task.getCode() + ")" + " وفيما يلي محتوى التحذير" + "<br/>" + "<u>" + message + "</u>");
            String title = "تحذير من " + task.getPerson().getNickname() + " / " + task.getPerson().getName();
            emailSender.send(title, mail, person.getEmail());
            return taskWarn;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @RequestMapping(value = "addDeduction", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    @Transactional
    public TaskDeduction addDeduction(@RequestParam(value = "taskId") Long taskId, @RequestParam(value = "personId") Long personId, @RequestParam(value = "message") String message, @RequestParam(value = "deduction") Double deduction, Principal principal) throws IOException {
        Task task = taskService.findOne(taskId);
        if (task.getCloseType().equals(Task.CloseType.Manual)) {
            throw new CustomException("لا يمكن القيام بأي عمليات على مهام الارشيف.");
        }
        Person person = personService.findOne(personId);
        if (!personRest.getPersonManager(task.getPerson()).getEmail().equals(principal.getName())) {
            if (!task.getPerson().getEmail().equals(principal.getName())) {
                throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف مصرح له بذلك.");
            }
        }
        try {
            TaskDeduction taskDeduction = new TaskDeduction();
            TaskDeduction tempTaskDeduction = taskDeductionService.findTopByTaskAndToPersonOrderByCodeDesc(task, person);
            if (tempTaskDeduction == null) {
                taskDeduction.setCode(1);
            } else {
                taskDeduction.setCode(tempTaskDeduction.getCode() + 1);
            }
            taskDeduction.setDate(new Date());
            taskDeduction.setType(TaskDeduction.TaskDeductionType.Manual);
            taskDeduction.setToPerson(person);
            taskDeduction.setTask(task);
            taskDeduction.setContent(message);
            taskDeduction.setDeduction(deduction);
            taskDeduction = taskDeductionService.save(taskDeduction);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم إرسال الخصم بنجاح")
                    .type("success")
                    .icon("fa-black-tie")
                    .build(), person.getName());
            ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NoTaskOperationsWarning.html");
            String mail = org.apache.commons.io.IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
            mail = mail.replaceAll("MESSAGE", "خصم من " + task.getPerson().getNickname() + " / " + task.getPerson().getName() + " بشأن المهمة رقم " + "(" + task.getCode() + ")" + " وفيما يلي محتوى الخصم" + "<br/>" + "<u>" + message + "</u>");
            String title = "خصم من " + task.getPerson().getNickname() + " / " + task.getPerson().getName() + " بقيمة " + taskDeduction.getDeduction() + " ريال سعودي";
            emailSender.send(title, mail, taskDeduction.getToPerson().getEmail());
            return taskDeduction;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @RequestMapping(value = "openTaskOnPerson", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TASK_UPDATE')")
    @Transactional
    public TaskTo openTaskOnPerson(@RequestParam(value = "taskId") Long taskId, @RequestParam(value = "personId") Long personId, @RequestParam(value = "message") String message, Principal principal) throws IOException {
        Person caller = personService.findByEmail(principal.getName());
        Task task = taskService.findOne(taskId);
        if (task.getCloseType().equals(Task.CloseType.Manual)) {
            throw new CustomException("لا يمكن القيام بأي عمليات على مهام الارشيف.");
        }
        Person person = personService.findOne(personId);
        if (!personRest.getPersonManager(task.getPerson()).getEmail().equals(principal.getName())) {
            if (!task.getPerson().getEmail().equals(principal.getName())) {
                throw new CustomException("غير مصرح لك القيام بهذة العملية، فقط جهة التكليف مصرح له بذلك.");
            }
        }
        log.info("فحص إذا كانت المهمة تحت التنفيذ...");
        if (!task.getCloseType().equals(Task.CloseType.Pending)) {
            throw new CustomException("لا يمكن فتح مهمة مغلقة على الموظف، حاول تمديد المهمة أولاً.");
        }
        TaskTo taskTo = taskToService.findByTaskAndPerson(task, person);
        if (taskTo == null) {
            throw new CustomException("عفواً، هذا الموظف غير موجود من ضمن فريق عمل المهمة.");
        }
        try {
            log.info("إغادة فتح المهمة على الموظف بعد التأكد من وجوده من ضمن فريق عمل المهمة.");
            taskTo.setClosed(false);
            taskTo.setCloseDate(null);
            taskTo.setDegree(null);
            taskTo.setProgress(0);
            taskTo = taskToService.save(taskTo);
            log.info("إعلام المستدعي بنجاح العملية.");
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المهام")
                    .message("تم فتح المهمة على الموظف بنجاح.")
                    .type("success")
                    .icon("fa-black-tie")
                    .build(), person.getName());
            log.info("إضافة حركة جديدة بشأن فتح المهمة على الموظف.");
            TaskOperation taskOperation = new TaskOperation();
            TaskOperation tempTaskOperation = taskOperationService.findTopByTaskIdOrderByCodeDesc(task.getId());
            if (tempTaskOperation == null) {
                taskOperation.setCode(1);
            } else {
                taskOperation.setCode(tempTaskOperation.getCode() + 1);
            }
            taskOperation.setDate(new Date());
            taskOperation.setSender(caller);
            taskOperation.setTask(task);
            taskOperation.setType(TaskOperation.OperationType.OpenTaskOnPerson);
            taskOperation.setContent("فتح المهمة على الموظف / " + person.getName() + " [ " + message + " ] ");
            taskOperationService.save(taskOperation);
            log.info("تم حفظ الحركة الجديدة بنجاح.");
            return taskTo;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

}
