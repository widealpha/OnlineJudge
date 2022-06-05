package cn.sdu.oj.domain.bo;

import com.alibaba.excel.annotation.ExcelProperty;

public class StudentExcelInfo {
    @ExcelProperty("学号")
    private String studentId;
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("性别")
    private String gender;
    /**
     * 院系
     */
    @ExcelProperty("上课院系")
    private String department;
    /**
     * 专业
     */
    @ExcelProperty("专业")
    private String major;
    /**
     * 班级名称
     */
    @ExcelProperty("班级")
    private String className;


    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
