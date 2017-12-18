package com.wb.controller;

import com.wb.model.Collection;
import com.wb.service.CollectionService;
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
public class CollectionController {
    @Autowired
    private UserService userService;

    @Autowired
    private CollectionService collectionService;

    @RequestMapping("/collections")
    public String collections(){
        return "collectionList";
    }

    @RequestMapping("/listCreatingCollection")
    @ResponseBody
    public Response listCreatingCollection(HttpServletRequest request) {
        Integer userId = userService.getUserIdFromRedis(request);
        List<Collection> collectionList = collectionService.listCreatingCollection(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("collectionList", collectionList);
        return new Response(0, "", map);
    }

    @RequestMapping("/collection/{collectionId}")
    public String collection(@PathVariable Integer collectionId, HttpServletRequest request, Model model) {
        // 当前用户id
        Integer localUserId = userService.getUserIdFromRedis(request);
        // 收藏夹内的答案列表信息
        Map<String, Object> map = collectionService.getCollectionContent(collectionId, localUserId);

        //获取当前用户收藏夹列表
        List<Collection> collectionList = collectionService.listCreatingCollection(localUserId);

        map.put("collectionList",collectionList);
        model.addAllAttributes(map);
        return "collectionContent";
    }
}
