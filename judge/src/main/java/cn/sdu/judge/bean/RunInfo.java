package cn.sdu.judge.bean;

public class RunInfo {
    private int exitCode;
    private boolean success;

    private String error;
    private String output;

    private Checkpoint checkpoint;

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Checkpoint getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(Checkpoint checkpoint) {
        this.checkpoint = checkpoint;
    }

    @Override
    public String toString() {
        return "RunInfo{" +
                "exitCode=" + exitCode +
                ", success=" + success +
                ", error='" + error + '\'' +
                ", output='" + output + '\'' +
                '}';
    }
}
