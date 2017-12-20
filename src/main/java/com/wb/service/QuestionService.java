package com.wb.service;

import com.wb.mapper.AnswerMapper;
import com.wb.mapper.QuestionMapper;
import com.wb.model.PageBean;
import com.wb.model.Question;
import com.wb.util.MyUtil;
import com.wb.util.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private JedisPool jedisPool;

    public PageBean<Question> listQuestionByUserId(Integer userId, Integer curPage) {
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
        PageBean<Question> pageBean = new PageBean<>(allPage,curPage);
        pageBean.setList(questionList);

        return pageBean;
    }

    // 列出所关注的问题
    public List<Question> listFollowQuestion(Integer userId) {
        Jedis jedis = jedisPool.getResource();
        // 所关注的问题的id集合
        Set<String> idSet = jedis.zrange(userId + RedisKey.FOLLOW_QUESTION, 0, -1);
        List<Integer> idList = MyUtil.StringSetToIntegerList(idSet);

        List<Question> list = new ArrayList<>();
        if (idList.size() > 0) {
            list = questionMapper.listQuestionByQuestionId(idList);
            for (Question question : list) {
                //设置回答数目
                int answerCount = answerMapper.selectAnswerCountByUserId(question.getQuestionId());
                question.setAnswerCount(answerCount);
                Long followedCount = jedis.zcard(question.getQuestionId() + RedisKey.FOLLOWED_QUESTION);
                question.setFollowedCount(Integer.parseInt(followedCount + ""));
            }
        }

        jedisPool.returnResource(jedis);
        return list;
    }
}
