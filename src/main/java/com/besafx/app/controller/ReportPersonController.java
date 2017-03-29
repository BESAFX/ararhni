package com.besafx.app.controller;

import com.besafx.app.config.CustomException;
import com.besafx.app.service.PersonService;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ReportPersonController {

    @Autowired
    private PersonService personService;

    @RequestMapping(value = "/report/Persons", method = RequestMethod.GET, produces = "application/pdf")
    @ResponseBody
    public void ReportPersons(@RequestParam("personsList") List<Long> personsList, HttpServletResponse response) throws JRException, IOException {

        if (personsList.isEmpty()) {
            throw new CustomException("عفواً، فضلاً اختر على الأقل مستخدم واحد للطباعة");
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=Persons.pdf");
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
        param1.append("تقرير مختصر عن المستخدمين");
        map.put("title", param1.toString());
        map.put("persons", personsList.stream().map(value -> personService.findOne(value)).collect(Collectors.toList()));

        ClassPathResource jrxmlFile = new ClassPathResource("/report/person/Persons.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getInputStream());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map);

        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
    }

}
