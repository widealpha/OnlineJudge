package cn.sdu.oj.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
/**
 * 问题，带着答案，标签
 */
public class ProblemWithInfo {
    protected Integer id;
    protected String name;
    protected String description;
    protected String example;
    protected Integer difficulty;
    protected Integer isOpen;
    protected String tip;
    protected Integer author;
    protected String answer;
    protected String tags;
    /**
     * 0为编程题
     */
    protected Integer type;

}
