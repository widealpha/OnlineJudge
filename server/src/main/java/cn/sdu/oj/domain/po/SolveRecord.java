package cn.sdu.oj.domain.po;

public class SolveRecord {
    private int id;
    private int userId;
    private int problemId;
    private String language;
    private String code;
    /**
     * 运行的错误信息
     */
    private String error;
    /**
     * 运行消耗的内存,单位KB,默认-1
     */
    private int memory = -1;

    /**
     * 运行消耗的时间,单位ms,默认-1
     */
    private int cpuTime = -1;

    private int realTime = -1;
    
    private int checkpointSize = 0;

    private int totalCorrect = 0;
    
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getCpuTime() {
        return cpuTime;
    }

    public void setCpuTime(int cpuTime) {
        this.cpuTime = cpuTime;
    }

    public int getRealTime() {
        return realTime;
    }

    public void setRealTime(int realTime) {
        this.realTime = realTime;
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

    public int getCheckpointSize() {
        return checkpointSize;
    }

    public void setCheckpointSize(int checkpointSize) {
        this.checkpointSize = checkpointSize;
    }

    public int getTotalCorrect() {
        return totalCorrect;
    }

    public void setTotalCorrect(int totalCorrect) {
        this.totalCorrect = totalCorrect;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
