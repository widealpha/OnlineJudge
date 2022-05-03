package cn.sdu.judge.bean;

import cn.sdu.judge.entity.StatusCode;

import java.sql.Timestamp;

public class TaskRecord {
    private long id;
    private String taskId;
    private String language;
    private String code;
    private int codeLength;
    private boolean specialJudge;

    private String specialJudgeLanguage;
    private String compileInfo;
    private String runInfo;
    private Timestamp createTime;
    private Timestamp modifiedTime;
    /**
     * 运行正确 1
     * {@link StatusCode#COMMON_FAIL} 其他错误 -1
     * {@link StatusCode#LANGUAGE_NOT_SUPPORT} 编程语言不支持 -2
     * {@link StatusCode#PROBLEM_NOT_EXIST} 编程语言不支持 -3
     * {@link StatusCode#COMPILE_ERROR} 编译错误 -4
     * {@link StatusCode#RUN_ERROR} 运行时错误 -5
     * {@link StatusCode#CHECKPOINT_ERROR} 测试点测试错误 -6
     */
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSpecialJudge() {
        return specialJudge;
    }

    public void setSpecialJudge(boolean specialJudge) {
        this.specialJudge = specialJudge;
    }

    public String getSpecialJudgeLanguage() {
        return specialJudgeLanguage;
    }

    public void setSpecialJudgeLanguage(String specialJudgeLanguage) {
        this.specialJudgeLanguage = specialJudgeLanguage;
    }

    public String getCompileInfo() {
        return compileInfo;
    }

    public void setCompileInfo(String compileInfo) {
        this.compileInfo = compileInfo;
    }

    public String getRunInfo() {
        return runInfo;
    }

    public void setRunInfo(String runInfo) {
        this.runInfo = runInfo;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Timestamp modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
