package com.wb.controller;

import com.wb.model.User;
import com.wb.service.MessageService;
import com.wb.service.UserService;
import com.wb.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/")
public class IndexController {
    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = {"/", "/index"})
    public String index(){
        return "index";
    }

    @RequestMapping("getIndexDetail")//里面方法名
    @ResponseBody
    public Response getIndexDetail(Integer page, HttpServletRequest request) {
        Integer userId = userService.getUserIdFromRedis(request);
        Map<String, Object> map = userService.getIndexDetail(userId, page);

        return new Response(0, "", map);
    }

    @RequestMapping("/explore")
    public String explore() {
       return "explore";
    }
    @RequestMapping("/setting")
    public String setting(HttpServletRequest request, Model model){
        Integer userId = userService.getUserIdFromRedis(request);
        User user = userService.getProfileInfo(userId);
        model.addAttribute("user", user);
        return "editProfile";
    }
    @RequestMapping("/editProfile")
    public String editProfile(User user, HttpServletRequest request) {
        Integer userId = userService.getUserIdFromRedis(request);
        user.setUserId(userId);

        userService.updateProfile(user);
        return "redirect:/profile/" + userId;
    }

    @RequestMapping("/updatePassword")
    public String updatePassword(String password, String newpassword, HttpServletRequest request, Model model) {
        Integer userId = userService.getUserIdFromRedis(request);
        Map<String, Object> map = userService.updatePassword(userId, password, newpassword);
        if (map.get("error") != null) {
            model.addAttribute("error",map.get("error"));
            return "forward:/setting";
        }
        return "redirect:/logout";
    }
}
