package cn.sdu.oj.domain.po;

import cn.sdu.oj.domain.bo.Problem;

import java.util.Date;

/**
 * @author 陈景涛
 * 非编程题
 */
public class NonProgramProblem extends Problem {
    protected String answer = "";
    protected Integer status = 0;
    protected Date createTime;
    protected Date lastModifyTime;

    public NonProgramProblem(Integer id, String name, String description, String example, Integer difficulty, Integer isOpen, String tip, Integer author, String answer) {
        super(id, name, description, example, difficulty, isOpen, tip, author);
        this.answer = answer;
    }

    public NonProgramProblem() {

    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    @Override
    public String toString() {
        return "NonProgramProblem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", example='" + example + '\'' +
                ", difficulty=" + difficulty +
                ", isOpen=" + isOpen +
                ", tip='" + tip + '\'' +
                ", author=" + author +
                ", answer='" + answer + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", lastModifyTime=" + lastModifyTime +
                '}';
    }
}
