package cn.sdu.judge.bean;

import java.io.File;

public class Checkpoint {

    public Checkpoint() {
    }

    public Checkpoint(int problemId) {
        this.problemId = problemId;
    }

    private int problemId;
    private int order;
    private File input;
    private File output;

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }
}
