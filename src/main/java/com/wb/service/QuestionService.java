package com.wb.service;

import com.wb.mapper.QuestionMapper;
import com.wb.model.Pagebean;
import com.wb.model.Question;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    public Pagebean<Question> listQuestionByUserId(Integer userId, Integer curPage) {
        //当请求页数为空时
        curPage = curPage == null ? 1 : curPage;
        //每页记录数，从哪里开始
        int limit = 8;
        int offset = (curPage - 1) * 8;
        //获取总记录数,总页数
        int allCount = questionMapper.selectQuestionCountByUserId(userId);
        int allPage = 0;
        if (allPage <= limit) {
            allPage = 1;
        }else if (allCount / limit == 0){
            allPage = allCount /limit;
        }else {
            allPage = allCount / limit + 1;
        }

        //构造查询Map
        Map<String, Object> map = new HashMap<>();
        map.put("offset", offset);
        map.put("limit", limit);
        map.put("userId", userId);
        //得到某业数据列表
        List<Question> questionList = questionMapper.listQuestionByUserId(map);

        //构造PageBean
        Pagebean<Question> pagebean = new Pagebean<>(allPage,curPage);
        pagebean.setList(questionList);

        return pagebean;
    }
}
