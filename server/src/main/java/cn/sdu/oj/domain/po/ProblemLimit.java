package cn.sdu.oj.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProblemLimit {
    /**
     * 主键 自增
     */
    private Integer id;
    /**
     * 对应题目id
     */
    private Integer problemId;
    /**
     * 运行时间 单位为ms
     */
    private Integer time = 3000;
    /**
     * 占用内存 单位为KB
     */
    private Integer memory = 65536;
    /**
     * 代码文本长度
     */
    private Integer text;

    private Date createTime;
    private Date lastModifyTime;
    private Integer status;

    public ProblemLimit(Integer problemId, Integer time, Integer memory, Integer text) {
        this.problemId = problemId;
        this.time = time;
        this.memory = memory;
        this.text = text;
    }
}
