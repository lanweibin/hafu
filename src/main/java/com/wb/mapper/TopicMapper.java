package com.wb.mapper;

import com.wb.model.Topic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TopicMapper {
    List<Topic> listHotTopic();

    List<Topic> listRootTopic();

    Topic selectTopicByTopicId(@Param("topicId") Integer topicId);

    List<Integer> selectQuestionIdByTopicId(Integer topicId);

    List<Topic> listTopicByParentId(@Param("parentTopicId") Integer parentTopicId);

    void updateFollowedCount(@Param("topicId") Integer topicId);

    void updateUnFollowedCount(Integer topicId);

    List<Topic> lisTopicByTopicName(@Param("topicName") String topicName);

    List<Topic> listTopicByTopicId(List<Integer> idList);
}
