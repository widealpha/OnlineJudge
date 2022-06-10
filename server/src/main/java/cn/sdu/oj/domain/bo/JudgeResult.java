package cn.sdu.oj.domain.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JudgeResult {
    private String taskId;
    private int problemId;
    private int checkPointSize;
    private Map<String, JudgeLimit> details;
    private Map<String, String> errors;

    private String output;

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

    public int getCheckPointSize() {
        return checkPointSize;
    }

    public void setCheckPointSize(int checkPointSize) {
        this.checkPointSize = checkPointSize;
    }

    public Map<String, JudgeLimit> getDetails() {
        return details;
    }

    public void setDetails(Map<String, JudgeLimit> details) {
        this.details = details;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
