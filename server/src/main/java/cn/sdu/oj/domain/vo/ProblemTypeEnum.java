package cn.sdu.oj.domain.vo;

public enum ProblemTypeEnum {
    PROGRAMING(0),
    CHOICE(1), //选择题
    COMPLETION(2), //填空题
    JUDGEMENT(3), //判断题
    SHORT(4); //简答题
    public final int value;

    public static String typeName(Integer value) {
        if (value == null) {
            return "";
        }
        switch (value) {
            case 0:
                return "编程题";
            case 1:
                return "选择题";
            case 2:
                return "填空题";
            case 3:
                return "判断题";
            case 4:
                return "简答题";
            default:
                return "";
        }
    }

    public static ProblemTypeEnum valueOf(int value) {
        for (ProblemTypeEnum e : ProblemTypeEnum.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null;
    }

    ProblemTypeEnum(int value) {
        this.value = value;
    }
}
