package com.wb.controller;

import com.wb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class UserController {

    @Autowired
    private UserService userService;


    @RequestMapping("/toLogin")
    public String toLogin() {
        return "toLogin";
    }
}
