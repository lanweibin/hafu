package com.wb.service;

import com.wb.async.MailTask;
import com.wb.mapper.UserMapper;
import com.wb.model.User;
import com.wb.util.MyConstant;
import com.wb.util.MyUtil;
import com.wb.util.RedisKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSender javaMailSender;//发送邮件用的
    //TaskExecutor是一个spring的线程池技术
    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private JedisPool jedisPool;

    //注册
    public Map<String,String> register(String username, String email, String password) {
        Map<String, String> map  = new HashMap<>();
        //验证邮箱格式
        Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$");
        Matcher m = p.matcher(email);
        if (!m.matches()) {
            map.put("regi-email-error", "请输入正确邮箱");
            return map;
        }

        //验证用户名长度
        if (StringUtils.isEmpty(username) || username.length() > 10){
            map.put("regi-username-error", "用户名长度需在1-10个字符");
            return map;
        }

        //验证密码长度
        p = Pattern.compile("^\\w{6,20}$");
        m = p.matcher(password);
        if (!m.matches()){
            map.put("regi-password-error", "密码长度需在6-20个字符");
        }

        int emailCount = userMapper.selectEmailCount(email);
        if (emailCount > 0){
            map.put("regi-email-error", "该邮箱已注册");
            return map;
        }

        User user= new User();
        user.setEmail(email);
        user.setPassword(MyUtil.md5(password));
        //user设置未激活
        String activateCode = MyUtil.createRandomCode();
        user.setActivationCode(activateCode);
        user.setJoinTime(new Date().getTime());

        user.setUsername(username);
        user.setAvatarUrl(MyConstant.QINIU_IMAGE_URL + "head.jpg");

        //发送邮件
        taskExecutor.execute(new MailTask(activateCode, user.getEmail(), javaMailSender, 1));

        //向数据库中插入数据
        userMapper.insertUser(user);

        // 设置默认关注用户
        Jedis jedis = jedisPool.getResource();
        jedis.zadd(user.getUserId() + RedisKey.FOLLOW_PEOPLE, new Date().getTime(), String.valueOf(3));
        jedis.zadd(3 + RedisKey.FOLLOWED_PEOPLE, new Date().getTime(), String.valueOf(user.getUserId()));
        jedis.zadd(user.getUserId() + RedisKey.FOLLOW_PEOPLE, new Date().getTime(), String.valueOf(4));
        jedis.zadd(4 + RedisKey.FOLLOWED_PEOPLE, new Date().getTime(), String.valueOf(user.getUserId()));
        jedisPool.returnResource(jedis);
        map.put("ok", "注册完成");
        return map;
    }

    //登入
    public Map<String,String> login(String email, String password, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        //验证用户名和密码是否正确
        Integer userId = userMapper.selectUserIdByEmailAndPassword(email, MyUtil.md5(password))
    }
}
