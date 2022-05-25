package cn.sdu.oj.domain.po;

public class GeneralProblem {
    private int id;
    private int type;
    private int typeProblemId;

    private int difficulty;

    private int creator;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getTypeProblemId() {
        return typeProblemId;
    }

    public void setTypeProblemId(int typeProblemId) {
        this.typeProblemId = typeProblemId;
    }


    public int getCreator() {
        return creator;
    }
    public void setCreator(int creator) {
        this.creator = creator;
    }
}
