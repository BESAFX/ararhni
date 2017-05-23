package com.besafx.app.component;
import com.besafx.app.config.EmailSender;
import com.besafx.app.controller.ReportTaskController;
import com.besafx.app.entity.*;
import com.besafx.app.search.TaskSearch;
import com.besafx.app.service.*;
import com.besafx.app.util.DateConverter;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class ScheduledTasks {

    private final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskSearch taskSearch;

    @Autowired
    private TaskToService taskToService;

    @Autowired
    private TaskCloseRequestService taskCloseRequestService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskWarnService taskWarnService;

    @Autowired
    private TaskDeductionService taskDeductionService;

    @Autowired
    private ReportTaskController reportTaskController;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmailSender emailSender;

    @Scheduled(cron = "0 0 2 * * SUN,MON,TUE,WED,THU")
    public void warnAllAboutUnCommentedTasksAtMidNight() {
        DateTime yesterday = new DateTime().minusDays(1).withTimeAtStartOfDay();
        DateTime today = new DateTime().withTimeAtStartOfDay();
        log.info("عدد المهام = " + taskService.count());
        log.info("عدد الافراد = " + personService.count());
        log.info("فحص كل فرد على حدا");
        check(yesterday, today);
    }

    private void check(DateTime startInterval, DateTime endInterval) {
        personService.findAll().forEach(person -> {
            log.info("////////////////////////////////" + person.getName() + "////////////////////////////////////////");
            log.info("فحص المهام الواردة السارية للموظف / " + person.getName());
            List<Task> tasks = taskSearch.search(null, null, Task.CloseType.Pending, null, null, null, null, null, null, true, true, "All", person.getId());
            log.info("عدد المهام المكلف بها = " + tasks.size());
            log.info("فحص كل مهمة على حدا");
            List<Task> warningTasks = new ArrayList<>();
            List<Task> deductionTasks = new ArrayList<>();
            DateTime nowCheckDate = new DateTime();
            tasks.stream()
                    .filter(task -> !taskToService.findByTaskAndPerson(task, person).getClosed())
                    .filter(task -> nowCheckDate.isAfter(new DateTime(task.getStartDate()).plusHours(24)))
                    .forEach(task -> {
                        log.info("البحث عن عدد حركات الموظف " + person.getName() + " على المهمة رقم " + task.getCode());
                        DateTime startTempInterval = new DateTime();
                        switch (task.getCommentType()) {
                            case Day:
                                startTempInterval = startInterval.minusDays(task.getCommentTypeCount());
                                break;
                            case Week:
                                startTempInterval = startInterval.minusWeeks(task.getCommentTypeCount());
                                break;
                            case Month:
                                startTempInterval = startInterval.minusMonths(task.getCommentTypeCount());
                                break;
                            default:
                                startTempInterval = startInterval.minusDays(task.getCommentTypeCount());
                                break;
                        }
                        log.info("من الفترة: " + DateConverter.getDateInFormatWithTime(startTempInterval.toDate()));
                        log.info("إلى الفترة: " + DateConverter.getDateInFormatWithTime(endInterval.toDate()));
                        long numberOfOperations = taskOperationService.countByTaskAndSenderAndDateBetween(task, person, startTempInterval.toDate(), endInterval.toDate());
                        log.info("عدد الحركات فى الفترة = " + numberOfOperations);
                        if (numberOfOperations == 0) {
                            long numberOfWarns = taskWarnService.countByTaskAndToPersonAndType(task, person, TaskWarn.TaskWarnType.Auto);
                            log.info("عدد التحذيرات على المهمة = " + numberOfWarns);
                            if (numberOfWarns < task.getWarn().longValue()) {
                                log.info("إرسال تحذير");
                                warningTasks.add(task);
                            } else {
                                log.info("إرسال خصم");
                                deductionTasks.add(task);
                            }
                        }

                    });
            if (!warningTasks.isEmpty()) {
                log.info("ارسال رسالة مجمعة بها كل التحذيرات");
                //Send Warn
                try {
                    StringBuilder builder = new StringBuilder();
                    builder.append("تحذير بالخصم بشأن عدم التعامل مع المهمة");
                    builder.append(" ");
                    builder.append("للموظف / " + person.getName());
                    builder.append(" ");
                    builder.append("من الفترة " + "(" + DateConverter.getHijriStringFromDateRTLWithTime(startInterval.toDate()) + ")");
                    builder.append(" ");
                    builder.append("إلى الفترة " + "(" + DateConverter.getHijriStringFromDateRTLWithTime(endInterval.toDate()) + ")");
                    builder.append(" ");
                    builder.append("نأمل الإلتزام بالتعليق فى خلال مدة لا تزيد عن 24 ساعة.");
                    log.info("جاري إرسال التحذير...");
                    createWarnEmail(warningTasks, builder.toString(), person);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!deductionTasks.isEmpty()) {
                log.info("إرسال رسالة مجمعة بها كل الحسومات والخصومات");
                //Send Discount
                try {
                    StringBuilder builder = new StringBuilder();
                    builder.append("خصم بشأن عدم التعامل مع المهمة");
                    builder.append(" ");
                    builder.append("للموظف / " + person.getName());
                    builder.append(" ");
                    builder.append("نظراً لانتهاء العدد المسموح به من التحذيرات،");
                    builder.append(" ");
                    builder.append("نأمل منه مراجعة جهة التكليف.");
                    createDeductionEmail(deductionTasks, builder.toString(), person);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            log.info("////////////////////////////////" + person.getName() + "////////////////////////////////////////");

        });
    }

    private void createWarnEmail(List<Task> tasks, String content, Person to) throws IOException {
        ListIterator<Task> listIterator = tasks.listIterator();
        while (listIterator.hasNext()) {
            Task task = listIterator.next();
            log.info("جاري إعداد التحذير وإدراجه...");
            TaskWarn taskWarn = new TaskWarn();
            TaskWarn tempTaskWarn = taskWarnService.findTopByTaskAndToPersonOrderByCodeDesc(task, to);
            if (tempTaskWarn == null) {
                taskWarn.setCode(1);
            } else {
                taskWarn.setCode(tempTaskWarn.getCode() + 1);
            }
            taskWarn.setDate(new Date());
            taskWarn.setTask(task);
            taskWarn.setType(TaskWarn.TaskWarnType.Auto);
            log.info("تعيين الموظف المجازى او المحذر كمرسل للحركة التى تعتبر تحذير او حسم");
            taskWarn.setToPerson(to);
            taskWarn.setContent(content);
            taskWarnService.save(taskWarn);
            log.info("تم حفظ التحذير الآلي باسم الموظف");
        }
//        ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NoTaskOperationsWarning.html");
//        String message = org.apache.commons.io.IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
//        message = message.replaceAll("MESSAGE", content.toString());
//        String title = "تحذير يومي بشأن عدم التعامل مع المهام";
//        emailSender.send(title, message, to.getEmail());
    }

    private void createDeductionEmail(List<Task> tasks, String content, Person to) throws IOException {
        ListIterator<Task> listIterator = tasks.listIterator();
        while (listIterator.hasNext()) {
            Task task = listIterator.next();
            log.info("جاري إعداد الخصم وإدراجه...");
            TaskDeduction taskDeduction = new TaskDeduction();
            TaskDeduction tempTaskDeduction = taskDeductionService.findTopByTaskAndToPersonOrderByCodeDesc(task, to);
            if (tempTaskDeduction == null) {
                taskDeduction.setCode(1);
            } else {
                taskDeduction.setCode(tempTaskDeduction.getCode() + 1);
            }
            taskDeduction.setDate(new Date());
            taskDeduction.setTask(task);
            taskDeduction.setType(TaskDeduction.TaskDeductionType.Auto);
            taskDeduction.setDeduction(task.getDeduction());
            log.info("تعيين الموظف المجازى او المحذر كمرسل للحركة التى تعتبر تحذير او حسم");
            taskDeduction.setToPerson(to);
            taskDeduction.setContent(content);
            taskDeductionService.save(taskDeduction);
            log.info("تم حفظ الخصم الآلي باسم الموظف");
        }
//        ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NoTaskOperationsWarning.html");
//        String message = org.apache.commons.io.IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
//        message = message.replaceAll("MESSAGE", content.toString());
//        String title = "خصم يومي بشأن عدم التعامل مع المهام";
//        emailSender.send(title, message, to.getEmail());
    }

    //@Scheduled(cron = "0 0 9 * * SUN,MON,TUE,WED,THU")
    public void sendReportAboutTaskTosCheck() throws InterruptedException, IOException, JRException, ExecutionException {
        Iterator<Person> iterator = personService.findAll().iterator();
        while (iterator.hasNext()) {
            Person person = iterator.next();
            List<Task> tasks = taskSearch.getOutgoingOpenedTasks("All", person.getId());
            if (!tasks.isEmpty()) {
                log.info("جاري العمل على مهام: " + person.getName());
                Future<byte[]> work = reportTaskController.ReportTaskTosCheck(tasks.stream().map(task -> task.getId()).collect(Collectors.toList()));
                byte[] fileBytes = work.get();
                String randomFileName = "TaskTosCheck-" + ThreadLocalRandom.current().nextInt(1, 50000);
                log.info("جاري إنشاء ملف التقرير: " + randomFileName);
                File reportFile = File.createTempFile(randomFileName, ".pdf");
                FileUtils.writeByteArrayToFile(reportFile, fileBytes);
                log.info("جاري تحويل الملف");
                Thread.sleep(10000);
                Future<Boolean> mail = emailSender.send("تقرير يومي لمتابعة الموظفين المكلفين - " + person.getNickname() + " / " + person.getName(), "", person.getEmail(), Lists.newArrayList(reportFile));
                mail.get();
                log.info("تم إرسال الملف فى البريد الإلكتروني بنجاح");
            }
        }
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void autoCloseTasks() throws IOException {
        DateTime yesterday = new DateTime().minusDays(1).withTimeAtStartOfDay();
        DateTime today = new DateTime().withTimeAtStartOfDay();
        log.info("جاري البحث عن المهام التى تم إغلاقها خلال 24 السابقة");
        log.info("سيتم إغلاق المهمة على الافراد المكلفين");
        log.info("سيتم إرسال خصومات إلى الافراد الذين لم يرسلوا طلب إغلاق على الأقل طوال حياة المهمة");
        List<Task> tasks = taskService.findByCloseTypeAndEndDateBetween(Task.CloseType.Pending, yesterday.toDate(), today.toDate());
        log.info("عدد المهام التى أغلقت خلال 24 ساعة الماضية: " + tasks.size());
        ListIterator<Task> listIterator = tasks.listIterator();
        while (listIterator.hasNext()) {
            Task task = listIterator.next();
            log.info("فحص المهمة رقم: " + task.getCode());
            log.info("فحص الموظفين المكلفين، ومعرفة إذا كان هناك أحد لم يرسل طلبات إغلاق");
            List<TaskTo> taskTos = taskToService.findByTask(task);
            log.info("عدد الموظفين المكلفين للمهمة رقم: " + task.getCode() + " يساوي : " + taskTos.size());
            ListIterator<TaskTo> taskToListIterator = taskTos.listIterator();
            while (taskToListIterator.hasNext()) {
                TaskTo taskTo = taskToListIterator.next();
                log.info("إيجاد طلبات إغلاق الموظف");
                List<TaskCloseRequest> closeRequests = taskCloseRequestService.findByTaskAndPerson(task, taskTo.getPerson());
                log.info("سيتم تجاهل هذا الموظف حال كانت المهمة مغلقة عليه");
                if (!taskTo.getClosed()) {
                    log.info("لن يتم تجاهل هذا الموظف حالة لم يرسل طلبات إغلاق إلى المهمة طوال حياة المهمة.");
                    if (closeRequests.isEmpty()) {
                        log.info("إرسال خصم إلى هذا الموظف / " + taskTo.getPerson().getName() + " بالمقدار المحدد من قبل جهة التكليف والذي يساوي: " + task.getDeductionOnAutoClose());
                        TaskDeduction taskDeduction = new TaskDeduction();
                        TaskDeduction tempTaskDeduction = taskDeductionService.findTopByTaskAndToPersonOrderByCodeDesc(task, taskTo.getPerson());
                        if (tempTaskDeduction == null) {
                            taskDeduction.setCode(1);
                        } else {
                            taskDeduction.setCode(tempTaskDeduction.getCode() + 1);
                        }
                        taskDeduction.setTask(task);
                        taskDeduction.setToPerson(taskTo.getPerson());
                        taskDeduction.setType(TaskDeduction.TaskDeductionType.Auto);
                        taskDeduction.setContent("تقرر توقيع خصم على الموظف / " + taskTo.getPerson().getName() + " نظراً لإغلاق المهمة عليه تلقائي بمقدار " + task.getDeductionOnAutoClose() + " ريال سعودي، فضلاً قم بمراجهة جهة التكليف.");
                        taskDeduction.setDate(new Date());
                        taskDeduction.setDeduction(task.getDeductionOnAutoClose());
                        taskDeductionService.save(taskDeduction);
//                        ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NoTaskOperationsWarning.html");
//                        String message = org.apache.commons.io.IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
//                        message = message.replaceAll("MESSAGE", "خصم إلكتروني بسبب إغلاق المهمة رقم / " + task.getCode() + " عليك تلقائي دون إرسال اى طلبات إغلاق طوال فترة حياة المهمة.");
//                        String title = "خصم إلكتروني يومي بسبب إغلاق المهمة تلقائي بمقدار / " + task.getDeductionOnAutoClose() + " ريال سعودي.";
//                        emailSender.send(title, message, taskDeduction.getToPerson().getEmail());
                        log.info("تم إرسال الخصم بنجاح إلى الموظف / " + taskTo.getPerson().getName());
                    }
                    log.info("إغلاق المهمة على هذا الموظف بتاريخ وقت الفحص");
                    taskTo.setClosed(true);
                    taskTo.setCloseDate(new Date());
                    taskTo.setDegree(TaskTo.PersonDegree.F);
                    taskToService.save(taskTo);
                }
                log.info("فحص الطلبات المعلقة...");
                closeRequests.stream().forEach(request -> {
                    if (request.getApproved() == null) {
                        if (request.getType()) {
                            log.info("الموافقة على طلب الاغلاق");
                            request.setApproved(true);
                        } else {
                            log.info("رفض طلب التمديد");
                            request.setApproved(false);
                        }
                        request.setApprovedDate(new Date());
                        taskCloseRequestService.save(request);
                    }
                });
                log.info("الانتهاء من فحص الطلبات المعلقة بنجاح.");
                log.info("إرسال حركة إلى المهمة تفيد بأن المهمة أغلقت تلقائي");
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
                taskOperation.setType(TaskOperation.OperationType.CloseTaskAuto);
                taskOperation.setContent("إغلاق المهمة تلقائي من خلال الفحص اليومي على الموظف / " + taskTo.getPerson().getName());
                taskOperationService.save(taskOperation);
                log.info("تحديث بيانات المهمة");
                task.setCloseType(Task.CloseType.Auto);
                taskService.save(task);
                log.info("تم الإنتهاء من فحص المهمة رقم: " + task.getCode() + " بنجاح.");

            }

        }

    }

    @Scheduled(cron = "0 0 10 * * *")
    public void notifyPeopleAboutClosedSoonTasks() {
        log.info("فحص كل المسخدمين");
        Iterator<Person> iterator = personService.findAll().iterator();
        while (iterator.hasNext()) {
            Person person = iterator.next();
            try {
                log.info("جاري العمل على مهام: " + person.getName());
                Future<byte[]> work = reportTaskController.ReportTasksClosedSoonNotify(person.getId());
                byte[] fileBytes = work.get();
                if (fileBytes == null) {
                    continue;
                }
                String randomFileName = "TasksClosedSoonNotify-" + ThreadLocalRandom.current().nextInt(1, 50000);
                log.info("جاري إنشاء ملف التقرير: " + randomFileName);
                File reportFile = File.createTempFile(randomFileName, ".pdf");
                FileUtils.writeByteArrayToFile(reportFile, fileBytes);
                log.info("جاري تحويل الملف");
                Thread.sleep(10000);
                Future<Boolean> mail = emailSender.send("تنبية بخصوص مهامك التى أوشكت على الإغلاق - " + person.getNickname() + " / " + person.getName(), "", person.getEmail(), Lists.newArrayList(reportFile));
                mail.get();
                log.info("تم إرسال الملف فى البريد الإلكتروني بنجاح");
            } catch (Exception ex) {
                log.info(ex.getMessage(), ex);
            }
        }
        log.info("نهاية الفحص بنجاح.");
    }

    @Scheduled(cron = "0 0 21 * * *")
    public void notifyManagersAboutTasksOperationsToday() {
        log.info("فحص كل المسخدمين");
        Iterator<Person> iterator = personService.findAll().iterator();
        while (iterator.hasNext()) {
            Person person = iterator.next();
            try {
                log.info("جاري العمل على مهام: " + person.getName());
                Future<byte[]> work = reportTaskController.ReportTasksOperationsToday(person.getId());
                byte[] fileBytes = work.get();
                if (fileBytes != null) {
                    String randomFileName = "TasksOperationsToday-" + ThreadLocalRandom.current().nextInt(1, 50000);
                    log.info("جاري إنشاء ملف التقرير: " + randomFileName);
                    File reportFile = File.createTempFile(randomFileName, ".pdf");
                    FileUtils.writeByteArrayToFile(reportFile, fileBytes);
                    log.info("جاري تحويل الملف");
                    Thread.sleep(10000);
                    Future<Boolean> mail = emailSender.send("تقرير حركات الموظفين اليوم - " + person.getNickname() + " / " + person.getName(), "", person.getEmail(), Lists.newArrayList(reportFile));
                    mail.get();
                    log.info("تم إرسال الملف فى البريد الإلكتروني بنجاح");
                } else {
                    log.info("لا يوجد تقرير لهذا المدير.");
                }
            } catch (Exception ex) {
                log.info(ex.getMessage(), ex);
            }
        }
        log.info("نهاية الفحص بنجاح.");
    }

    @Scheduled(cron = "0 0 22 * * *")
    public void notifyBossAboutWatchingTasksOperations() {
        log.info("ارسال التقرير لكل مدراء الشركات");
        companyService.findAll().forEach(company -> {
            regionService.findByCompany(company).forEach(region -> {
                branchService.findByRegion(region).forEach(branch -> {
                    try {
                        log.info("جاري فحص موظفين الفرع / " + branch.getName());
                        List<Person> personList = new ArrayList<>();
                        log.info("تصفية كل الموظفين ما دون المستوي");
                        personList.add(branch.getManager());
                        departmentService.findByBranch(branch).forEach(department -> {
                            personList.add(department.getManager());
                            personList.addAll(employeeService.findByDepartment(department).stream().map(employee -> employee.getPerson()).collect(Collectors.toList()));
                        });
                        Future<byte[]> work = reportTaskController.ReportWatchTasksOperations(personList.stream().map(person -> person.getId()).distinct().collect(Collectors.toList()));
                        byte[] fileBytes = work.get();
                        if (fileBytes != null) {
                            String randomFileName = "WatchTasksOperations-" + ThreadLocalRandom.current().nextInt(1, 50000);
                            log.info("جاري إنشاء ملف التقرير: " + randomFileName);
                            File reportFile = File.createTempFile(randomFileName, ".pdf");
                            FileUtils.writeByteArrayToFile(reportFile, fileBytes);
                            log.info("جاري تحويل الملف");
                            Thread.sleep(10000);
                            Future<Boolean> mail = emailSender.send("تقرير متابعة حركة العمل داخل فرع / " + branch.getName(), "التقرير بالمرفقات", company.getManager().getEmail(), Lists.newArrayList(reportFile));
                            mail.get();
                            log.info("تم إرسال الملف فى البريد الإلكتروني بنجاح");
                        } else {
                            log.info("لا يوجد تقرير لهذا المدير.");
                        }
                    } catch (Exception ex) {
                        log.info(ex.getMessage(), ex);
                    }
                });
            });
        });
    }

    @Scheduled(cron = "0 0 20 * * SUN,MON,TUE,WED,THU")
    public void notifyManagersAboutOutgoingTasksDeductions() {
        log.info("فحص كل المسخدمين");
        Iterator<Person> iterator = personService.findAll().iterator();
        while (iterator.hasNext()) {
            Person person = iterator.next();
            try {
                log.info("جاري العمل على مهام: " + person.getName());
                Future<byte[]> work = reportTaskController.ReportOutgoingTasksDeductions(person.getId());
                byte[] fileBytes = work.get();
                if (fileBytes == null) {
                    continue;
                }
                String randomFileName = "TasksDeductionsSummery-" + ThreadLocalRandom.current().nextInt(1, 50000);
                log.info("جاري إنشاء ملف التقرير: " + randomFileName);
                File reportFile = File.createTempFile(randomFileName, ".pdf");
                FileUtils.writeByteArrayToFile(reportFile, fileBytes);
                log.info("جاري تحويل الملف");
                Thread.sleep(10000);
                Future<Boolean> mail = emailSender.send("تقرير مختصر لحسومات المكلفين - " + person.getNickname() + " / " + person.getName(), "", person.getEmail(), Lists.newArrayList(reportFile));
                mail.get();
                log.info("تم إرسال الملف فى البريد الإلكتروني بنجاح");
            } catch (Exception ex) {
                log.info(ex.getMessage(), ex);
            }
        }
        log.info("نهاية الفحص بنجاح.");
    }
}
