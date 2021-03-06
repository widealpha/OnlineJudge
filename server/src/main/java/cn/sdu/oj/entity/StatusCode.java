package cn.sdu.oj.entity;

public enum StatusCode {
    /* 成功 */
    SUCCESS(0, "成功"),

    /* 默认失败 */
    COMMON_FAIL(-1, "失败"),

    /* 参数错误：1000～1999 */
    PARAM_NOT_VALID(1001, "参数不合法"),
    PARAM_EMPTY(1002, "参数为空"),
    PARAM_TYPE_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失"),

    /* 用户错误 */
    USER_NOT_LOGIN(2001, "用户未登录"),

    USER_CREDENTIALS_ERROR(2002, "用户名或密码错误"),

    USER_ACCOUNT_DISABLE(2003, "账号被禁用"),

    USER_TOKEN_ERROR(2004, "token校验失败，请检查token是否被篡改"),

    USER_NO_TOKEN_ERROR(2005, "无token"),

    USER_TOKEN_OVERDUE(2006, "token过期，请重新登陆"),

    USER_ACCOUNT_NOT_EXIST(2007, "用户不存在"),

    USER_ACCOUNT_ALREADY_EXIST(2008, "账号已存在"),

    USER_ACCOUNT_USE_BY_OTHERS(2009, "账号下线"),
    ROLE_NOT_EXIST(2010, "权限不存在"),

    /* 业务错误 */
    NO_PERMISSION(3001, "没有权限"),
    DATA_NOT_EXIST(3002, "数据不存在"),
    NO_PERMISSION_OR_EMPTY(3003, "请求数据不存在或无权获取数据"),
    DATA_ALREADY_EXIST(3004, "数据已存在"),
    VALIDATE_ERROR(3005, "验证失败"),
    /* 判题状态码 */
    OVER_SOLVE_LIMIT(3101, "超出并行判题数量限制"),
    PROBLEM_NOT_EXIST(3102, "请求的题目不存在"),

    SAME_TASK_EXIST(3107, "相同任务已被提交"),
    OVER_LIMIT(3108, "超出资源限制"),

    WAIT_JUDGE(3110, "等待评测"),
    /* 成功 */
    JUDGE_SUCCESS(3111, "测试点通过"),
    LANGUAGE_NOT_SUPPORT(3112, "编程语言不支持"),
    JUDGE_TIME_OUT(3113, "评测超时"),
    JUDGE_MEMORY_OUT(3114, "评测内存超限"),
    JUDGE_OUTPUT_OUT(3115, "评测输出超限"),
    JUDGE_COMPILE_ERROR(3116, "编译错误"),
    JUDGE_RUNTIME_ERROR(3117, "运行时出错错误"),
    JUDGE_WRONG_ANSWER(3118, "测试点未完全通过"),
    JUDGE_SYSTEM_ERROR(3119, "系统错误"),
    CODE_TOO_LONG(3120, "代码长度过长"),


    LAST_ERROR_CODE(9999, "如果用到了这个错误码,那确实是非常不幸,建议提桶跑路");

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
