package cn.sdu.judge.judger;

import cn.sdu.judge.bean.Checkpoint;
import cn.sdu.judge.bean.CompileInfo;
import cn.sdu.judge.bean.RunInfo;
import cn.sdu.judge.util.FileUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class JavaJudgeImpl implements JudgeInterface {
    File inputFile;
    File outputFile;
    File errorFile;
    File compiledFile;

    Path tempDir;

    public JavaJudgeImpl() throws IOException {
        this(UUID.randomUUID().toString());
    }

    public JavaJudgeImpl(String prefix) throws IOException {
        tempDir = Files.createTempDirectory("oj-" + prefix);
        inputFile = Files.createFile(tempDir.resolve("Main.java")).toFile();
        outputFile = Files.createFile(tempDir.resolve("output.txt")).toFile();
        errorFile = Files.createFile(tempDir.resolve("error.txt")).toFile();
        compiledFile = Files.createFile(tempDir.resolve("Main.class")).toFile();
    }

    @Override
    public CompileInfo compile(String input) throws IOException, InterruptedException {
        CompileInfo compileInfo = new CompileInfo();

        FileOutputStream outputStream = new FileOutputStream(inputFile);
        outputStream.write(("" + input).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();

        //构建进程，重定向编译输出和错误
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("javac", inputFile.getAbsolutePath(), "-d", compiledFile.getParent(),
                "-encoding", "UTF-8");
        processBuilder.redirectError(errorFile);
        processBuilder.redirectOutput(outputFile);
        Process process = processBuilder.start();

        boolean b = errorFile.delete();
        //构建编译信息
        compileInfo.setExitCode(process.waitFor());
        compileInfo.setSuccess(compileInfo.getExitCode() == 0);
        compileInfo.setError(FileUtil.readAllFromFile(errorFile));
        compileInfo.setOutput(FileUtil.readAllFromFile(outputFile));
        return compileInfo;
    }

    @Override
    public RunInfo run(Checkpoint checkpoint) throws IOException, InterruptedException {
        RunInfo runInfo = new RunInfo();
        runInfo.setCheckpoint(checkpoint);
        try {
            //构建进程，重定向输入输出和错误
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("java", "Main");
            processBuilder.redirectInput(checkpoint.getInput().getAbsoluteFile());
            processBuilder.redirectOutput(outputFile);
            processBuilder.redirectError(errorFile);
            Process process = processBuilder.start();
            //构建运行信息
            runInfo.setExitCode(process.waitFor());
            runInfo.setSuccess(FileUtil.fileSame(checkpoint.getOutput(), outputFile));
            runInfo.setError(FileUtil.readAllFromFile(errorFile));
            runInfo.setOutput(FileUtil.readAllFromFile(outputFile));
        } catch (IOException e) {
            runInfo.setSuccess(false);
            runInfo.setError(e.getMessage());
            runInfo.setExitCode(-1);
        }
        return runInfo;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void clean() {
        File[] children = tempDir.toFile().listFiles();
        if (children != null) {
            for (File file : children) {
                file.delete();
            }
        }
        tempDir.toFile().delete();
    }
}
