package cn.sdu.oj.domain.dto;

import cn.sdu.oj.domain.bo.Problem;
import cn.sdu.oj.domain.po.AsyncProblem;
import cn.sdu.oj.domain.po.GeneralProblem;
import cn.sdu.oj.domain.po.ProblemLimit;
import cn.sdu.oj.domain.po.Tag;
import cn.sdu.oj.domain.vo.DifficultyEnum;
import cn.sdu.oj.domain.vo.ProblemTypeEnum;

import java.util.List;

public class ProblemDto {
    private Integer id;
    private String name;
    private Integer type;
    private String description;

    private String example;
    private String typeName;
    private String answer;

    private String options;
    private Integer creator;
    private Integer difficulty;
    private String difficultyName;

    private List<Tag> tags;

    /**
     * 时间限制,单位ms
     */
    private int timeLimit;
    /**
     * 占用内存 单位为KB
     */
    private int memoryLimit;
    /**
     * 代码文本长度,单位为Byte
     */
    private int codeLengthLimit;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getDifficultyName() {
        return difficultyName;
    }

    public void setDifficultyName(String difficultyName) {
        this.difficultyName = difficultyName;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public int getCodeLengthLimit() {
        return codeLengthLimit;
    }

    public void setCodeLengthLimit(int codeLengthLimit) {
        this.codeLengthLimit = codeLengthLimit;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
