package cn.sdu.oj.domain.dto;

import cn.sdu.oj.domain.po.UserInfo;

public class MinorUserInfoDto {
    private int userId;
    private String username;
    private String avatar;

    private String nickname;

    public MinorUserInfoDto() {
    }

    public MinorUserInfoDto(UserInfo userInfo, String username) {
        this.userId = userInfo.getUserId();
        this.username = username;
        this.avatar = userInfo.getAvatar();
        this.nickname = userInfo.getNickname();
    }



    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
