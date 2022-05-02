package cn.sdu.oj.domain.bo;


public class JudgeLimit {
    /**
     * cpu时间限制,单位为ms
     */
    private int cpuTime;
    /**
     * 时间限制,单位为ms
     */
    private int realTime;
    /**
     * 内存限制,单位为KB
     */
    private int memory;

    public JudgeLimit() {
    }

    public JudgeLimit(LanguageEnum language) {
        switch (language) {
            case C99:
            case CPP17:
                cpuTime = 1000;
                realTime = 1000;
                memory = 1024 * 5;
                break;
            case JAVA8:
            case PYTHON3:
            default:
                cpuTime = 2000;
                realTime = 2000;
                memory = 1024 * 10;
                break;
        }
    }

    public int getCpuTime() {
        return cpuTime;
    }

    public void setCpuTime(int cpuTime) {
        this.cpuTime = cpuTime;
    }

    public int getRealTime() {
        return realTime;
    }

    public void setRealTime(int realTime) {
        this.realTime = realTime;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }
}
