package cn.sdu.oj.domain.vo;


import cn.sdu.oj.domain.po.AnswerRecord;
import cn.sdu.oj.domain.po.ProblemSetProblem;
import cn.sdu.oj.domain.po.SolveRecord;

public class ProblemSetProblemVo {

    private Integer type;
    private Integer score;

    private SolveRecord solveRecord;
    private AnswerRecord answerRecord;



    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public AnswerRecord getAnswerRecord() {
        return answerRecord;
    }

    public void setAnswerRecord(AnswerRecord answerRecord) {
        this.answerRecord = answerRecord;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public SolveRecord getSolveRecord() {
        return solveRecord;
    }

    public void setSolveRecord(SolveRecord solveRecord) {
        this.solveRecord = solveRecord;
    }
}


