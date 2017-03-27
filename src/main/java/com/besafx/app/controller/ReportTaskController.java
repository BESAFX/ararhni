package com.besafx.app.controller;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Task;
import com.besafx.app.service.TaskService;
import com.besafx.app.util.DateConverter;
import com.besafx.app.util.WrapperUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ReportTaskController {

    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "/report/TaskOperations", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportTaskOperationsByTask(
            @RequestParam("tasksList") List<Long> tasksList,
            @RequestParam(value = "startDate", required = false) Long startDate,
            @RequestParam(value = "endDate", required = false) Long endDate,
            HttpServletResponse response) throws JRException, IOException {

        if (tasksList.isEmpty()) {
            throw new CustomException("عفواً، فضلاً اختر على الأقل مهمة واحدة للطباعة");
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=Report.pdf");
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
        tasksList.stream().forEach(id -> {
            Task task = taskService.findOne(id);
            WrapperUtil wrapperUtil = new WrapperUtil();
            if (startDate == null && endDate == null) {
                wrapperUtil.setObj1(task);
            } else {
                task.setTaskOperations(task.getTaskOperations().stream().filter(taskOperation -> taskOperation.getDate().after(new Date(startDate)) && taskOperation.getDate().before(new Date(endDate))).collect(Collectors.toList()));
                wrapperUtil.setObj1(task);
            }
            wrapperUtil.setObj2(task.getTaskTos().stream().map(to -> to.getPerson().getName()).collect(Collectors.toList()).toString());
            list.add(wrapperUtil);
        });

        ClassPathResource jrxmlFile = new ClassPathResource("/report/task/TaskOperations.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(list));

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

}
