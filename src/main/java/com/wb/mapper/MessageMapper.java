package com.wb.mapper;

import com.wb.model.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper {
    List<Message> listMessageByUserId(@Param("userId") Integer userId);
}
