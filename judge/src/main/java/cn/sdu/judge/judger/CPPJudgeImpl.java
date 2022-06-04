package cn.sdu.judge.judger;

import cn.sdu.judge.bean.Checkpoint;
import cn.sdu.judge.bean.CompileInfo;
import cn.sdu.judge.bean.JudgeLimit;
import cn.sdu.judge.bean.RunInfo;
import cn.sdu.judge.util.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * c++编译执行器
 */
public class CPPJudgeImpl implements JudgeInterface {

    File inputFile;
    File outputFile;
    File errorFile;
    File compiledFile;

    Path tempDir;

    public CPPJudgeImpl(String prefix) throws IOException {
        tempDir = Files.createTempDirectory("oj-" + prefix);
        inputFile = Files.createFile(tempDir.resolve("main.cpp")).toFile();
        outputFile = Files.createFile(tempDir.resolve("output.txt")).toFile();
        errorFile = Files.createFile(tempDir.resolve("error.txt")).toFile();
        compiledFile = Files.createFile(tempDir.resolve("main.exe")).toFile();
    }

    public CPPJudgeImpl() throws IOException {
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
        processBuilder.command("g++", inputFile.getAbsolutePath(), "-o", compiledFile.getAbsolutePath());
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
    public RunInfo run(Checkpoint checkpoint, JudgeLimit limit) throws IOException, InterruptedException {

        //构建进程，重定向输入输出和错误
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(machine,
                "-b", compiledFile.getAbsolutePath(),
                "-i", checkpoint.getInput().getAbsolutePath(),
                "-o", outputFile.getAbsolutePath(),
                "-e", errorFile.getAbsolutePath(),
                "--max-memory", String.valueOf(limit.getMemory()),
                "--max-cpu-time", String.valueOf(limit.getCpuTime()),
                "--max-real-time", String.valueOf(limit.getRealTime()));
        Process process = processBuilder.start();
        //构建运行信息
        RunInfo runInfo = new RunInfo();
        runInfo.setExitCode(process.waitFor());
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        String res = reader.readLine();
        JSONObject jsonObject = JSONObject.parseObject(res);
        runInfo.setSignal(jsonObject.getInteger("signal"));
        runInfo.setCpuTime(jsonObject.getInteger("cpu_time"));
        runInfo.setMemory(jsonObject.getInteger("memory"));
        runInfo.setRealTime(jsonObject.getInteger("real_time"));
        //关掉输出流防止后面的读取收到影响
        process.getErrorStream().close();
        process.getInputStream().close();
        runInfo.setCheckpoint(checkpoint);
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
