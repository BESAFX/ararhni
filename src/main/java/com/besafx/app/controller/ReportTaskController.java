package com.besafx.app.controller;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Task;
import com.besafx.app.service.*;
import com.besafx.app.util.DateConverter;
import com.besafx.app.util.WrapperUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@RestController
public class ReportTaskController {

    private final Logger log = LoggerFactory.getLogger(ReportTaskController.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskToService taskToService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskWarnService taskWarnService;

    @Autowired
    private TaskDeductionService taskDeductionService;

    @Autowired
    private TaskCloseRequestService taskCloseRequestService;

    @RequestMapping(value = "/report/TaskOperations", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportTaskOperationsByTask(@RequestParam("tasksList") List<Long> tasksList, @RequestParam(value = "startDate", required = false) Long startDate, @RequestParam(value = "endDate", required = false) Long endDate, HttpServletResponse response) throws JRException, IOException {

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
    public void ReportTasks(@RequestParam("tasksList") List<Long> tasksList, HttpServletResponse response) throws JRException, IOException {

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
    public void ReportTaskTosCheck(@RequestParam("tasksList") List<Long> tasksList, HttpServletResponse response) throws JRException, IOException {

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

    @Async("threadPoolReportGenerator")
    public Future<byte[]> ReportTaskTosCheck(List<Long> tasksList) throws JRException, IOException, InterruptedException {
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
        return new AsyncResult<>(JasperExportManager.exportReportToPdf(jasperPrint));
    }

    private void initTaskTosCheckList(@RequestParam("tasksList") List<Long> tasksList, List<WrapperUtil> list) {
        tasksList.stream().forEach(id -> {
            Task task = taskService.findOne(id);
            WrapperUtil wrapperUtil = new WrapperUtil();
            wrapperUtil.setObj1(task);

            List<WrapperUtil> tempList = new ArrayList<>();
            taskToService.findByTask(task).stream().forEach(to -> {
                WrapperUtil tempWrapperUtil = new WrapperUtil();
                tempWrapperUtil.setObj1(to.getPerson().getName());
                tempWrapperUtil.setObj2(task.getTaskWarns().stream().filter(taskWarn -> taskWarn.getToPerson().getId().intValue() == to.getId()));
                long deductionCount = task.getTaskDeductions().stream().filter(taskDeduction -> taskDeduction.getToPerson().getId().intValue() == to.getId()).count();
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
}
