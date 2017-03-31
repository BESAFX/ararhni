package com.besafx.app.rest;

import com.besafx.app.entity.Person;
import com.besafx.app.entity.Region;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.RegionService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/region/")
public class RegionRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_REGION_CREATE')")
    public Region create(@RequestBody Region region, Principal principal) {
        Integer maxCode = regionService.findMaxCode();
        if (maxCode == null) {
            region.setCode(1);
        } else {
            region.setCode(maxCode + 1);
        }
        region = regionService.save(region);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على المناطق")
                .message("تم اضافة منطقة جديدة بنجاح")
                .type("success")
                .icon("fa-map-marker")
                .build(), principal.getName());
        notificationService.notifyAllExceptMe(Notification
                .builder()
                .title("العمليات على المناطق")
                .message("تم اضافة منطقة جديدة بواسطة " + personService.findByEmail(principal.getName()).getName())
                .type("warning")
                .icon("fa-map-marker")
                .build());
        return region;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_REGION_UPDATE')")
    public Region update(@RequestBody Region region, Principal principal) {
        Region object = regionService.findOne(region.getId());
        if (object != null) {
            region = regionService.save(region);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على المناطق")
                    .message("تم تعديل بيانات المنطقة بنجاح")
                    .type("success")
                    .icon("fa-map-marker")
                    .build(), principal.getName());
            notificationService.notifyAllExceptMe(Notification
                    .builder()
                    .title("العمليات على المناطق")
                    .message("تم تعديل بيانات المنطقة رقم " + region.getCode() +  " بواسطة " + personService.findByEmail(principal.getName()).getName())
                    .type("warning")
                    .icon("fa-map-marker")
                    .build());
            return region;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_REGION_DELETE')")
    public void delete(@PathVariable Long id) {
        Region object = regionService.findOne(id);
        if (object != null) {
            regionService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Region> findAll() {
        return Lists.newArrayList(regionService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Region findOne(@PathVariable Long id) {
        return regionService.findOne(id);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return regionService.count();
    }

    @RequestMapping(value = "findByName/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Region findByName(@PathVariable(value = "name") String name) {
        return regionService.findByName(name);
    }

    @RequestMapping(value = "findByCode/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Region findByCode(@PathVariable(value = "code") Integer code) {
        return regionService.findByCode(code);
    }

    @RequestMapping(value = "fetchTableData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Region> fetchTableData(Principal principal) {
        List<Region> list = new ArrayList<>();
        Person person = personService.findByEmail(principal.getName());
        person.getCompanies().stream().forEach(company -> list.addAll(company.getRegions()));
        person.getBranches().stream().forEach(branch -> list.add(branch.getRegion()));
        person.getDepartments().stream().forEach(department -> list.add(department.getBranch().getRegion()));
        person.getEmployees().stream().forEach(employee -> list.add(employee.getDepartment().getBranch().getRegion()));
        list.addAll(person.getRegions());
        return list.stream().distinct().collect(Collectors.toList());
    }

}
