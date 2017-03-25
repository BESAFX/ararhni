package com.besafx.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PartialController {

    @Autowired
    private SessionRegistry sessionRegistry;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView navToHome() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal().equals("anonymousUser")) {
            return new ModelAndView("redirect:/login");
        } else {
            return new ModelAndView("index");
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String navToLogin() {
        return "login";
    }


    @RequestMapping(value = "/getAllLoggedUsers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> getLoggedUsers() {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = {
            "/login",
            "/home",
            "/menu",
            "/company",
            "/region",
            "/branch",
            "/department",
            "/employee",
            "/team",
            "/person",
            "/task",
            "/reportModel",
            "/help",
            "/profile"
    })
    public ModelAndView navToView() {
        return new ModelAndView("forward:/");
    }
}
