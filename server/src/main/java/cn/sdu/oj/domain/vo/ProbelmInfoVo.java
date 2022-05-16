package cn.sdu.oj.domain.vo;

import cn.sdu.oj.domain.bo.Problem;
import cn.sdu.oj.domain.po.ProblemLimit;
import cn.sdu.oj.domain.po.ProgramProblem;
import cn.sdu.oj.domain.po.Tag;
import cn.sdu.oj.domain.po.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProbelmInfoVo {
    protected Integer id;
    protected String name;
    protected String description;
    protected String example;
    protected Integer difficulty;
    protected Integer isOpen;
    protected String tip;
    protected UserInfo authorInfo;
    protected List<Tag> tagList;
    protected Integer type;
    protected ProblemLimit limit;

    public ProbelmInfoVo(Problem problem, ProblemLimit limit, UserInfo info, List<Tag> tagList) {
        id = problem.getId();
        name = problem.getName();
        description = problem.getDescription();
        example = problem.getExample();
        difficulty = problem.getDifficulty();
        isOpen = problem.getIsOpen();
        tip = problem.getTip();
        authorInfo = info;
        this.tagList = tagList;
        type = problem instanceof ProgramProblem ? 0 : -1;
        this.limit = limit;
    }
}
