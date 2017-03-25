package com.besafx.app.rest;

import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Department;
import com.besafx.app.entity.Person;
import com.besafx.app.service.DepartmentService;
import com.besafx.app.service.PersonService;
import com.besafx.app.util.NotifyCode;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/department/")
public class DepartmentRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_DEPARTMENT_CREATE')")
    public Department create(@RequestBody Department department, Principal principal) {
        Integer maxCode = departmentService.findMaxCode();
        if (maxCode == null) {
            department.setCode(1);
        } else {
            department.setCode(maxCode + 1);
        }
        department = departmentService.save(department);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على الأقسام")
                .message("تم اضافة قسم جديد بنجاح")
                .type("success")
                .icon("fa-sitemap")
                .build(), principal.getName());
        notificationService.notifyAllExceptMe(Notification
                .builder()
                .title("العمليات على الأقسام")
                .message("تم اضافة قسم جديد بواسطة " +  personService.findByEmail(principal.getName()).getName())
                .type("warning")
                .icon("fa-sitemap")
                .build());
        return department;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_DEPARTMENT_UPDATE')")
    public Department update(@RequestBody Department department, Principal principal) {
        Department object = departmentService.findOne(department.getId());
        if (object != null) {
            department = departmentService.save(department);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على الأقسام")
                    .message("تم تعديل بيانات القسم بنجاح")
                    .type("success")
                    .icon("fa-sitemap")
                    .build(), principal.getName());
            notificationService.notifyAllExceptMe(Notification
                    .builder()
                    .title("العمليات على الأقسام")
                    .message("تم تعديل بيانات القسم رقم " + department.getCode() +  " بواسطة " + personService.findByEmail(principal.getName()).getName())
                    .type("warning")
                    .icon("fa-sitemap")
                    .build());
            return department;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_DEPARTMENT_DELETE')")
    public void delete(@PathVariable Long id) {
        Department object = departmentService.findOne(id);
        if (object != null) {
            departmentService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Department> findAll() {
        return Lists.newArrayList(departmentService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Department findOne(@PathVariable Long id) {
        return departmentService.findOne(id);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return departmentService.count();
    }

    @RequestMapping(value = "fetchTableData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Department> fetchTableData(Principal principal) {
        List<Department> list = new ArrayList<>();
        Person person = personService.findByEmail(principal.getName());
        person.getCompanies().stream().forEach(company -> company.getRegions().stream().forEach(region -> region.getBranches().stream().forEach(branch -> list.addAll(branch.getDepartments()))));
        person.getRegions().stream().forEach(region -> region.getBranches().stream().forEach(branch -> list.addAll(branch.getDepartments())));
        person.getBranches().stream().forEach(branch -> list.addAll(branch.getDepartments()));
        person.getDepartments().stream().forEach(department -> list.add(department));
        person.getEmployees().stream().forEach(employee -> list.add(employee.getDepartment()));
        list.addAll(person.getDepartments());
        return list.stream().distinct().collect(Collectors.toList());
    }

}
