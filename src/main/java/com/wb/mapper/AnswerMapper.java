package com.wb.mapper;

import com.wb.model.Answer;

import java.util.List;
import java.util.Map;

public interface AnswerMapper {
    List<Answer> listAnswerByUserIdList(Map<String, Object> map);

    List<Answer> listAnswerByAnswerId(List<Integer> idList);
}
