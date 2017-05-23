package com.besafx.app.controller;
import com.besafx.app.config.CustomException;
import com.besafx.app.config.EmailSender;
import com.besafx.app.entity.*;
import com.besafx.app.rest.TaskOperationRest;
import com.besafx.app.search.TaskSearch;
import com.besafx.app.service.*;
import com.besafx.app.util.DateConverter;
import com.besafx.app.util.WrapperUtil;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RestController
public class ReportTaskController {

    private final Logger log = LoggerFactory.getLogger(ReportTaskController.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskToService taskToService;

    @Autowired
    private TaskSearch taskSearch;

    @Autowired
    private TaskOperationRest taskOperationRest;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskWarnService taskWarnService;

    @Autowired
    private TaskDeductionService taskDeductionService;

    @Autowired
    private TaskCloseRequestService taskCloseRequestService;

    @Autowired
    private EmailSender emailSender;

    @RequestMapping(value = "/report/TaskOperations", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportTaskOperationsByTask(
            @RequestParam("tasksList") List<Long> tasksList,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate, HttpServletResponse response)
            throws JRException, IOException {
        if (tasksList.isEmpty()) {
            throw new CustomException("عفواً، فضلاً اختر على الأقل مهمة واحدة للطباعة");
        }
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=TaskOperationsByTask.pdf");
        final OutputStream outStream = response.getOutputStream();
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        if (startDate == null && endDate == null) {
            param1.append("\n");
            param1.append("تقرير عن حركات المهام بدون تحديد فترة");
        } else {
            param1.append("\n");
            param1.append("تقرير عن حركات المهام حسب الفترة من: " + DateConverter.getHijriStringFromDateLTR(startDate) + " إلى الفترة: " + DateConverter.getHijriStringFromDateLTR(endDate));
        }
        map.put("title", param1.toString());
        List<WrapperUtil> list = new ArrayList<>();
        ListIterator<Long> listIterator = tasksList.listIterator();
        while (listIterator.hasNext()) {
            Long id = listIterator.next();
            Task task = taskService.findOne(id);
            if (task == null) {
                continue;
            }
            WrapperUtil wrapperUtil = new WrapperUtil();
            if (startDate == null && endDate == null) {
                task.setTaskOperations(task.getTaskOperations()
                        .stream()
                        .collect(Collectors.toList()));
                wrapperUtil.setObj1(task);
            } else {
                task.setTaskOperations(task.getTaskOperations().stream().filter(taskOperation -> taskOperation.getDate().after(new Date(startDate)) && taskOperation.getDate().before(new Date(endDate))).collect(Collectors.toList()));
                wrapperUtil.setObj1(task);
            }
            wrapperUtil.setObj2(task.getTaskTos().stream().map(to -> to.getPerson().getName()).collect(Collectors.toList()).toString());
            list.add(wrapperUtil);
        }
        ClassPathResource jrxmlFile = new ClassPathResource("/report/task/TaskOperations.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(list));
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/Tasks", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportTasks(
            @RequestParam("tasksList") List<Long> tasksList,
            HttpServletResponse response)
            throws JRException, IOException {
        if (tasksList.isEmpty()) {
            throw new CustomException("عفواً، فضلاً اختر على الأقل مهمة واحدة للطباعة");
        }
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=Tasks.pdf");
        final OutputStream outStream = response.getOutputStream();
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        param1.append("\n");
        param1.append("تقرير مختصر عن المهام");
        map.put("title", param1.toString());
        map.put("tasks", tasksList.stream().map(value -> taskService.findOne(value)).collect(Collectors.toList()));
        ClassPathResource jrxmlFile = new ClassPathResource("/report/task/Tasks.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/TaskTosCheck", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportTaskTosCheck(
            @RequestParam("tasksList") List<Long> tasksList,
            HttpServletResponse response)
            throws JRException, IOException {
        if (tasksList.isEmpty()) {
            throw new CustomException("عفواً، فضلاً اختر على الأقل مهمة واحدة للطباعة");
        }
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=TaskOperationsByTask.pdf");
        final OutputStream outStream = response.getOutputStream();
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        param1.append("\n");
        param1.append("تقرير متابعة مهام إدارية");
        map.put("title", param1.toString());
        List<WrapperUtil> list = new ArrayList<>();
        initTaskTosCheckList(tasksList, list);
        ClassPathResource jrxmlFile = new ClassPathResource("/report/task/TaskTosCheck.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(list));
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/OutgoingTasksDeductions", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportOutgoingTasksDeductions(
            @RequestParam(value = "personId") Long personId,
            @RequestParam(value = "closeType", required = false) Task.CloseType closeType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response)
            throws JRException, IOException {
        Person person = personService.findOne(personId);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=OutgoingTasksDeductions.pdf");
        final OutputStream outStream = response.getOutputStream();
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        param1.append("\n");
        if (startDate != null && endDate != null) {
            param1.append("تقرير مختصر بخصومات المهام الصادرة من " + person.getNickname() + " / " + person.getName());
            param1.append("\n");
            param1.append("من الفترة " + " ( " + DateConverter.getHijriStringFromDateLTR(startDate) + " ) ");
            param1.append(" ");
            param1.append("إلى الفترة " + " ( " + DateConverter.getHijriStringFromDateLTR(endDate) + " ) ");
        } else {
            param1.append("تقرير مختصر بخصومات المهام الصادرة من " + person.getNickname() + " / " + person.getName());
        }
        map.put("title", param1.toString());
        List<WrapperUtil> list = initOutgoingTasksDeductionsList(personId, closeType, startDate, endDate);
        map.put("list", list);
        log.info("عدد العناصر يساوي: " + list.size());
        ClassPathResource jrxmlFile = new ClassPathResource("/report/task/OutgoingTasksDeductions.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/IncomingTasksDeductions", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportIncomingTasksDeductions(
            @RequestParam(value = "personList") List<Long> personList,
            @RequestParam(value = "closeType", required = false) Task.CloseType closeType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response)
            throws JRException, IOException {
        if (personList.isEmpty()) {
            throw new CustomException("فضلاً اختر موظف واحد على الاقل.");
        }
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=IncomingTasksDeductions.pdf");
        final OutputStream outStream = response.getOutputStream();
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        param1.append("\n");
        if (startDate != null && endDate != null) {
            param1.append("تقرير مختصر بخصومات المهام الواردة إلى الموظفين");
            param1.append(" ");
            param1.append("من الفترة " + " ( " + DateConverter.getHijriStringFromDateLTR(startDate) + " ) ");
            param1.append(" ");
            param1.append("إلى الفترة " + " ( " + DateConverter.getHijriStringFromDateLTR(endDate) + " ) ");
        } else {
            param1.append("تقرير مختصر بخصومات المهام الواردة إلى الموظفين");
        }
        map.put("title", param1.toString());
        List<WrapperUtil> list = initIncomingTasksDeductionsList(personList, closeType, startDate, endDate);
        map.put("list", list);
        log.info("عدد العناصر يساوي: " + list.size());
        ClassPathResource jrxmlFile = new ClassPathResource("/report/task/IncomingTasksDeductions.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/WatchTasksOperations", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportWatchTasksOperations(
            @RequestParam(value = "personList") List<Long> personList,
            @RequestParam(value = "closeType", required = false) Task.CloseType closeType,
            HttpServletResponse response)
            throws JRException, IOException {
        log.info("قراءة كل المهام الواردة لهؤلاء الموظفين");
        if (personList.isEmpty()) {
            throw new CustomException("فضلاً اختر موظف واحد على الاقل.");
        }
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=IncomingTasksDeductions.pdf");
        final OutputStream outStream = response.getOutputStream();
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        param1.append("\n");
        param1.append("تقرير مختصر لمراقبة حركة العمل على المهام للمكلفين");
        //
        map.put("title", param1.toString());
        List<WrapperUtil> list = initWatchTasksOperationsList(personList, closeType);
        map.put("list", list);
        log.info("عدد العناصر يساوي: " + list.size());
        ClassPathResource jrxmlFile = new ClassPathResource("/report/task/WatchTasksOperations.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/email/IncomingTasksDeductions", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void EmailIncomingTasksDeductions(
            @RequestParam(value = "personList") List<Long> personList,
            @RequestParam(value = "closeType", required = false) Task.CloseType closeType,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "message", required = false) String message) throws JRException, IOException, ExecutionException, InterruptedException {
        if (personList.isEmpty()) {
            throw new CustomException("فضلاً اختر موظف واحد على الاقل.");
        }
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        param1.append("\n");
        if (startDate != null && endDate != null) {
            param1.append("تقرير مختصر بخصومات المهام الواردة إلى الموظفين");
            param1.append(" ");
            param1.append("من الفترة " + " ( " + DateConverter.getHijriStringFromDateLTR(startDate) + " ) ");
            param1.append(" ");
            param1.append("إلى الفترة " + " ( " + DateConverter.getHijriStringFromDateLTR(endDate) + " ) ");
        } else {
            param1.append("تقرير مختصر بخصومات المهام الواردة إلى الموظفين");
        }
        map.put("title", param1.toString());
        List<WrapperUtil> list = initIncomingTasksDeductionsList(personList, closeType, startDate, endDate);
        map.put("list", list);
        log.info("عدد العناصر يساوي: " + list.size());
        ClassPathResource jrxmlFile = new ClassPathResource("/report/task/IncomingTasksDeductions.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
        String randomFileName = "IncomingTasksDeductions-" + ThreadLocalRandom.current().nextInt(1, 50000);
        log.info("جاري إنشاء ملف التقرير: " + randomFileName);
        File reportFile = File.createTempFile(randomFileName, ".pdf");
        FileUtils.writeByteArrayToFile(reportFile, bytes);
        Future<Boolean> mail = emailSender.send(title, "<strong dir=\"rtl\" style=\"text-align: center; color: red\">" + message + "</strong>", email, Lists.newArrayList(reportFile));
        mail.get();
        log.info("تم إرسال الملف فى البريد الإلكتروني بنجاح");
    }

    @RequestMapping(value = "/report/TasksClosedSoon", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportTasksClosedSoon(
            @RequestParam(value = "personId") Long personId,
            HttpServletResponse response)
            throws JRException, IOException {
        Person person = personService.findOne(personId);
        Optional.ofNullable(person).orElseThrow(() -> new CustomException("فضلاً اختر الموظف اولاً."));
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=IncomingTasksDeductions.pdf");
        final OutputStream outStream = response.getOutputStream();
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        param1.append("\n");
        param1.append("تقرير عن المهام الباقي على تاريخ إغلاقها أقل من 72 ساعة (ثلاث أيام) من تاريخ اليوم للموظف / " + person.getName());
        map.put("title", param1.toString());
        List<WrapperUtil> list = initTasksClosedSoonNotifyList(personId);
        map.put("list", list);
        ClassPathResource jrxmlFile = new ClassPathResource("/report/task/TasksClosedSoonNotify.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @RequestMapping(value = "/report/ReportOutgoingTasksOperationsToday", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportOutgoingTasksOperationsToday(
            @RequestParam(value = "personId") Long personId,
            @RequestParam(value = "title", required = false) String title,
            HttpServletResponse response)
            throws JRException, IOException {
        Person person = personService.findOne(personId);
        Optional.ofNullable(person).orElseThrow(() -> new CustomException("فضلاً اختر الموظف اولاً."));
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=IncomingTasksDeductions.pdf");
        final OutputStream outStream = response.getOutputStream();
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        param1.append("\n");
        param1.append(Optional.ofNullable(title).isPresent() ? title : "تقرير عن حركات المهام الصادرة من " + person.getNickname() + " / " + person.getName());
        map.put("title", param1.toString());
        List<WrapperUtil> list = initTasksOperationsTodayList(personId);
        map.put("list", list);
        //
        ClassPathResource jrxmlFile = new ClassPathResource("/report/task/TasksOperationsToday.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

    @Async("threadPoolReportGenerator")
    public Future<byte[]> ReportTaskTosCheck(List<Long> tasksList) {
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        param1.append("\n");
        param1.append("تقرير متابعة مهام إدارية");
        map.put("title", param1.toString());
        List<WrapperUtil> list = new ArrayList<>();
        initTaskTosCheckList(tasksList, list);
        try {
            ClassPathResource jrxmlFile = new ClassPathResource("/report/task/TaskTosCheck.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(list));
            return new AsyncResult<>(JasperExportManager.exportReportToPdf(jasperPrint));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Async("threadPoolReportGenerator")
    public Future<byte[]> ReportTasksClosedSoonNotify(Long personId) {
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        param1.append("\n");
        param1.append("تقرير عن المهام الباقي على تاريخ إغلاقها أقل من 72 ساعة (ثلاث أيام) من تاريخ اليوم");
        map.put("title", param1.toString());
        List<WrapperUtil> list = initTasksClosedSoonNotifyList(personId);
        map.put("list", list);
        if (list.isEmpty()) {
            return null;
        }
        try {
            ClassPathResource jrxmlFile = new ClassPathResource("/report/task/TasksClosedSoonNotify.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
            return new AsyncResult<>(JasperExportManager.exportReportToPdf(jasperPrint));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Async("threadPoolReportGenerator")
    public Future<byte[]> ReportTasksOperationsToday(Long personId) {
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        param1.append("\n");
        param1.append("تقرير عن حركات الموظفين على المهام الإدارية");
        map.put("title", param1.toString());
        List<WrapperUtil> list = initTasksOperationsTodayList(personId);
        map.put("list", list);
        if (list.isEmpty()) {
            return null;
        }
        try {
            ClassPathResource jrxmlFile = new ClassPathResource("/report/task/TasksOperationsToday.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
            return new AsyncResult<>(JasperExportManager.exportReportToPdf(jasperPrint));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Async("threadPoolReportGenerator")
    public Future<byte[]> ReportOutgoingTasksDeductions(Long personId) {
        Person person = personService.findOne(personId);
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        param1.append("\n");
        param1.append("تقرير مختصر بإجمالي خصومات المهام الصادرة من " + person.getNickname() + " / " + person.getName());
        map.put("title", param1.toString());
        List<WrapperUtil> list = initOutgoingTasksDeductionsList(personId, null, null, null);
        map.put("list", list);
        if (list.isEmpty()) {
            return null;
        }
        try {
            ClassPathResource jrxmlFile = new ClassPathResource("/report/task/OutgoingTasksDeductions.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
            return new AsyncResult<>(JasperExportManager.exportReportToPdf(jasperPrint));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Async("threadPoolReportGenerator")
    public Future<byte[]> ReportWatchTasksOperations(List<Long> personsList) {
        /**
         * Insert Parameters
         */
        Map<String, Object> map = new HashMap<>();
        StringBuilder param1 = new StringBuilder();
        param1.append("المعهد الأهلي العالي للتدريب");
        param1.append("\n");
        param1.append("تحت إشراف المؤسسة العامة للتدريب المهني والتقني");
        param1.append("\n");
        param1.append("تقرير مختصر لمراقبة حركة العمل على المهام للمكلفين");
        map.put("title", param1.toString());
        List<WrapperUtil> list = initWatchTasksOperationsList(personsList, Task.CloseType.Pending);
        map.put("list", list);
        if (list.isEmpty()) {
            return null;
        }
        try {
            ClassPathResource jrxmlFile = new ClassPathResource("/report/task/WatchTasksOperations.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);
            return new AsyncResult<>(JasperExportManager.exportReportToPdf(jasperPrint));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    private void initTaskTosCheckList(@RequestParam("tasksList") List<Long> tasksList, List<WrapperUtil> list) {
        tasksList.stream().forEach(id -> {
            Task task = taskService.findOne(id);
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(task);
            List<WrapperUtil> tempList = new ArrayList<>();
            taskToService.findByTask(task).stream().forEach(to -> {
                WrapperUtil tempWrapperUtil = new WrapperUtil();
                long warnCount = taskWarnService.countByTaskAndToPerson(task, to.getPerson());
                long deductionCount = taskDeductionService.countByTaskAndToPerson(task, to.getPerson());
                tempWrapperUtil.setObj1(to.getPerson().getName());
                tempWrapperUtil.setObj2(warnCount);
                tempWrapperUtil.setObj3(deductionCount);
                tempWrapperUtil.setObj4(deductionCount * task.getDeduction());
                tempWrapperUtil.setObj5(to.getProgress());
                tempWrapperUtil.setObj6(taskCloseRequestService.countByPersonAndTask(to.getPerson(), task));
                Optional.ofNullable(to.getCloseDate()).ifPresent(value -> tempWrapperUtil.setObj7(DateConverter.getHijriStringFromDateRTL(value)));
                tempList.add(tempWrapperUtil);
            });
            wrapperUtil.setObj2(tempList);
            list.add(wrapperUtil);
        });
    }

    private List<WrapperUtil> initTasksClosedSoonNotifyList(Long personId) {
        log.info("قراءة كل المهام الواردة لهذا المستخدم...");
        List<Task> tasks = taskSearch.search(null, null, Task.CloseType.Pending, null, null, null, null, null, null, true, true, "All", personId);
        log.info("عدد المهام المكلف بها = " + tasks.size());
        log.info("فحص كل مهمة على حدا");
        List<WrapperUtil> list = new ArrayList<>();
        tasks.stream().forEach(task -> {
            log.info("فحص المهمة رقم : " + task.getCode());
            DateTime now = new DateTime();
            DateTime taskEndDate = new DateTime(task.getEndDate());
            int hours = Hours.hoursBetween(now.withTimeAtStartOfDay(), taskEndDate).getHours();
            log.info("عدد الساعات بين تاريخ نهاية المهمة والآن: " + hours);
            log.info("فحص إذا كانت الساعات المتبقية أقل من 3 * 24 ساعة (ثلاث أيام)");
            if (hours < 72) {
                WrapperUtil wrapperUtil = new WrapperUtil();
                wrapperUtil.setObj1("[" + task.getCode() + "]" + " " + task.getTitle());
                wrapperUtil.setObj2(hours + " ساعة");
                wrapperUtil.setObj3(taskCloseRequestService.findByTaskIdAndPersonIdAndTypeAndApprovedIsNull(task.getId(), personId, false).size());
                wrapperUtil.setObj4(taskCloseRequestService.findByTaskIdAndPersonIdAndTypeAndApprovedIsNull(task.getId(), personId, false).size());
                wrapperUtil.setObj5(taskWarnService.findByTaskIdAndToPersonId(task.getId(), personId).size());
                wrapperUtil.setObj6(taskDeductionService.findByTaskIdAndToPersonId(task.getId(), personId).size());
                wrapperUtil.setObj7(DateConverter.getHijriStringFromDateRTLWithTime(task.getEndDate()));
                list.add(wrapperUtil);
            }
        });
        return list;
    }

    private List<WrapperUtil> initTasksOperationsTodayList(Long personId) {
        log.info("قراءة كل المهام الصادرة من هذا المستخدم...");
        List<Task> tasks = taskSearch.search(null, null, Task.CloseType.Pending, null, null, null, null, null, null, false, true, "All", personId);
        log.info("عدد المهام الصادرة منه = " + tasks.size());
        List<WrapperUtil> list = new ArrayList<>();
        log.info("تجميع حركات المهام فى قائمة واحدة للعرض...");
        taskOperationRest.getTaskOperations("Day", tasks).stream().forEach(taskOperation -> {
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(taskOperation.getCode());
            wrapperUtil.setObj2(taskOperation.getTask().getCode());
            wrapperUtil.setObj3(taskOperation.getTask().getTitle());
            wrapperUtil.setObj4(taskOperation.getSender().getNickname() + " / " + taskOperation.getSender().getName());
            wrapperUtil.setObj5(DateConverter.getHijriStringFromDateRTLWithTime(taskOperation.getDate()));
            wrapperUtil.setObj6(taskOperation.getContent());
            wrapperUtil.setObj7(taskOperation.getDate());//Hidden Field for Sorting
            list.add(wrapperUtil);
        });
        list.sort((WrapperUtil w1, WrapperUtil w2) -> ((Date) w1.getObj7()).compareTo((Date) w2.getObj7()));
        return list;
    }

    private List<WrapperUtil> initOutgoingTasksDeductionsList(Long personId, Task.CloseType closeType, Long startDate, Long endDate) {
        Person person = personService.findOne(personId);
        log.info("قراءة كل المهام الصادرة من " + person.getNickname() + " / " + person.getName());
        List<Task> tasks = taskSearch.search(null, null, closeType, null, null, null, null, null, null, false, true, "All", personId);
        log.info("عدد المهام الصادرة منه = " + tasks.size());
        List<WrapperUtil> list = new ArrayList<>();
        log.info("تجميع البيانات...");
        ListIterator<Task> listIterator = tasks.listIterator();
        while (listIterator.hasNext()) {
            Task task = listIterator.next();
            log.info("فحص كل المكلفين بهذة المهمة");
            taskToService.findByTask(task).stream().forEach(taskTo -> {
                WrapperUtil wrapperUtil = new WrapperUtil();
                List<TaskDeduction> allDeduction = new ArrayList<>();
                if (startDate != null && endDate != null) {
                    allDeduction = taskDeductionService.findByTaskAndToPersonAndDateBetween(task, taskTo.getPerson(), new Date(startDate), new Date(endDate));
                } else {
                    allDeduction = taskDeductionService.findByTaskAndToPerson(task, taskTo.getPerson());
                }
                log.info("عدد الخصومات على هذة المهمة يساوي : " + allDeduction.size());
                if (!allDeduction.isEmpty()) {
                    wrapperUtil.setObj1(taskTo.getPerson().getNickname() + " / " + taskTo.getPerson().getName());
                    wrapperUtil.setObj2("[" + task.getCode() + "]" + " " + task.getTitle());
                    wrapperUtil.setObj3(DateConverter.getHijriStringFromDateRTLWithTime(task.getEndDate()));
                    switch (task.getCloseType()) {
                        case Pending:
                            wrapperUtil.setObj4("تحت التنفيذ");
                            break;
                        case Auto:
                            wrapperUtil.setObj4("مغلقة تلقائي");
                            break;
                        case Manual:
                            wrapperUtil.setObj4("ارشيف");
                            break;
                    }
                    wrapperUtil.setObj5(allDeduction
                            .stream()
                            .filter(taskDeduction -> taskDeduction.getType().equals(TaskDeduction.TaskDeductionType.Auto))
                            .mapToDouble(TaskDeduction::getDeduction).sum());
                    wrapperUtil.setObj6(allDeduction
                            .stream()
                            .filter(taskDeduction -> taskDeduction.getType().equals(TaskDeduction.TaskDeductionType.Manual))
                            .mapToDouble(TaskDeduction::getDeduction).sum());
                    if (Optional.ofNullable(taskTo.getDegree()).isPresent()) {
                        switch (taskTo.getDegree()) {
                            case A:
                                wrapperUtil.setObj7("ممتاز");
                                break;
                            case B:
                                wrapperUtil.setObj7("جيد جداً");
                                break;
                            case C:
                                wrapperUtil.setObj7("جيد");
                                break;
                            case D:
                                wrapperUtil.setObj7("مقبول");
                                break;
                            case F:
                                wrapperUtil.setObj7("سيء");
                                break;
                        }
                    } else {
                        wrapperUtil.setObj7("غير محدد");
                    }
                    list.add(wrapperUtil);
                }

            });
        }
        return list;
    }

    private List<WrapperUtil> initIncomingTasksDeductionsList(List<Long> persons, Task.CloseType closeType, Long startDate, Long endDate) {
        List<WrapperUtil> list = new ArrayList<>();
        persons.stream().forEach(personId -> {
            Person person = personService.findOne(personId);
            log.info("قراءة كل المهام الواردة إلى " + person.getNickname() + " / " + person.getName());
            List<Task> tasks = taskSearch.search(null, null, closeType, null, null, null, null, null, null, true, null, "All", personId);
            log.info("عدد المهام الواردة إليه = " + tasks.size());
            log.info("تجميع البيانات...");
            ListIterator<Task> listIterator = tasks.listIterator();
            while (listIterator.hasNext()) {
                Task task = listIterator.next();
                log.info("TaskCloseType: " + task.getCloseType());
                log.info("فحص الخصومات للمهمة رقم : " + task.getCode());
                WrapperUtil wrapperUtil = new WrapperUtil();
                List<TaskDeduction> allDeduction = new ArrayList<>();
                if (startDate != null && endDate != null) {
                    allDeduction = taskDeductionService.findByTaskAndToPersonAndDateBetween(task, person, new Date(startDate), new Date(endDate));
                } else {
                    allDeduction = taskDeductionService.findByTaskAndToPerson(task, person);
                }
                log.info("عدد الخصومات على هذة المهمة يساوي : " + allDeduction.size());
                if (!allDeduction.isEmpty()) {
                    wrapperUtil.setObj1(person.getNickname() + " / " + person.getName());
                    wrapperUtil.setObj2("[" + task.getCode() + "]" + " " + task.getTitle());
                    wrapperUtil.setObj3(task.getPerson().getNickname() + " / " + task.getPerson().getName());
                    wrapperUtil.setObj4(DateConverter.getHijriStringFromDateRTLWithTime(task.getEndDate()));
                    switch (task.getCloseType()) {
                        case Pending:
                            wrapperUtil.setObj5("تحت التنفيذ");
                            break;
                        case Auto:
                            wrapperUtil.setObj5("مغلقة تلقائي");
                            break;
                        case Manual:
                            wrapperUtil.setObj5("ارشيف");
                            break;
                    }
                    wrapperUtil.setObj6(allDeduction
                            .stream()
                            .filter(taskDeduction -> taskDeduction.getType().equals(TaskDeduction.TaskDeductionType.Auto))
                            .mapToDouble(TaskDeduction::getDeduction).sum());
                    wrapperUtil.setObj7(allDeduction
                            .stream()
                            .filter(taskDeduction -> taskDeduction.getType().equals(TaskDeduction.TaskDeductionType.Manual))
                            .mapToDouble(TaskDeduction::getDeduction).sum());
                    log.info("Getting TaskTo Object for this person.");
                    TaskTo taskTo = taskToService.findByTaskAndPerson(task, person);
                    if (Optional.ofNullable(taskTo.getDegree()).isPresent()) {
                        switch (taskTo.getDegree()) {
                            case A:
                                wrapperUtil.setObj8("ممتاز");
                                break;
                            case B:
                                wrapperUtil.setObj8("جيد جداً");
                                break;
                            case C:
                                wrapperUtil.setObj8("جيد");
                                break;
                            case D:
                                wrapperUtil.setObj8("مقبول");
                                break;
                            case F:
                                wrapperUtil.setObj8("سيء");
                                break;
                        }
                    } else {
                        wrapperUtil.setObj8("غير محدد");
                    }
                    list.add(wrapperUtil);
                }
            }
        });
        return list;
    }

    private List<WrapperUtil> initWatchTasksOperationsList(List<Long> persons, Task.CloseType closeType) {
        List<WrapperUtil> list = new ArrayList<>();
        LocalDate today = new DateTime().withTimeAtStartOfDay().toLocalDate();
        LocalDate tomorrow = new DateTime().plusDays(1).withTimeAtStartOfDay().toLocalDate();
        persons.stream().forEach(personId -> {
            Person person = personService.findOne(personId);
            log.info("قراءة كل المهام الواردة إلى " + person.getNickname() + " / " + person.getName());
            List<Task> tasks = taskSearch.search(null, null, closeType, null, null, null, null, null, null, true, null, "All", personId);
            log.info("عدد المهام الواردة إليه = " + tasks.size());
            log.info("تجميع البيانات...");
            ListIterator<Task> listIterator = tasks.listIterator();
            while (listIterator.hasNext()) {
                Task task = listIterator.next();
                WrapperUtil wrapperUtil = new WrapperUtil();
                wrapperUtil.setObj1(person.getNickname() + " / " + person.getName());
                wrapperUtil.setObj2(task.getTitle());
                wrapperUtil.setObj3(task.getPerson().getNickname() + " / " + task.getPerson().getName());
                wrapperUtil.setObj4(DateConverter.getHijriStringFromDateRTLWithTime(task.getEndDate()));
                switch (task.getCloseType()) {
                    case Pending:
                        wrapperUtil.setObj5("تحت التنفيذ");
                        break;
                    case Auto:
                        wrapperUtil.setObj5("مغلقة تلقائي");
                        break;
                    case Manual:
                        wrapperUtil.setObj5("ارشيف");
                        break;
                }
                long countToday = taskOperationService.countByTaskAndSenderAndDateBetween(task, person, today.toDate(), tomorrow.toDate());
                long countAll = taskOperationService.countByTaskAndSender(task, person);
                wrapperUtil.setObj6(countToday);
                TaskOperation topOperation = taskOperationService.findTopByTaskAndSenderOrderByDateDesc(task, person);
                wrapperUtil.setObj7(Optional.ofNullable(topOperation).isPresent() ? DateConverter.getHijriStringFromDateRTLWithTime(topOperation.getDate()) : "");
                wrapperUtil.setObj8(countAll);
                wrapperUtil.setObj9(task.getCode());
                list.add(wrapperUtil);
            }
        });
        list.sort((WrapperUtil w1, WrapperUtil w2) -> ((Integer) w1.getObj9()).compareTo((Integer) w2.getObj9()));
        return list;
    }
}
