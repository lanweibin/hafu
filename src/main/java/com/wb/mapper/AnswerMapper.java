package com.wb.mapper;

import com.wb.model.Answer;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AnswerMapper {
    List<Answer> listAnswerByUserIdList(Map<String, Object> map);

    List<Answer> listAnswerByAnswerId(List<Integer> idList);

    int selectAnswerCountByUserId(@Param("userId") Integer userId);

    List<Answer> listAnswerByUserId(Map<String, Object> map);

    int listAnswerCountByQuestionId(List<Integer> questionIdList);

    List<Answer> listGoodAnswerByQuestionId(Map<String, Object> map);

    List<Answer> listAnswerByCreateTime(@Param("createTime") long createTime);

    List<Answer> selectAnswerByQuestionId(@Param("questionId") Integer questionId);
}
