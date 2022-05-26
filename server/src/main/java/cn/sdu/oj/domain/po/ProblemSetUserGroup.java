package cn.sdu.oj.domain.po;

public class ProblemSetUserGroup {
    private Integer id;
    private Integer problem_set_id;
    private Integer user_group_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProblem_set_id() {
        return problem_set_id;
    }

    public void setProblem_set_id(Integer problem_set_id) {
        this.problem_set_id = problem_set_id;
    }

    public Integer getUser_group_id() {
        return user_group_id;
    }

    public void setUser_group_id(Integer user_group_id) {
        this.user_group_id = user_group_id;
    }
}
