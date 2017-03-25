package com.besafx.app.rest;

import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Employee;
import com.besafx.app.entity.Person;
import com.besafx.app.service.EmployeeService;
import com.besafx.app.service.PersonService;
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
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/employee/")
public class EmployeeRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_EMPLOYEE_CREATE')")
    public Employee create(@RequestBody Employee employee, Principal principal) {
        if (employeeService.findByPersonAndDepartment(employee.getPerson(), employee.getDepartment()) != null) {
            throw new CustomException("هذا الموظف موجود بالفعل");
        }
        Integer maxCode = employeeService.findMaxCode();
        if (maxCode == null) {
            employee.setCode(1);
        } else {
            employee.setCode(maxCode + 1);
        }
        employee = employeeService.save(employee);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على الموظفين")
                .message("تم اضافة موظف جديد بنجاح")
                .type("success")
                .icon("fa-user-circle")
                .build(), principal.getName());
        notificationService.notifyAllExceptMe(Notification
                .builder()
                .title("العمليات على الموظفين")
                .message("تم اضافة موظف جديد بواسطة " + personService.findByEmail(principal.getName()).getName())
                .type("warning")
                .icon("fa-user-circle")
                .build());
        return employee;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_EMPLOYEE_UPDATE')")
    public Employee update(@RequestBody Employee employee, Principal principal) {
        Employee object = employeeService.findOne(employee.getId());
        if (object != null) {
            Optional.ofNullable(employeeService.findByPersonAndDepartment(employee.getPerson(), employee.getDepartment())).ifPresent(value -> {
                if (object.getId() != value.getId()) {
                    throw new CustomException("هذا الموظف موجود بالفعل");
                }
            });
            employee = employeeService.save(employee);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على الموظفين")
                    .message("تم تعديل بيانات الموظف بنجاح")
                    .type("success")
                    .icon("fa-user-circle")
                    .build(), principal.getName());
            notificationService.notifyAllExceptMe(Notification
                    .builder()
                    .title("العمليات على الموظفين")
                    .message("تم تعديل بيانات الموظف رقم " + employee.getCode() + " بواسطة " + personService.findByEmail(principal.getName()).getName())
                    .type("warning")
                    .icon("fa-user-circle")
                    .build());
            return employee;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_EMPLOYEE_DELETE')")
    public void delete(@PathVariable Long id) {
        Employee object = employeeService.findOne(id);
        if (object != null) {
            employeeService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Employee> findAll() {
        return Lists.newArrayList(employeeService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Employee findOne(@PathVariable Long id) {
        return employeeService.findOne(id);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return employeeService.count();
    }

    @RequestMapping(value = "fetchTableData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Employee> fetchTableData(Principal principal) {
        List<Employee> list = new ArrayList<>();
        Person person = personService.findByEmail(principal.getName());
        person.getCompanies().stream().forEach(company -> company.getRegions().stream().forEach(region -> region.getBranches().stream().forEach(branch -> branch.getDepartments().stream().forEach(department -> list.addAll(department.getEmployees())))));
        person.getRegions().stream().forEach(region -> region.getBranches().stream().forEach(branch -> branch.getDepartments().stream().forEach(department -> list.addAll(department.getEmployees()))));
        person.getBranches().stream().forEach(branch -> branch.getDepartments().stream().forEach(department -> list.addAll(department.getEmployees())));
        person.getDepartments().stream().forEach(department -> list.addAll(department.getEmployees()));
        list.addAll(person.getEmployees());
        return list.stream().distinct().collect(Collectors.toList());
    }

}
