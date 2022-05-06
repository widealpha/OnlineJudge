package cn.sdu.oj.domain.bo;

public class JudgeTask {
    private String taskId;
    private int problemId;
    private LanguageEnum language;
    private String code;
    private JudgeLimit limit;
    private boolean specialJudge;
    private LanguageEnum specialJudgeLanguage;

    /**
     * 是否是测试判题模式
     */
    private boolean testMode = false;
    private String testInput;

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

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public String getTestInput() {
        return testInput;
    }

    public void setTestInput(String testInput) {
        this.testInput = testInput;
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
