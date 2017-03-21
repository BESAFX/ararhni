package com.besafx.app.rest;

import com.besafx.app.entity.Permission;
import com.besafx.app.service.PermissionService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/permission/")
public class PermissionRest {

    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERMISSION_CREATE')")
    public Permission create(@RequestBody Permission permission) {
        return permissionService.save(permission);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERMISSION_UPDATE')")
    public Permission update(@RequestBody Permission permission) {
        Permission object = permissionService.findOne(permission.getId());
        if (object != null) {
            return permissionService.save(permission);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERMISSION_DELETE')")
    public void delete(@PathVariable Long id) {
        Permission object = permissionService.findOne(id);
        if (object != null) {
            permissionService.delete(id);
        }
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Permission> findAll() {
        return Lists.newArrayList(permissionService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Permission findOne(@PathVariable Long id) {
        return permissionService.findOne(id);
    }
}
