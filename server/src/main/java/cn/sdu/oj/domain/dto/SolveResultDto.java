package cn.sdu.oj.domain.dto;

import cn.sdu.oj.domain.po.SolveRecord;

public class SolveResultDto {
    private String taskId;
    private int problemId;
    private String language;
    private String error;
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
}
