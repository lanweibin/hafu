package com.wb.service;

import com.alibaba.fastjson.JSON;
import com.wb.mapper.AnswerMapper;
import com.wb.mapper.CommentMapper;
import com.wb.mapper.QuestionMapper;
import com.wb.mapper.UserMapper;
import com.wb.model.*;
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
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;

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

    public Map<String,Object> getQuestionDetail(Integer questionId, Integer userId) {
        Map<String, Object> map = new HashMap<>();
        //获取问题信息
        Question question = questionMapper.selectQuestionByQuestionId(questionId);
        if (question == null) {
            throw new RuntimeException("该问题Id不存在");
        }
        //获取该问题被浏览次数
        Jedis jedis = jedisPool.getResource();
        jedis.zincrby(RedisKey.QUESTION_SCANED_COUNT, 1, questionId+"");
        question.setScanedCount((int) jedis.zscore(RedisKey.QUESTION_SCANED_COUNT, questionId + "").doubleValue());
        //获取该问题被关注的人数
        Long followedCount = jedis.zcard(questionId + RedisKey.FOLLOWED_QUESTION);
        question.setFollowedCount(Integer.parseInt(followedCount + ""));
        // 获取10个关注该问题的人
        Set<String> userIdSet = jedis.zrange(questionId + RedisKey.FOLLOWED_QUESTION, 0, 9);
        List<Integer> userIdList = MyUtil.StringSetToIntegerList(userIdSet);
        List<User> followedUserList = new ArrayList<>();
        if ( userIdList.size() > 0) {
            followedUserList = userMapper.listUserInfoByUserId(userIdList);
        }

        // 获取5个该话题下的问题 relatedQuestionList
        List<Question> relatedQuestionList = questionMapper.listRelatedQuestion(questionId);
        System.out.println("listRelatedQuestion:" + relatedQuestionList);

        //获取用户提问信息
        User askUser = userMapper.selectUserInfoByUserId(question.getUserId());
        question.setUser(askUser);
        //获取问题评论列表
        List<QuestionComment> questionCommentList = commentMapper.listQuestionCommentByQuestionId(questionId);
        //为每条问题评论绑定用户信息
        for (QuestionComment comment : questionCommentList) {
            User commentUser = userMapper.selectUserInfoByUserId(comment.getUserId());
            comment.setUser(commentUser);
            //判断用户是否赞过该评论
            Long rank = jedis.zrank(userId + RedisKey.LIKE_QUESTION_COMMENT, comment.getQuestionCommentId() + "");
            comment.setLikeState(rank == null ? "false" : "true");
            //获取该评论被点赞次数
            Long likedCount = jedis.zcard(comment.getQuestionCommentId() + RedisKey.LIKE_QUESTION_COMMENT);
            comment.setLikedCount(Integer.valueOf(likedCount + ""));
        }
        question.setQuestionCommentList(questionCommentList);

        //获取答案列表
        List<Answer> answerList = answerMapper.selectAnswerByQuestionId(questionId);
        for (Answer answer : answerList) {
            User answerUser = userMapper.selectUserInfoByUserId(answer.getUserId());
            answer.setUser(answerUser);
            //获取  答案评论 列表
            List<AnswerComment> answerCommentList = commentMapper.listAnswerCommentByAnswerId(answer.getAnswerId());
            for (AnswerComment comment : answerCommentList) {
                //gei 评论绑定用户信息
                User commentUser = userMapper.selectUserInfoByUserId(comment.getUserId());
                comment.setUser(commentUser);
                //判断用户是否赞过该条评论
                Long rank = jedis.zrank(userId +RedisKey.LIKE_ANSWER_COMMENT, comment.getAnswerCommentId() + "");
                comment.setLikeState(rank == null ? "false" : "true");
                //获取评论被点赞次数
                Long likedCount = jedis.zcard(comment.getAnswerCommentId() + RedisKey.LIKE_ANSWER_COMMENT);
                comment.setLikedCount(Integer.valueOf(likedCount + ""));
            }
            answer.setAnswerCommentList(answerCommentList);

            // 获取用户点赞状态
            Long rank = jedis.zrank(answer.getAnswerId() + RedisKey.LIKED_ANSWER, String.valueOf(userId));
            answer.setLikeState(rank == null ? "false" : "true");
            // 获取该回答被点赞次数
            Long likedCount = jedis.zcard(answer.getAnswerId() + RedisKey.LIKED_ANSWER);
            answer.setLikedCount(Integer.valueOf(likedCount + ""));
        }

        // 获取话题信息
        Map<Integer, String> topicMap = (Map<Integer, String>) JSON.parse(question.getTopicKvList());

        map.put("topicMap", topicMap);
        map.put("question", question);
        map.put("answerList", answerList);
        map.put("followedUserList", followedUserList);
        map.put("relatedQuestionList", relatedQuestionList);
        jedisPool.returnResource(jedis);
        return map;
    }

    public List<Question> listQuestionByPage(Integer curPage) {
        curPage = curPage == null ? 1 : curPage;
        int limit = 3;
        int offset = (curPage - 1) * limit;

        Jedis jedis = jedisPool.getResource();

        Set<String> idSet = jedis.zrange(RedisKey.QUESTION_SCANED_COUNT, offset, offset + limit -1);
        List<Integer> idList = MyUtil.StringSetToIntegerList(idSet);
        System.out.println(idList);
        List<Question> questionList = new ArrayList<>();
        if (idList.size() > 0) {
            questionList = questionMapper.listQuestionByQuestionId(idList);

            for (Question question : questionList) {
                question.setAnswerCount(answerMapper.selectAnswerCountByUserId(question.getQuestionId()));
                question.setFollowedCount(Integer.parseInt(jedis.zcard(question.getQuestionId() + RedisKey.FOLLOWED_QUESTION) + ""));
            }
        }
        jedisPool.returnResource(jedis);
        return questionList;
    }
}
