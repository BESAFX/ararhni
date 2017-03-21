package com.besafx.app.rest;

import com.besafx.app.entity.Screen;
import com.besafx.app.service.ScreenService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/screen/")
public class ScreenRest {

    @Autowired
    private ScreenService screenService;

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Screen> findAll() {
        return Lists.newArrayList(screenService.findAll());
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Screen findOne(@PathVariable Long id) {
        return screenService.findOne(id);
    }
}
