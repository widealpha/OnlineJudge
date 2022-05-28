package cn.sdu.oj.service;

import cn.sdu.oj.dao.UserInfoMapper;
import cn.sdu.oj.dao.UserMapper;
import cn.sdu.oj.domain.po.UserInfo;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.util.JwtUtil;
import cn.sdu.oj.util.RedisUtil;
import cn.sdu.oj.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    private JwtUtil jwtUtil;
    @Value("${spring.mail.username}")
    String emailFrom;

    @Transactional
    public ResultEntity<Boolean> register(String username, String password, String email) {
        if (username.isEmpty() || password.isEmpty()) {
            return ResultEntity.data(StatusCode.PARAM_EMPTY, false);
        } else if (!StringUtil.allLetter(username)) {
            return ResultEntity.data(StatusCode.PARAM_NOT_VALID, false);
        }
        User user = new User(username, passwordEncoder.encode(password));
        try {
            userMapper.insert(user);
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(user.getId());
            userInfo.setEmail(email);
            userInfoMapper.insertUserInfo(userInfo);
            return ResultEntity.data(StatusCode.SUCCESS, true);
        } catch (DuplicateKeyException e) {
            return ResultEntity.data(StatusCode.USER_ACCOUNT_ALREADY_EXIST, false);
        }
    }

    @Transactional
    public ResultEntity<Boolean> emailRegister(String email, String code, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return ResultEntity.data(StatusCode.PARAM_EMPTY, false);
        }
        if (!validateRegisterCode(email, code)) {
            return ResultEntity.data(StatusCode.VALIDATE_ERROR, false);
        } else {
            String username = UUID.randomUUID().toString().replace("-", "");
            ResultEntity<Boolean> result = register(username, password, email);
            if (result.getData()) {
                redisUtil.delete("register:" + email);
            }
            return ResultEntity.data(StatusCode.SUCCESS, true);
        }
    }

    public ResultEntity<Boolean> logout(User user) {
        String redisKey = "logout:" + user.getId();
        redisUtil.setEx(redisKey, String.valueOf(System.currentTimeMillis()), jwtUtil.EXPIRATION, TimeUnit.SECONDS);
        return ResultEntity.data(StatusCode.SUCCESS, true);
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

    public ResultEntity<Boolean> changePassword(int userId, String oldPassword, String newPassword) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ResultEntity.data(StatusCode.USER_ACCOUNT_NOT_EXIST, false);
        }
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            User newUser = new User(user.getUsername(), passwordEncoder.encode(newPassword));
            return ResultEntity.data(userMapper.updateByPrimaryKey(user));
        } else {
            return ResultEntity.data(StatusCode.USER_CREDENTIALS_ERROR, false);
        }
    }

    public ResultEntity<List<User>> generateCompetitionUserList(int problemSetId, int size) {
        List<User> users = new ArrayList<>();
        Random random = new Random(problemSetId);
        for (int i = 0; i < size; i++) {
            String username = "js-" + problemSetId + "-" + (10000 + random.nextInt(9999));
            User user = new User(username, passwordEncoder.encode("123456"));
            userMapper.insert(user);
            users.add(user);
        }
        return ResultEntity.data(users);
    }
}
