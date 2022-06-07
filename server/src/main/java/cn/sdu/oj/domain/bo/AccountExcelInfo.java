package cn.sdu.oj.domain.bo;

import com.alibaba.excel.annotation.ExcelProperty;

public class AccountExcelInfo {
    @ExcelProperty("用户名")
    private String username;
    @ExcelProperty("密码")
    private String password;

    public AccountExcelInfo() {
    }

    public AccountExcelInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
