package com.wb.service;

import com.wb.mapper.MessageMapper;
import com.wb.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    public Map<String,List<Message>> listMessage(Integer userId) {
        List<Message> messageList = messageMapper.listMessageByUserId(userId);
        Map<String, List<Message>> map = new HashMap<>();
        for (Message message : messageList) {
            String time = message.getMessageDate();
            if (map.get(time) == null) {
                map.put(time, new LinkedList<Message>());
                map.get(time).add(message);
            }else {
                map.get(time).add(message);
            }
        }
        return map;
    }
}
