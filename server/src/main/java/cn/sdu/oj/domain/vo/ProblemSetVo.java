package cn.sdu.oj.domain.vo;

import cn.sdu.oj.domain.po.ProblemSet;
import cn.sdu.oj.domain.po.ProblemSetProblem;

import java.util.Date;
import java.util.List;

public class ProblemSetVo {
    private Integer id;
    private String name;
    private String type;
    private String introduction;
    private Integer isPublic;
    private Date beginTime;
    private Date endTime;
    private Integer creatorId;
    private List<ProblemSetProblem> problems;

    public ProblemSetVo(ProblemSet problemSet, List<ProblemSetProblem> problems) {
        this.problems =problems;
        this.id = problemSet.getId();
        this.name = problemSet.getName();
        this.type = problemSet.getType();
        this.introduction = problemSet.getIntroduction();
        this.isPublic = problemSet.getIsPublic();
        this.beginTime = problemSet.getBeginTime();
        this.endTime = problemSet.getEndTime();
        this.creatorId = problemSet.getCreatorId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }


    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
    }


    public List<ProblemSetProblem> getProblems() {
        return problems;
    }

    public void setProblems(List<ProblemSetProblem> problems) {
        this.problems = problems;
    }
}

