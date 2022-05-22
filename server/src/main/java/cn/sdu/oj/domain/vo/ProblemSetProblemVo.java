package cn.sdu.oj.domain.vo;


import cn.sdu.oj.domain.po.ProblemSetProblem;

public class ProblemSetProblemVo {
    private Integer id;
    private Integer problem_id;
    private Integer problem_set_id;
    private Integer type;
    private Integer complement;

    public ProblemSetProblemVo(ProblemSetProblem problemSetProblem, Integer complement) {
        this.complement = complement;

        this.id = problemSetProblem.getId();
        this.problem_id =problemSetProblem.getProblem_id();
        this.problem_set_id =problemSetProblem.getProblem_set_id();
        this.type = problemSetProblem.getType();
    }

    public Integer getComplement() {
        return complement;
    }

    public void setComplement(Integer complement) {
        this.complement = complement;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProblem_id() {
        return problem_id;
    }

    public void setProblem_id(Integer problem_id) {
        this.problem_id = problem_id;
    }

    public Integer getProblem_set_id() {
        return problem_set_id;
    }

    public void setProblem_set_id(Integer problem_set_id) {
        this.problem_set_id = problem_set_id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}


