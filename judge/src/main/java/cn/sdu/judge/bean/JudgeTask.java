package cn.sdu.judge.bean;

public class JudgeTask {
    private int taskId;
    private int problemId;
    private LanguageEnum language;
    private String code;
    private JudgeLimit limit;

    public LanguageEnum getLanguage() {
        return language;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public void setLanguage(LanguageEnum language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JudgeLimit getLimit() {
        return limit;
    }

    public void setLimit(JudgeLimit limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "JudgeTask{" +
                "language='" + language + '\'' +
                ", code='" + code + '\'' +
                ", limit='" + limit + '\'' +
                '}';
    }
}
