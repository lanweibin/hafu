package com.wb.mapper;

import com.wb.model.AnswerComment;
import com.wb.model.QuestionComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {
    int selectAnswerCommentCountByAnswerId(@Param("answerId") Integer answerId);

    List<QuestionComment> listQuestionCommentByQuestionId(@Param("questionId") Integer questionId);

    List<AnswerComment> listAnswerCommentByAnswerId(@Param("answerId") Integer answerId);
}
