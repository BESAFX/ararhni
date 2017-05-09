package com.besafx.app.rest;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.*;
import com.besafx.app.service.*;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/person/")
public class PersonRest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private PersonService personService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERSON_CREATE')")
    public Person create(@RequestBody Person person, Principal principal) {
        if (personService.findByEmail(person.getEmail()) != null) {
            throw new CustomException("هذا البريد الإلكتروني غير متاح ، فضلاً ادخل بريد آخر غير مستخدم");
        }
        person.setHiddenPassword(person.getPassword());
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setEnabled(true);
        person.setTokenExpired(false);
        person.setOptionThemeName("black");
        person = personService.save(person);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على حسابات المستخدمين")
                .message("تم اضافة مستخدم جديد بنجاح")
                .type("success")
                .icon("fa-plus-circle")
                .build(), principal.getName());
        return person;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERSON_UPDATE') or hasRole('ROLE_PROFILE_UPDATE')")
    @JsonView(Views.Summery.class)
    public Person update(@RequestBody Person person, Principal principal) {
        Person object = personService.findOne(person.getId());
        if (object != null) {
            if (!object.getPassword().equals(person.getPassword())) {
                person.setHiddenPassword(person.getPassword());
                person.setPassword(passwordEncoder.encode(person.getPassword()));
            } else {
                person.setHiddenPassword(object.getHiddenPassword());
            }
            person = personService.save(person);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على حسابات المستخدمين")
                    .message("تم تعديل بيانات الحساب بنجاح")
                    .type("success")
                    .icon("fa-edit")
                    .build(), principal.getName());
            return person;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERSON_DELETE')")
    public void delete(@PathVariable Long id) {
        Person object = personService.findOne(id);
        if (object != null) {
            personService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Person> findAll() {
        return Lists.newArrayList(personService.findAll());
    }

    @RequestMapping(value = "findAllSummery", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @JsonView(Views.Summery.class)
    public List<Person> findAllSummery() {
        return Lists.newArrayList(personService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Person findOne(@PathVariable Long id) {
        return personService.findOne(id);
    }

    @RequestMapping(value = "findByEmail/{email}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Person findByEmail(@PathVariable(value = "email") String email) {
        return personService.findByEmail(email);
    }

    @RequestMapping("findActivePerson")
    @ResponseBody
    public Person findActivePerson(Principal principal) {
        return personService.findByEmail(principal.getName());
    }

    @RequestMapping("findActivePersonManager")
    @ResponseBody
    public Person findActivePersonManager(Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        return getPersonManager(person);
    }

    @RequestMapping("findActivePersonManagerSummery")
    @ResponseBody
    @JsonView(Views.Summery.class)
    public Person findActivePersonManagerSummery(Principal principal) {
        Person person = personService.findByEmail(principal.getName());
        return getPersonManager(person);
    }

    public Person getPersonManager(Person person) {
        List<Employee> employees = employeeService.findByPerson(person);
        List<Department> departments = departmentService.findByManager(person);
        List<Branch> branches = branchService.findByManager(person);
        List<Region> regions = regionService.findByManager(person);
        List<Company> companies = companyService.findByManager(person);
        if (!employees.isEmpty()) {
            return employees.get(0).getDepartment().getBranch().getRegion().getCompany().getManager();
        } else if (!departments.isEmpty()) {
            return departments.get(0).getBranch().getRegion().getCompany().getManager();
        } else if (!branches.isEmpty()) {
            return branches.get(0).getRegion().getCompany().getManager();
        } else if (!regions.isEmpty()) {
            return regions.get(0).getCompany().getManager();
        } else if (companies.isEmpty()) {
            return companies.get(0).getManager();
        } else {
            return person;
        }
    }

    @RequestMapping("findAuthorities")
    @ResponseBody
    public List<String> findAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
    }

    @RequestMapping("findHiddenPassword/{email:.+}")
    @ResponseBody
    public String findHiddenPassword(@PathVariable(value = "email") String email, Authentication authentication) {
        if (authentication.isAuthenticated()) {
            Person person = personService.findByEmail(email);
            if (person != null) {
                return person.getHiddenPassword();
            } else {
                return "تأكد من البريد الإلكتروني، يجب أن يكون مسجلاً";
            }
        } else {
            return "غير مصرح لك";
        }
    }

    @RequestMapping(value = "countPersonsByTeam/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Integer countPersonsByTeam(@PathVariable(value = "id") Long id) {
        return personService.countByTeam(teamService.findOne(id));
    }

    @RequestMapping(value = "findPersonUnderMe", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Person> findPersonUnderMe(Principal principal) {
        Person me = personService.findByEmail(principal.getName());
        List<Person> list = new ArrayList<>();
        me.getCompanies().stream().forEach(company -> {
            list.add(company.getManager());
            company.getRegions().stream().forEach(region -> {
                list.add(region.getManager());
                region.getBranches().stream().forEach(branch -> {
                    list.add(branch.getManager());
                    branch.getDepartments().stream().forEach(department -> {
                        list.add(department.getManager());
                        department.getEmployees().stream().forEach(employee -> list.add(employee.getPerson()));
                    });
                });
            });
        });
        me.getRegions().stream().forEach(region -> {
            list.add(region.getManager());
            region.getBranches().stream().forEach(branch -> {
                list.add(branch.getManager());
                branch.getDepartments().stream().forEach(department -> {
                    list.add(department.getManager());
                    department.getEmployees().stream().forEach(employee -> list.add(employee.getPerson()));
                });
            });
        });
        me.getBranches().stream().forEach(branch -> {
            list.add(branch.getManager());
            branch.getDepartments().stream().forEach(department -> {
                list.add(department.getManager());
                department.getEmployees().stream().forEach(employee -> list.add(employee.getPerson()));
            });
        });
        me.getDepartments().stream().forEach(department -> {
            list.add(department.getManager());
            department.getEmployees().stream().forEach(employee -> list.add(employee.getPerson()));
        });
        me.getEmployees().stream().forEach(employee -> list.add(employee.getPerson()));
        return list.stream().distinct().collect(Collectors.toList());
    }

    @RequestMapping(value = "findPersonUnderMeSummery", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @JsonView(Views.Summery.class)
    public List<Person> findPersonUnderMeSummery(Principal principal) {
        return findPersonUnderMe(principal);
    }

}
