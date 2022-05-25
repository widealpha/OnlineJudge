package cn.sdu.oj.domain.vo;

public enum DifficultyEnum {
    EASY(0),
    MEDIUM(1),
    HARD(2);

    public final int value;

    public static String difficultyName(Integer value) {
        if (value == null){
            return null;
        }
        switch (value) {
            case 0:
                return "简单";
            case 1:
                return "中等";
            case 2:
                return "困难";
            default:
                return "无";
        }
    }

    DifficultyEnum(int value) {
        this.value = value;
    }
}
