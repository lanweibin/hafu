package com.wb.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.wb.model.*;
import com.wb.service.*;
import com.wb.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private TopicService topicService;


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

    //侧面我关注的问题
    @RequestMapping("/profileFollowQuestion/{userId}")
    public String profileFollowQuestion(@PathVariable Integer userId, HttpServletRequest request, Model model) {
        Integer localUserId = userService.getUserIdFromRedis(request);
        //获取用户信息
        Map<String, Object> map = userService.profile(userId, localUserId);
        //获取问题列表
        List<Question> questionList = questionService.listFollowQuestion(userId);
        map.put("questionList", questionList);
        model.addAllAttributes(map);
        return "profileFollowQuestion";
    }

    //关注里的收藏
    @RequestMapping("/profileCollection/{userId}")
    public String profileCollection(@PathVariable Integer userId, HttpServletRequest request, Model model) {
        Integer localUserId = userService.getUserIdFromRedis(request);
        //获取用户信息
        Map<String, Object> map = userService.profile(userId,localUserId);
        //获取收藏夹列表
        List<Collection> collectionList = collectionService.listCreatingCollection(userId);
        map.put("collectionList", collectionList);

        model.addAllAttributes(map);
        return "profileCollection";
    }

    //关注的人
    @RequestMapping("/profileFollowPeople/{userId}")
    public String profileFollowPeople(@PathVariable Integer userId, HttpServletRequest request, Model model) {
        Integer localUserId = userService.getUserIdFromRedis(request);
        //获取用户信息
        Map<String, Object> map = userService.profile(userId, localUserId);

        //关注者列表
        List<User> userList = userService.listFollowingUser(userId);
        map.put("userList", userList);

        model.addAllAttributes(map);
        return "profileFollowPeople";

    }

    //被关注用户列表
    @RequestMapping("/profileFollowedPeople/{userId}")
    public String profileFollowedPeople(@PathVariable Integer userId, HttpServletRequest request, Model model) {
        Integer localUserId = userService.getUserIdFromRedis(request);
        Map<String, Object> map = userService.profile(userId, localUserId);

        //获取被关注者列表
        List<User> userList = userService.listFollowedUser(userId);
        map.put("userList", userList);

        model.addAllAttributes(map);
        return "profileFollowedPeople";
    }

    //关注的收藏夹
    @RequestMapping("/profileFollowCollection/{userId}")
    public String profileFollowCollection(@PathVariable Integer userId, HttpServletRequest request, Model model) {
        Integer localUserId = userService.getUserIdFromRedis(request);
        Map<String, Object> map = userService.profile(userId, localUserId);
        //获取收藏夹列表
        List<Collection> collectionList = collectionService.listFollowingCollection(userId);
        map.put("collectionList", collectionList);

        model.addAllAttributes(map);
        return "profileFollowCollection";
    }

    @RequestMapping("/profileFollowTopic/{userId}")
    public String profileFollowTopic(@PathVariable Integer userId, HttpServletRequest request, Model model) {
        Integer localUserId = userService.getUserIdFromRedis(request);
        // 获取用户信息
        Map<String, Object> map = userService.profile(userId, localUserId);
        //获取话题列表
        List<Topic> topicList = topicService.listFollowingTopic(userId);
        map.put("topicList", topicList);
        model.addAllAttributes(map);

        return "profileFollowTopic";
    }

}
