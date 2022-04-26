package cn.sdu.judge.bean;

public class JudgeTask {
    private String taskId;
    private int problemId;
    private LanguageEnum language;
    private String code;
    private JudgeLimit limit;
    private boolean specialJudge;
    private LanguageEnum specialJudgeLanguage;

    public LanguageEnum getLanguage() {
        return language;
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

    public boolean isSpecialJudge() {
        return specialJudge;
    }

    public void setSpecialJudge(boolean specialJudge) {
        this.specialJudge = specialJudge;
    }

    public LanguageEnum getSpecialJudgeLanguage() {
        return specialJudgeLanguage;
    }

    public void setSpecialJudgeLanguage(LanguageEnum specialJudgeLanguage) {
        this.specialJudgeLanguage = specialJudgeLanguage;
    }

    @Override
    public String toString() {
        return "JudgeTask{" +
                "taskId=" + taskId +
                ", problemId=" + problemId +
                ", language=" + language +
                ", code='" + code + '\'' +
                ", limit=" + limit +
                ", specialJudge=" + specialJudge +
                ", specialJudgeLanguage=" + specialJudgeLanguage +
                '}';
    }
}
