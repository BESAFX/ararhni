package com.besafx.app.rest;

import com.besafx.app.entity.Permission;
import com.besafx.app.entity.Role;
import com.besafx.app.entity.Team;
import com.besafx.app.service.PermissionService;
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
@RequestMapping(value = "/api/role")
public class RoleRest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ROLE_CREATE')")
    public Role create(@RequestBody Role role) {
        return roleService.save(role);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ROLE_UPDATE')")
    public Role update(@RequestBody Role role) {
        Role object = roleService.findOne(role.getId());
        if (object != null) {
            return roleService.save(role);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ROLE_DELETE')")
    public void delete(@PathVariable Long id) {
        Role object = roleService.findOne(id);
        if (object != null) {
            roleService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Role> findAll() {
        return Lists.newArrayList(roleService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Role findOne(@PathVariable Long id) {
        return roleService.findOne(id);
    }

    @RequestMapping(value = "findByTeam/{teamId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Role> findByTeam(@PathVariable(value = "teamId") Long teamId) {
        return roleService.findByTeam(teamService.findOne(teamId));
    }

    @RequestMapping(value = "setUpRoles", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Role> setUpRoles(@RequestBody List<Role> roles, Principal principal) {

        Team team = teamService.save(roles.get(0).getTeam());

        roles.stream().forEach(role -> {
            Permission permission = permissionService.findByCreateEntityAndUpdateEntityAndDeleteEntityAndReportEntityAndScreen(
                    role.getPermission().getCreateEntity(),
                    role.getPermission().getUpdateEntity(),
                    role.getPermission().getDeleteEntity(),
                    role.getPermission().getReportEntity(),
                    role.getPermission().getScreen()
            );
            if (permission == null) {

                permission = new Permission();
                permission.setCreateEntity(role.getPermission().getCreateEntity());
                permission.setUpdateEntity(role.getPermission().getUpdateEntity());
                permission.setDeleteEntity(role.getPermission().getDeleteEntity());
                permission.setReportEntity(role.getPermission().getReportEntity());
                permission.setScreen(role.getPermission().getScreen());
                permissionService.save(permission);
            }

            role.setPermission(permission);
            role.setTeam(team);
            roleService.save(role);

        });

        return roles;
    }
}
