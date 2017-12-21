package com.wb.controller;

import com.wb.model.Collection;
import com.wb.service.CollectionService;
import com.wb.service.QuestionService;
import com.wb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;
    @Autowired
    private CollectionService collectionService;

    @RequestMapping("/questionDetail")
    public String questionDetail(@PathVariable Integer questionId, HttpServletRequest request, Model model) {
        Integer userId = userService.getUserIdFromRedis(request);

        Map<String, Object> questionDetail = questionService.getQuestionDetail(questionId,userId);

        //获取收藏夹信息
        List<Collection> collectionList = collectionService.listCreatingCollection(userId);
        questionDetail.put("collectionList", collectionList);

        model.addAllAttributes(questionDetail);
        return "questionDetail";
    }
}
