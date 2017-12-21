package com.wb.controller;

import com.wb.model.Topic;
import com.wb.service.TopicService;
import com.wb.service.UserService;
import com.wb.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class TopicController {
    @Autowired
    private UserService userService;

    @Autowired
    private TopicService topicService;

    @RequestMapping("/topics")
    public String topics(Model model) {
        Map<String, Object> map = topicService.listAllTopic();
        model.addAllAttributes(map);
        return "topics";
    }

    @RequestMapping("/topicDetail/{topicId}")
    public String topicDetail(@PathVariable Integer topicId, Integer page, Boolean allQuesttion, Model model, HttpServletRequest request) {
        Integer userId = userService.getUserIdFromRedis(request);
        Map<String, Object> map = topicService.getTopicDetail(topicId,allQuesttion,page,userId);
        model.addAllAttributes(map);
        return "topicDetail";
    }

    @RequestMapping("/listTopicByParentTopicId")
    @ResponseBody
    public Response listTopicByParentTopicId(Integer parentTopicId) {
        List<Topic> list = topicService.listTopicByParentTopicId(parentTopicId);
        Map<String, Object> map = new HashMap<>();
        map.put("topicList",list);
        return new Response(0, "", map);
    }

    //关注功能
    @RequestMapping("/followTopic")
    @ResponseBody
    public Response followTopic(Integer topicId, HttpServletRequest request) {
        Integer userId = userService.getUserIdFromRedis(request);
        topicService.followTopic(userId, topicId);
        return new Response(0,"");
    }

    @RequestMapping("/unfollowTopic")
    @ResponseBody
    public Response unfollowTopic(Integer topicId, HttpServletRequest request) {
        Integer userId = userService.getUserIdFromRedis(request);
        topicService.unfollowTopic(userId, topicId);
        return  new Response(0, "");
    }

    @RequestMapping("/judgePeopleFollowTopic")
    @ResponseBody
    public Response judgePeopleFollowTopic(Integer topicId, HttpServletRequest request) {
        Integer userId = userService.getUserIdFromRedis(request);
        boolean status = topicService.judgePeopleFollowTopic(userId, topicId);

        return new Response(0, "", status);
    }
}
