package com.wb.mapper;

import com.wb.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    int selectEmailCount(String email);

    void insertUser(User user);

    //这里需要包装类型
    Integer selectUserIdByEmailAndPassword(@Param("email") String email, @Param("password") String password);
    Integer selectActivationStateByUserId(Integer userId);

    User selectUserInfoByUserId(@Param("userId") Integer userId);

    User selectProfileInfoByUserId(@Param("userId") Integer userId);
}
