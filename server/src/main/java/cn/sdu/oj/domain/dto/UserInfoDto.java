package cn.sdu.oj.domain.dto;

import cn.sdu.oj.domain.po.UserInfo;

import java.util.List;

public class UserInfoDto {

    public UserInfoDto() {
    }

    public UserInfoDto(UserInfo userInfo) {
        this.userId = userInfo.getUserId();
        this.name = userInfo.getName();
        this.avatar = userInfo.getAvatar();
        this.nickname = userInfo.getNickname();
        this.studentId = userInfo.getStudentId();
        this.email = userInfo.getEmail();
    }


    private Integer userId;

    //姓名
    private String name;

    //头像
    private String avatar;

    //昵称
    private String nickname;

    //学号
    private String studentId;

    //邮箱
    private String email;

    private List<String> roles;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
