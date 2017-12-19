package com.wb.service;

import com.wb.mapper.AnswerMapper;
import com.wb.model.Answer;
import com.wb.model.PageBean;
import com.wb.util.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnswerService {
    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private JedisPool jedisPool;

    public PageBean<Answer> listAnswerByUserId(Integer userId, Integer curPage) {
        //请求页数为空时
        curPage = curPage == null ? 1 : curPage;

        int limit = 8;
        int offset = (curPage - 1) * 8;

        int allCount = answerMapper.selectAnswerCountByUserId(userId);//allCount = 15
        int allPage = 0;
        if (allCount <= limit) {
            allPage = 1;
        }else if (allCount / limit == 0) {
            allPage = allCount / limit;
        }else {
            allPage = allCount / limit + 1;//allpage = 2
        }

        //构造查询Map
        Map<String, Object> map = new HashMap<>();
        map.put("offset", offset);
        map.put("limit", limit);
        map.put("userId", userId);
        // 得到某页数据列表
        List<Answer> answerList = answerMapper.listAnswerByUserId(map);//answerlist.size=8 map=3

        //获取答案被点赞次数
        Jedis jedis = jedisPool.getResource();
        for (Answer answer :answerList) {
            Long likedCount = jedis.zcard(answer.getAnswerId() + RedisKey.LIKE_ANSWER);
            answer.setLikedCount(Integer.parseInt(likedCount + ""));
        }

        //构造PageBean
        PageBean<Answer> pageBean = new PageBean<>(allPage, curPage);
        pageBean.setList(answerList);

        return pageBean;
    }
}
