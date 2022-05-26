package cn.sdu.oj.domain.vo;

public enum ProblemSetTypeEnum {
    PRACTICE(1,"练习"),
    EXAM(2,"测验"),
    CONTEST(3,"竞赛");

    public final int id;
    public final String name;

    ProblemSetTypeEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
