package com.wb.controller;

import com.wb.service.UserService;
import com.wb.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/")
public class IndexController {
    @Autowired
    private UserService userService;

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
}
