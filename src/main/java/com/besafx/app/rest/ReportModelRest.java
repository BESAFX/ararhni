package com.besafx.app.rest;

import com.besafx.app.entity.Person;
import com.besafx.app.entity.ReportModel;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.ReportModelService;
import com.besafx.app.service.ScreenService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/reportModel/")
public class ReportModelRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private ScreenService screenService;

    @Autowired
    private ReportModelService reportModelService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ReportModel create(@RequestBody ReportModel reportModel, Principal principal) {
        Person me = personService.findByEmail(principal.getName());
        reportModel.setPerson(me);
        reportModel = reportModelService.save(reportModel);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على نماذج الطباعة")
                .message("تم اضافة النموذج الجديد بنجاح")
                .type("success")
                .icon("fa-print")
                .build(), me.getEmail());
        notificationService.notifyAllExceptMe(Notification
                .builder()
                .title("العمليات على نماذج الطباعة")
                .message("تم اضافة نموذج جديد بواسطة " +  personService.findByEmail(principal.getName()).getName())
                .type("warning")
                .icon("fa-print")
                .build());
        return reportModel;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ReportModel update(@RequestBody ReportModel reportModel, Principal principal) {
        ReportModel object = reportModelService.findOne(reportModel.getId());
        if (object != null) {
            Person me = personService.findByEmail(principal.getName());
            reportModel.setPerson(me);
            reportModel = reportModelService.save(reportModel);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على نماذج الطباعة")
                    .message("تم تعديل بيانات النموذج بنجاح")
                    .type("success")
                    .icon("fa-print")
                    .build(), principal.getName());
            notificationService.notifyAllExceptMe(Notification
                    .builder()
                    .title("العمليات على نماذج الطباعة")
                    .message("تم تعديل بيانات نموذج"  +  " بواسطة " + me.getName())
                    .type("warning")
                    .icon("fa-print")
                    .build());
            return reportModel;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable Long id) {
        ReportModel object = reportModelService.findOne(id);
        if (object != null) {
            reportModelService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ReportModel> findAll() {
        return Lists.newArrayList(reportModelService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ReportModel findOne(@PathVariable Long id) {
        return reportModelService.findOne(id);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return reportModelService.count();
    }

    @RequestMapping(value = "findByScreen/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ReportModel> findByScreen(@PathVariable(value = "id") Long id) {
        return reportModelService.findByScreen(screenService.findOne(id));
    }


}
