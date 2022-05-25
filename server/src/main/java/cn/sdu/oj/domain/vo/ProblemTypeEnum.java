package cn.sdu.oj.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ProblemTypeEnum {
    PROGRAMING(0, "编程题"),
    CHOICE(1, "选择题"), //选择题
    COMPLETION(2, "填空题"), //填空题
    JUDGEMENT(3, "判断题"), //判断题
    SHORT(4, "简答题"); //简答题
    public final int id;
    public final String name;

    public static String typeName(Integer typeId) {
        if (typeId == null) {
            return "";
        }
        switch (typeId) {
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

    public static ProblemTypeEnum valueOf(int typeId) {
        for (ProblemTypeEnum e : ProblemTypeEnum.values()) {
            if (e.id == typeId) {
                return e;
            }
        }
        return null;
    }

    ProblemTypeEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
