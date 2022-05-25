package cn.sdu.oj.domain.po;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 异步题目,编程题目
 */
public class AsyncProblem {
    private Integer id;
    private String name;
    private String description;
    private String example;
    private int difficulty;
    private int creator;
    int status;

    /**
     * 时间限制,单位ms
     */
    private int timeLimit;
    /**
     * 占用内存 单位为KB
     */
    private int memoryLimit;
    /**
     * 代码文本长度,单位为Byte
     */
    private int codeLengthLimit;
    private Date createTime;
    private Date modifiedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public int getCodeLengthLimit() {
        return codeLengthLimit;
    }

    public void setCodeLengthLimit(int codeLengthLimit) {
        this.codeLengthLimit = codeLengthLimit;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
