package cn.sdu.oj.domain.bo;

public enum JudgeStatus {
    /* 成功 */
    ALL_CHECKPOINTS_SUCCESS(1, "测试点全部通过"),


    LANGUAGE_NOT_SUPPORT(2, "编程语言不支持"),

    PROBLEM_NOT_EXIST(3, "请求的判题不存在"),
    COMPILE_ERROR(4, "编译错误"),
    RUN_ERROR(5, "运行时出错"),
    CHECKPOINT_ERROR(6, "测试点未通过"),

    SAME_TASK_EXIST(7, "相同任务已被提交"),
    /* 默认失败 */
    COMMON_FAIL(500, "失败");


    private Integer code;
    private String message;

    JudgeStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 根据code获取message
     *
     * @return 获取message信息
     */
    public static String getMessageByCode(Integer code) {
        for (JudgeStatus ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
