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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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

    private void check(DateTime startLast12Hour, DateTime endLast12Hour) {
        personService.findAll().forEach(person -> {

            log.info("////////////////////////////////" + person.getName() + "////////////////////////////////////////");

            log.info("فحص المهام الواردة السارية للموظف / " + person.getName());

            List<Task> tasks = taskSearch.search(null, null, null, null, null, null, null, null, true, true, "All", person.getId());

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
                log.info("من الفترة: " + DateConverter.getDateInFormatWithTime(startLast12Hour.toDate()));
                log.info("إلى الفترة: " + DateConverter.getDateInFormatWithTime(endLast12Hour.toDate()));

                long numberOfOperations = taskOperationService.countByTaskAndSenderAndDateBetween(task, person, startLast12Hour.toDate(), endLast12Hour.toDate());

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
                    builder.append("تحذير بالخصم بشأن عدم التعامل مع المهام رقم " + "(" + warningTasks.stream().map(task -> task.getCode()).collect(Collectors.toList()) + ")");
                    builder.append(" ");
                    builder.append("للموظف / " + person.getName());
                    builder.append(" ");
                    builder.append("من الفترة " + "(" + DateConverter.getHijriStringFromDateRTLWithTime(startLast12Hour.toDate()) + ")");
                    builder.append(" ");
                    builder.append("إلى الفترة " + "(" + DateConverter.getHijriStringFromDateRTLWithTime(endLast12Hour.toDate()) + ")");
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
                    builder.append("خصم بشأن عدم التعامل مع المهام رقم " + "(" + deductionTasks.stream().map(task -> task.getCode()).collect(Collectors.toList()) + ")");
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

        ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NoTaskOperationsWarning.html");
        String message = org.apache.commons.io.IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
        message = message.replaceAll("MESSAGE", content.toString());
        String title = "تحذير يومي بشأن عدم التعامل مع المهام";
        emailSender.send(title, message, to.getEmail());
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

        ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NoTaskOperationsWarning.html");
        String message = org.apache.commons.io.IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
        message = message.replaceAll("MESSAGE", content.toString());
        String title = "خصم يومي بشأن عدم التعامل مع المهام";
        emailSender.send(title, message, to.getEmail());
    }

    @Scheduled(cron = "0 0 9 * * SUN,MON,TUE,WED,THU")
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
                Future<Boolean> mail = emailSender.send("تقرير يومي لمتابعة الموظفين المكلفين - " + person.getNickname() + " / " + person.getName(), "", person.getEmail(), Lists.newArrayList(new FileSystemResource(reportFile)));
                mail.get();
                log.info("تم إرسال الملف فى البريد الإلكتروني بنجاح");
            }
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void autoCloseTasks() throws IOException {
        DateTime yesterday = new DateTime().minusDays(1).withTimeAtStartOfDay();
        DateTime today = new DateTime().withTimeAtStartOfDay();

        log.info("جاري البحث عن المهام التى تم إغلاقها خلال 24 السابقة");

        log.info("سيتم إغلاق المهمة على الافراد المكلفين");

        log.info("سيتم إرسال خصومات إلى الافراد الذين لم يرسلوا طلب إغلاق على الأقل طوال حياة المهمة");

        List<Task> tasks = taskService.findByEndDateBetween(yesterday.toDate(), today.toDate());

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

                log.info("سيتم تجاهل هذا الموظف حال كانت المهمة مغلقة عليه");

                if (!taskTo.getClosed()) {

                    log.info("لن يتم تجاهل هذا الموظف حالة لم يرسل طلبات إغلاق إلى المهمة طوال حياة المهمة.");

                    if (taskCloseRequestService.findByTaskAndPerson(task, taskTo.getPerson()).isEmpty()) {

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
                        taskDeductionService.save(taskDeduction);

                        ClassPathResource classPathResource = new ClassPathResource("/mailTemplate/NoTaskOperationsWarning.html");
                        String message = org.apache.commons.io.IOUtils.toString(classPathResource.getInputStream(), Charset.defaultCharset());
                        message = message.replaceAll("MESSAGE", "خصم إلكتروني بسبب إغلاق المهمة رقم / " + task.getCode() + " عليك تلقائي دون إرسال اى طلبات إغلاق طوال فترة حياة المهمة.");
                        String title = "خصم إلكتروني يومي بسبب إغلاق المهمة تلقائي بمقدار / " + task.getDeductionOnAutoClose() + " ريال سعودي.";
                        emailSender.send(title, message, taskDeduction.getToPerson().getEmail());

                        log.info("تم إرسال الخصم بنجاح إلى الموظف / " + taskTo.getPerson().getName());
                    }

                    log.info("إغلاق المهمة على هذا الموظف بتاريخ وقت الفحص");
                    taskTo.setClosed(true);
                    taskTo.setCloseDate(new Date());
                    taskTo.setDegree(TaskTo.PersonDegree.F);
                    taskToService.save(taskTo);
                }

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
                taskOperation.setContent("إغلاق المهمة تلقائي من خلال الفحص اليومي.");
                taskOperationService.save(taskOperation);

                log.info("تم الإنتهاء من فحص المهمة رقم: " + task.getCode() + " بنجاح.");

            }

        }

    }
}
