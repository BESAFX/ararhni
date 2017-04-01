package com.besafx.app.init;

import com.besafx.app.entity.*;
import com.besafx.app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class Initializer implements CommandLineRunner {

    @Autowired
    private ScreenService screenService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PersonService personService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (personService.count() == 0) {
            runForFirstTimeOnly();
        }
    }

    private void runForFirstTimeOnly() {

        Team team = new Team();
        team.setName("الدعم الفني");
        teamService.save(team);

        Person person = new Person();
        person.setName("الدعم الفني");
        person.setPhoto("/no-image.jpg");
        person.setQualification("مدير النظام");
        person.setTeam(team);
        person.setEmail("admin@email.com");
        person.setPassword(passwordEncoder.encode("admin"));
        person.setEnabled(true);
        person.setTokenExpired(false);
        person.setOptionThemeName("black");
        personService.save(person);

        Screen screen = new Screen();
        screen.setCode("COMPANY");
        screen.setName("الشركات");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode("REGION");
        screen.setName("المناطق");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode("BRANCH");
        screen.setName("الفروع");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode("DEPARTMENT");
        screen.setName("الأقسام");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode("EMPLOYEE");
        screen.setName("الموظفون");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode("TEAM");
        screen.setName("مجموعات الصلاحيات");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode("PERSON");
        screen.setName("المستخدمون");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode("TASK");
        screen.setName("المهام");
        screenService.save(screen);

        screen = new Screen();
        screen.setCode("TASK_OPERATION");
        screen.setName("حركات المهام");
        screenService.save(screen);

        Iterator<Screen> iterator = screenService.findAll().iterator();

        while (iterator.hasNext()) {

            screen = iterator.next();

            Permission permissionFounded = permissionService
                    .findByCreateEntityAndUpdateEntityAndDeleteEntityAndReportEntityAndScreen(
                            true,
                            true,
                            true,
                            true,
                            screen);

            if (permissionFounded == null) {
                Permission permission = new Permission();
                permission.setScreen(screen);
                permission.setCreateEntity(true);
                permission.setUpdateEntity(true);
                permission.setDeleteEntity(true);
                permission.setReportEntity(true);
                permissionService.save(permission);

                Role role = new Role();
                role.setTeam(team);
                role.setPermission(permission);
                roleService.save(role);
            } else {
                Role role = new Role();
                role.setTeam(team);
                role.setPermission(permissionFounded);
                roleService.save(role);
            }
        }
    }
}
