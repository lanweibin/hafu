package com.wb.controller;

import com.wb.model.Answer;
import com.wb.model.PageBean;
import com.wb.model.Question;
import com.wb.service.AnswerService;
import com.wb.service.QuestionService;
import com.wb.service.UserService;
import com.wb.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;


    @RequestMapping("/toLogin")
    public String toLogin() {
        return "toLogin";
    }

    @RequestMapping("/register")
    @ResponseBody
    public Response register(String username, String email, String password) {
        Map<String, String> map = userService.register(username, email, password);
        if (map.get("ok") != null){
            return new Response(0, "系统已经向你的邮箱发送了一封邮件哦，验证后就可以登录啦~");
        }else {
            return new Response(1, "error", map);
        }
    }

    @RequestMapping("/login")
    @ResponseBody
    public Response login(String email, String password, HttpServletResponse response){
        Map<String, Object > map = userService.login(email, password, response);
        if (map.get("error") == null){
            return new Response(0, "", map);
        }else {
            return new Response(1, map.get("error").toString());
        }
    }
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request, response);
        return "redirect:/toLogin";
    }

    @RequestMapping("/profileQuestion/{userId}")
    public String profileQuestion(@PathVariable Integer userId, Integer page, HttpServletRequest request, Model model) {
        Integer localUserId = userService.getUserIdFromRedis(request);
        //获取用户信息
        Map<String, Object> map = userService.profile(userId,localUserId);
        //获取回答列表
        PageBean<Question> pageBean = questionService.listQuestionByUserId(userId, page);
        map.put("pageBean",pageBean);

        model.addAllAttributes(map);
        return "profileQuestion";
    }

    //我回答的问题
    @RequestMapping("/profile/{userId}")
    public String profile(@PathVariable Integer userId, Integer page, HttpServletRequest request, Model model) {
        Integer localUserId = userService.getUserIdFromRedis(request);
        // 获取用户信息
        Map<String, Object> map = userService.profile(userId, localUserId);
        // 获取回答列表
        PageBean<Answer> pageBean = answerService.listAnswerByUserId(userId, page);
        map.put("pageBean", pageBean);

        model.addAllAttributes(map);
        return "profileAnswer";
    }

}
