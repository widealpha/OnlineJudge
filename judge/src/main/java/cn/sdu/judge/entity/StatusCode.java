package cn.sdu.judge.entity;

public enum StatusCode {
    WAIT_JUDGE(0, "等待评测"),
    /* 成功 */
    JUDGE_SUCCESS(1, "测试点通过"),
    LANGUAGE_NOT_SUPPORT(2, "编程语言不支持"),
    JUDGE_TIME_OUT(3, "评测超时"),
    JUDGE_MEMORY_OUT(4, "评测内存超限"),
    JUDGE_OUTPUT_OUT(5, "评测输出超限"),
    JUDGE_COMPILE_ERROR(6, "编译错误"),
    JUDGE_RUNTIME_ERROR(7, "运行时出错错误"),
    JUDGE_WRONG_ANSWER(8, "测试点未通过"),

    SAME_TASK_EXIST(300, "相同任务已被提交"),
    PROBLEM_NOT_EXIST(501, "请求的判题不存在"),
    /* 默认失败 */
    JUDGE_SYSTEM_ERROR(500, "系统错误");


    private Integer code;
    private String message;

    StatusCode(Integer code, String message) {
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
        for (StatusCode ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
