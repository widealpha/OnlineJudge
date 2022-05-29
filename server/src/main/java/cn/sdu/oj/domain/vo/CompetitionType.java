package cn.sdu.oj.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CompetitionType {
    ACM(0, "ACM"),
    OI(1, "OI"),
    IOI(2, "IOI");

    public final int id;
    public final String name;

    CompetitionType(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
