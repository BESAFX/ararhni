package com.besafx.app.rest;

import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Person;
import com.besafx.app.service.BranchService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/branch/")
public class BranchRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BRANCH_CREATE')")
    public Branch create(@RequestBody Branch branch, Principal principal) {
        Integer maxCode = branchService.findMaxCode();
        if (maxCode == null) {
            branch.setCode(1);
        } else {
            branch.setCode(maxCode + 1);
        }
        branch = branchService.save(branch);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على الفروع")
                .message("تم اضافة فرع جديد بنجاح")
                .type("success")
                .icon("fa-cubes")
                .build(), principal.getName());
        return branch;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BRANCH_UPDATE')")
    public Branch update(@RequestBody Branch branch, Principal principal) {
        Branch object = branchService.findOne(branch.getId());
        if (object != null) {
            branch = branchService.save(branch);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على الفروع")
                    .message("تم تعديل بيانات الفرع بنجاح")
                    .type("success")
                    .icon("fa-cubes")
                    .build(), principal.getName());
//            notificationService.notifyAllExceptMe(Notification
//                    .builder()
//                    .title("العمليات على الفروع")
//                    .message("تم تعديل بيانات الفرع رقم " + branch.getCode() +  " بواسطة " + personService.findByEmail(principal.getName()).getName())
//                    .type("warning")
//                    .icon("fa-cubes")
//                    .build());
            return branch;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BRANCH_DELETE')")
    public void delete(@PathVariable Long id) {
        Branch object = branchService.findOne(id);
        if (object != null) {
            branchService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Branch> findAll() {
        return Lists.newArrayList(branchService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Branch findOne(@PathVariable Long id) {
        return branchService.findOne(id);
    }

    @RequestMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long count() {
        return branchService.count();
    }

    @RequestMapping(value = "findByName/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Branch findByName(@PathVariable(value = "name") String name) {
        return branchService.findByName(name);
    }

    @RequestMapping(value = "findByCode/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Branch findByCode(@PathVariable(value = "code") Integer code) {
        return branchService.findByCode(code);
    }

    @RequestMapping(value = "fetchTableData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Branch> fetchTableData(Principal principal) {
        List<Branch> list = new ArrayList<>();
        Person person = personService.findByEmail(principal.getName());
        person.getCompanies().stream().forEach(company -> company.getRegions().stream().forEach(region -> list.addAll(region.getBranches())));
        person.getRegions().stream().forEach(region -> list.addAll(region.getBranches()));
        person.getDepartments().stream().forEach(department -> list.add(department.getBranch()));
        person.getEmployees().stream().forEach(employee -> list.add(employee.getDepartment().getBranch()));
        list.addAll(person.getBranches());
        return list.stream().distinct().collect(Collectors.toList());
    }

}
