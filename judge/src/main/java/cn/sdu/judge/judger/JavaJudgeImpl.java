package cn.sdu.judge.judger;

import cn.sdu.judge.bean.Checkpoint;
import cn.sdu.judge.bean.CompileInfo;
import cn.sdu.judge.bean.JudgeLimit;
import cn.sdu.judge.bean.RunInfo;
import cn.sdu.judge.util.FileUtil;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * java编译执行器
 */
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
    public CompileInfo compile(String code) throws IOException, InterruptedException {
        CompileInfo compileInfo = new CompileInfo();

        FileOutputStream outputStream = new FileOutputStream(inputFile);
        outputStream.write(("" + code).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();

        //构建进程，重定向编译输出和错误
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("javac", inputFile.getAbsolutePath(), "-d", compiledFile.getParent(),
                "-encoding", "UTF-8", "-J-Dfile.encoding=UTF-8");
        Process process = processBuilder.start();
        //构建编译信息
        compileInfo.setExitCode(process.waitFor());
        compileInfo.setSuccess(compileInfo.getExitCode() == 0);
        compileInfo.setOutput(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
        compileInfo.setError(new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8));
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
                "-b", "/usr/bin/java",
                "-i", checkpoint.getInput().getAbsolutePath(),
                "-o", outputFile.getAbsolutePath(),
                "-e", errorFile.getAbsolutePath(),
                "--args", "-Dfile.encoding=UTF-8 -classpath " + compiledFile.getParent() + " Main",
                "--max-memory", String.valueOf(limit.getMemory()),
                "--max-cpu-time", String.valueOf(limit.getCpuTime()),
                "--max-real-time", String.valueOf(limit.getRealTime()));
        Process process = processBuilder.start();
        RunInfo runInfo = new RunInfo();
        //构建运行信息
        runInfo.setExitCode(process.waitFor());
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        String res = reader.readLine();
        JSONObject jsonObject = JSONObject.parseObject(res);
        runInfo.setCpuTime(jsonObject.getInteger("cpu_time"));
        runInfo.setSignal(jsonObject.getInteger("signal"));
        runInfo.setMemory(jsonObject.getInteger("memory"));
        runInfo.setRealTime(jsonObject.getInteger("real_time"));
        //关掉输出流防止后面的读取收到影响
        process.getErrorStream().close();
        process.getInputStream().close();
        runInfo.setCheckpoint(checkpoint);
        runInfo.setSuccess(FileUtil.fileSame(checkpoint.getOutput(), outputFile));
        runInfo.setError(FileUtil.topLinesFromFile(errorFile, 100));
        runInfo.setOutput(FileUtil.topLinesFromFile(outputFile, 100));
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
