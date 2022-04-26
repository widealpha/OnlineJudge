package cn.sdu.judge.judger;

import cn.sdu.judge.bean.Checkpoint;
import cn.sdu.judge.bean.CompileInfo;
import cn.sdu.judge.bean.RunInfo;
import cn.sdu.judge.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class CJudgeImpl implements JudgeInterface {
    File inputFile;
    File outputFile;
    File errorFile;
    File compiledFile;

    Path tempDir;

    public CJudgeImpl(String prefix) throws IOException {
        tempDir = Files.createTempDirectory("oj-" + prefix);
        inputFile = Files.createFile(tempDir.resolve("main.cpp")).toFile();
        outputFile = Files.createFile(tempDir.resolve("output.txt")).toFile();
        errorFile = Files.createFile(tempDir.resolve("error.txt")).toFile();
        compiledFile = Files.createFile(tempDir.resolve("main.exe")).toFile();
    }

    public CJudgeImpl() throws IOException {
        this(UUID.randomUUID().toString());
    }

    @Override
    public CompileInfo compile(String code) throws IOException, InterruptedException {
        FileOutputStream outputStream = new FileOutputStream(inputFile);
        outputStream.write(("" + code).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();

        //构建进程
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("gcc", inputFile.getAbsolutePath(), "-o", compiledFile.getAbsolutePath());
        Process process = processBuilder.start();
        //构建编译信息
        CompileInfo compileInfo = new CompileInfo();
        compileInfo.setExitCode(process.waitFor());
        compileInfo.setSuccess(compileInfo.getExitCode() == 0);
        //获取编译输出和错误信息
        compileInfo.setError(new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8));
        compileInfo.setOutput(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
        //关掉输出流防止后面的读取收到影响
        process.getErrorStream().close();
        process.getInputStream().close();
        return compileInfo;
    }

    @Override
    public RunInfo run(Checkpoint checkpoint) throws IOException, InterruptedException {
        //构建进程，重定向输入输出和错误
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(compiledFile.getAbsolutePath());
        processBuilder.redirectInput(checkpoint.getInput().getAbsoluteFile());
        processBuilder.redirectOutput(outputFile);
        processBuilder.redirectError(errorFile);
        Process process = processBuilder.start();
        //构建运行信息
        RunInfo runInfo = new RunInfo();
        runInfo.setExitCode(process.waitFor());
        //关掉输出流防止后面的读取收到影响
        process.getErrorStream().close();
        process.getInputStream().close();
        runInfo.setSuccess(FileUtil.fileSame(checkpoint.getOutput(), outputFile));
        runInfo.setError(FileUtil.topLinesFromFile(errorFile, 50));
        runInfo.setOutput(FileUtil.topLinesFromFile(outputFile, 50));
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
