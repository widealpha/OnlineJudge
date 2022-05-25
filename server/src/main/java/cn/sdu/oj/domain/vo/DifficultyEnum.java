package cn.sdu.oj.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DifficultyEnum {
    EASY(0, "简单"),
    MEDIUM(1, "中等"),
    HARD(2, "困难");

    public final int id;
    public final String name;

    public static String difficultyName(Integer typeId) {
        if (typeId == null) {
            return null;
        }
        switch (typeId) {
            case 0:
                return "简单";
            case 1:
                return "中等";
            case 2:
                return "困难";
            default:
                return null;
        }
    }

    DifficultyEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "DifficultyEnum{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
