package cn.sdu.oj.controller.paramBean.problem;

import cn.sdu.oj.util.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Default
public class AddProblemParam {

    private Integer id;
    private String name;
    private String description;
    private String example;
    private String answer;
    private Integer type = 0;
    private Integer status = 0;
    private String tags;
    private Integer difficulty;
    private String tip;
    private Integer author;
}
