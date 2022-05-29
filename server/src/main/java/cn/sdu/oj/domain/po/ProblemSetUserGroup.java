package cn.sdu.oj.domain.po;

public class ProblemSetUserGroup {
    private Integer id;
    private Integer problemSetId;
    private Integer userGroupId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProblemSetId() {
        return problemSetId;
    }

    public void setProblemSetId(Integer problemSetId) {
        this.problemSetId = problemSetId;
    }

    public Integer getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(Integer userGroupId) {
        this.userGroupId = userGroupId;
    }
}
