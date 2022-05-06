package cn.sdu.judge.bean;

import java.util.HashMap;
import java.util.Map;

public class JudgeResult {
    private String taskId;
    private int problemId;
    private int checkpointSize;
    private Map<Integer, JudgeLimit> details;
    private Map<Integer, String> errors;

    public JudgeResult() {
        details = new HashMap<>();
        errors = new HashMap<>();
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public int getCheckpointSize() {
        return checkpointSize;
    }

    public void setCheckpointSize(int checkpointSize) {
        this.checkpointSize = checkpointSize;
    }

    public Map<Integer, JudgeLimit> getDetails() {
        return details;
    }

    public void setDetails(Map<Integer, JudgeLimit> details) {
        this.details = details;
    }

    public Map<Integer, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<Integer, String> errors) {
        this.errors = errors;
    }
}
