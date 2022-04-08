package cn.sdu.oj.service;

import cn.sdu.oj.dao.UserInfoMapper;
import cn.sdu.oj.dao.UserMapper;
import cn.sdu.oj.domain.po.UserInfo;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MailSender mailSender;
    @Value("${spring.mail.username}")
    String emailFrom;

    @Transactional
    public StatusCode register(String username, String password, String email) {
        if (username.isEmpty() || password.isEmpty()) {
            return StatusCode.PARAM_EMPTY;
        }
        User user = new User(username, passwordEncoder.encode(password));
        try {
            userMapper.insert(user);
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(user.getId());
            userInfo.setEmail(email);
            userInfoMapper.insertUserInfo(userInfo);
            return StatusCode.SUCCESS;
        } catch (DuplicateKeyException e) {
            return StatusCode.USER_ACCOUNT_ALREADY_EXIST;
        }
    }

    @Transactional
    public StatusCode emailRegister(String email, String code, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return StatusCode.PARAM_EMPTY;
        }
        String username = UUID.randomUUID().toString();
        if (!validateRegisterCode(email, code)) {
            return StatusCode.VALIDATE_ERROR;
        } else {
            redisUtil.delete("register:" + email);
            return register(username, password, email);
        }
    }

    @Async
    public void sendRegisterValidateCode(String email) {
        String redisKey = "register:" + email;
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(emailFrom);
        message.setTo(email);
        message.setSubject("感谢您注册学生能力提升平台");
        String code;
        if (redisUtil.get(redisKey) != null) {
            code = redisUtil.get(redisKey);
        } else {
            code = String.valueOf(new Random().nextInt(899999) + 100000);
            redisUtil.setEx(redisKey, code, 30, TimeUnit.MINUTES);
        }
        message.setText("您的验证码为: " + code + "\n验证码30分钟内有效");
        mailSender.send(message);
    }

    public boolean validateRegisterCode(String email, String code) {
        if (code == null) {
            return false;
        }
        String redisKey = "register:" + email;
        return code.equals(redisUtil.get(redisKey));
    }
}
