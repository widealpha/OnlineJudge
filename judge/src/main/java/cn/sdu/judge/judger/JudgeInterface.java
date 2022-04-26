package cn.sdu.judge.judger;

import cn.sdu.judge.bean.Checkpoint;
import cn.sdu.judge.bean.CompileInfo;
import cn.sdu.judge.bean.RunInfo;

import java.io.File;
import java.io.IOException;

public interface JudgeInterface {
    /**
     * 编译文件
     *
     * @param code 需要编译的代码
     * @return 编译过程中的信息
     * @throws IOException          IO异常
     * @throws InterruptedException 等待进程编译时发生的异常
     */
    CompileInfo compile(String code) throws IOException, InterruptedException;

    /**
     * 运行程序
     *
     * @param checkpoint 测试点文件
     * @return 运行过程中的信息
     * @throws IOException          IO异常
     * @throws InterruptedException 等待进程运行时发生的异常
     */
    RunInfo run(Checkpoint checkpoint) throws IOException, InterruptedException;

    /**
     * 清理编译中间以及结果产物
     */
    void clean();
}
