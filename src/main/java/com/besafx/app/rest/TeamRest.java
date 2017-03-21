package com.besafx.app.rest;

import com.besafx.app.entity.Team;
import com.besafx.app.service.PersonService;
import com.besafx.app.service.RoleService;
import com.besafx.app.service.TeamService;
import com.besafx.app.util.NotifyCode;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/team/")
public class TeamRest {

    @Autowired
    private PersonService personService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TEAM_CREATE')")
    public Team create(@RequestBody Team team, Principal principal) {
        team = teamService.save(team);
        notificationService.notifyOne(Notification
                .builder()
                .title("العمليات على المجموعات")
                .message("تم اضافة مجموعة صلاحيات جديدة بنجاح")
                .type("success")
                .icon("fa-shield")
                .build(), principal.getName());
        notificationService.notifyAllExceptMe(Notification
                .builder()
                .title("العمليات على المجموعات")
                .message("تم اضافة مجموعة صلاحيات جديدة بواسطة " +  personService.findByEmail(principal.getName()).getName())
                .type("warning")
                .icon("fa-shield")
                .build());
        return team;
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TEAM_UPDATE')")
    public Team update(@RequestBody Team team, Principal principal) {
        Team object = teamService.findOne(team.getId());
        if (object != null) {
            team = teamService.save(team);
            notificationService.notifyOne(Notification
                    .builder()
                    .title("العمليات على مجموعات المستخدمين")
                    .message("تم تعديل بيانات المجموعة بنجاح")
                    .type("success")
                    .icon("fa-shield")
                    .build(), principal.getName());
            notificationService.notifyAllExceptMe(Notification
                    .builder()
                    .title("العمليات على مجموعات المستخدمين")
                    .message("تم تعديل بيانات المجموعة " + team.getName() +  " بواسطة " + personService.findByEmail(principal.getName()).getName())
                    .type("warning")
                    .icon("fa-shield")
                    .build());
            return team;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_TEAM_DELETE')")
    public void delete(@PathVariable Long id) {
        Team team = teamService.findOne(id);
        if (team != null) {
            roleService.findByTeam(team).stream().forEach(role -> roleService.delete(role));
            teamService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Team> findAll() {
        return Lists.newArrayList(teamService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Team findOne(@PathVariable Long id) {
        return teamService.findOne(id);
    }
}
