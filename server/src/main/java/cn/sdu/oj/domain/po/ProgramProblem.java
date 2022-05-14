package cn.sdu.oj.domain.po;

import cn.sdu.oj.domain.bo.Problem;

import java.util.Date;

/**
 * @author 陈景涛
 * 编程题
 */
public class ProgramProblem extends Problem {

    protected Integer status = 0;
    protected Date createTime;
    protected Date lastModifyTime;

    public ProgramProblem(Integer id, String name, String description, String example, Integer difficulty, Integer isOpen, String tip, Integer author) {
        super(id, name, description, example, difficulty, isOpen, tip, author);
    }

    public ProgramProblem() {
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
        return "ProgramProblem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", example='" + example + '\'' +
                ", difficulty=" + difficulty +
                ", isOpen=" + isOpen +
                ", tip='" + tip + '\'' +
                ", author=" + author +
                ", status=" + status +
                ", createTime=" + createTime +
                ", lastModifyTime=" + lastModifyTime +
                '}';
    }
}
