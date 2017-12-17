package com.wb.mapper;

import com.wb.model.User;

public interface UserMapper {
    int selectEmailCount(String email);

    void insertUser(User user);

    Integer selectUserIdByEmailAndPassword(String email, String s);
}
