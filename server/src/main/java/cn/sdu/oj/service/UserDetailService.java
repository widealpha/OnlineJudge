package cn.sdu.oj.service;

import cn.sdu.oj.dao.RoleMapper;
import cn.sdu.oj.dao.UserInfoMapper;
import cn.sdu.oj.dao.UserMapper;
import cn.sdu.oj.domain.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailService implements org.springframework.security.core.userdetails.UserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Value("${spring.mail.username}")
    String emailFrom;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        //如果包含@表示是邮箱登录
        if (username.contains("@")) {
            Integer userId = userInfoMapper.selectUserIdByEmail(username);
            if (userId == null) {
                throw new UsernameNotFoundException("邮箱不存在");
            } else {
                user = userMapper.selectByPrimaryKey(userId);
            }
        } else {
            user = userMapper.selectByUsername(username);
        }
        if (user != null) {
            List<String> roles = roleMapper.selectRolesWithUserId(user.getId());
            List<GrantedAuthority> authorities = new ArrayList<>();
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
            //所有经过验证的用户均携带COMMON身份
            authorities.add(new SimpleGrantedAuthority("ROLE_COMMON"));
            User authorityUser = new User(username, user.getPassword(), authorities);
            authorityUser.setId(authorityUser.getId());
            return authorityUser;
        } else {
            throw new UsernameNotFoundException(username + "Not Found");
        }
    }

}
