package cn.sdu.oj.domain.dto;

import cn.sdu.oj.domain.po.SolveRecord;

import java.util.Date;

public class SolveResultDto {
    private String taskId;
    private int problemId;
    private String language;
    private String error;

    private String output;
    /**
     * 单位ms
     */
    private int cpuTime;
    /**
     * 单位KB
     */
    private int memory;

    /**
     * 总测试点的数量
     */
    private int checkpointSize;
    /**
     * 已通过测试点的数量
     */
    private int totalCorrect;

    private int statusCode;

    private String code;

    private Date date;

    public static SolveResultDto fromSolveRecord(SolveRecord solveRecord) {
        SolveResultDto solveResultDto = new SolveResultDto();
        solveResultDto.taskId = "" + solveRecord.getId();
        solveResultDto.problemId = solveRecord.getProblemId();
        solveResultDto.language = solveRecord.getLanguage();
        solveResultDto.cpuTime = solveRecord.getCpuTime();
        solveResultDto.memory = solveRecord.getMemory();
        solveResultDto.checkpointSize = solveRecord.getCheckpointSize();
        solveResultDto.totalCorrect = solveRecord.getTotalCorrect();
        solveResultDto.statusCode = solveRecord.getStatus();
        solveResultDto.error = solveRecord.getError();
        solveResultDto.date = solveRecord.getCreateTime();
        solveResultDto.code = solveRecord.getCode();
        solveResultDto.output = solveRecord.getOutput();
        return solveResultDto;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getProblemId() {
        return problemId;
    }

    public String getLanguage() {
        return language;
    }

    public String getError() {
        return error;
    }

    public int getCpuTime() {
        return cpuTime;
    }

    public int getMemory() {
        return memory;
    }

    public int getCheckpointSize() {
        return checkpointSize;
    }

    public int getTotalCorrect() {
        return totalCorrect;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setCpuTime(int cpuTime) {
        this.cpuTime = cpuTime;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public void setCheckpointSize(int checkpointSize) {
        this.checkpointSize = checkpointSize;
    }

    public void setTotalCorrect(int totalCorrect) {
        this.totalCorrect = totalCorrect;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
