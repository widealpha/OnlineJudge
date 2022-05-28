package cn.sdu.oj.domain.po;

public class ProblemSetUserAnswer {
    private Integer id;
    private Integer user_id;
    private Integer problem_set_id;
    private Integer is_submit;
    private String answer;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getProblem_set_id() {
        return problem_set_id;
    }

    public void setProblem_set_id(Integer problem_set_id) {
        this.problem_set_id = problem_set_id;
    }

    public Integer getIs_submit() {
        return is_submit;
    }

    public void setIs_submit(Integer is_submit) {
        this.is_submit = is_submit;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
