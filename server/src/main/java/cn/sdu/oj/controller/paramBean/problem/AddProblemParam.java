package cn.sdu.oj.controller.paramBean.problem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddProblemParam {
    private Integer id;
    private String name;
    private String description;
    private String example;
    private String answer;
    private int type;
    private Integer status = 0;
    private String tags;
    private int difficulty;
    private String tip;
}
