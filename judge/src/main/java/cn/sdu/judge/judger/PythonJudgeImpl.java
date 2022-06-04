package cn.sdu.judge.judger;

import cn.sdu.judge.bean.Checkpoint;
import cn.sdu.judge.bean.CompileInfo;
import cn.sdu.judge.bean.RunInfo;
import cn.sdu.judge.util.FileUtil;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * python编译执行器
 */
public class PythonJudgeImpl implements JudgeInterface {
    File inputFile;
    File outputFile;
    File errorFile;

    Path tempDir;

    public PythonJudgeImpl(String prefix) throws IOException {
        tempDir = Files.createTempDirectory("oj-" + prefix);
        inputFile = Files.createFile(tempDir.resolve("main.py")).toFile();
        outputFile = Files.createFile(tempDir.resolve("output.txt")).toFile();
        errorFile = Files.createFile(tempDir.resolve("error.txt")).toFile();
    }

    public PythonJudgeImpl() throws IOException {
        this(UUID.randomUUID().toString());
    }

    /**
     * 将需要编译的代码写入文件直接编译成功
     *
     * @param code 需要编译的代码
     * @return 返回成功
     */
    @Override
    public CompileInfo compile(String code) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(inputFile);
        outputStream.write(("" + code).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();

        CompileInfo compileInfo = new CompileInfo();
        compileInfo.setExitCode(0);
        compileInfo.setSuccess(true);
        return compileInfo;
    }

    @Override
    public RunInfo run(Checkpoint checkpoint) throws IOException, InterruptedException {
        //构建进程，重定向输入输出和错误
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(machine,
                "-b", "/usr/bin/python3",
                "-i", checkpoint.getInput().getAbsolutePath(),
                "-o", outputFile.getAbsolutePath(),
                "-e", errorFile.getAbsolutePath(),
                "--args", inputFile.getAbsolutePath());
        Process process = processBuilder.start();
        //构建运行信息
        RunInfo runInfo = new RunInfo();
        runInfo.setExitCode(process.waitFor());
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        String res = reader.readLine();
        JSONObject jsonObject = JSONObject.parseObject(res);
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
