package com.wb.service;

import com.sun.org.apache.regexp.internal.RE;
import com.wb.mapper.AnswerMapper;
import com.wb.mapper.CollectionMapper;
import com.wb.model.Answer;
import com.wb.model.Collection;
import com.wb.util.MyUtil;
import com.wb.util.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


import java.util.*;

@Service
public class CollectionService {
    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private JedisPool jedisPool;

    //列出直接喜欢的收藏夹列表
    public List<Collection> listCreatingCollection(Integer userId) {
        //获取收藏夹列表
        List<Collection> list = collectionMapper.listCreatingCollectionByUserId(userId);
        // 获取每个列表中的答案条数
        Jedis jedis = jedisPool.getResource();
        for (Collection collection : list) {
            Long answerCount = jedis.zcard(collection.getCollectionId() + RedisKey.COLLECT);
            collection.setAnswerCount(Integer.parseInt(answerCount+ ""));
            System.out.println(answerCount);
        }

        jedisPool.returnResource(jedis);
        return list;
    }

    // 获取收藏夹内容详情
    public Map<String,Object> getCollectionContent(Integer collectionId, Integer localUserId) {
        Map<String, Object> map = new HashMap<>();
        Jedis jedis = jedisPool.getResource();

        //获取收藏夹里答案的id列表
        Set<String> idSet = jedis.zrange(collectionId + RedisKey.COLLECT, 0, -1);
        List<Integer> idList = MyUtil.StringSetToIntegerList(idSet);
        if (idList.size() > 0) {
            List<Answer> answerList = answerMapper.listAnswerByAnswerId(idList);
            map.put("answerList", answerList);
        }

        //判断是不是自己的的收藏夹信息
        Integer userId = collectionMapper.selectUserIdByCollectionId(collectionId);
        if (userId.equals(localUserId)) {
            map.put("isSelf", true);
        }

        //获取收藏夹信息
        Collection collection = collectionMapper.selectCollectionByCollectionId(collectionId);
        Long answerCount = jedis.zcard(collectionId + RedisKey.COLLECT);
        collection.setAnswerCount(Integer.parseInt(answerCount + ""));
        map.put("collection", collection);

        jedisPool.returnResource(jedis);
        return map;
    }

    //创建收藏夹
    public void addCollection(Collection collection, Integer userId) {
        collection.setUserId(userId);
        collection.setCreateTime(new Date().getTime());
        collection.setUpdateTime(new Date().getTime());
        collectionMapper.insertCollection(collection);
    }

    // 判断某人是否关注了某收藏夹
    public boolean judgePeopleFollowCollection(Integer userId, Integer collectionId) {
        Jedis jedis = jedisPool.getResource();
        Long rank = jedis.zrank(userId + RedisKey.FOLLOW_COLLECTION, String.valueOf(collectionId));
        jedisPool.returnResource(jedis);

        System.out.println(rank);
        return rank == null ? false : true;
    }
}
