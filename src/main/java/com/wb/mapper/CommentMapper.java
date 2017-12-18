package com.wb.mapper;

import org.apache.ibatis.annotations.Param;

public interface CommentMapper {
    int selectAnswerCommentCountByAnswerId(@Param("answerId") Integer answerId);
}
