package cn.sdu.oj.service;

import cn.sdu.oj.controller.paramBean.problem.AddProblemParam;
import cn.sdu.oj.dao.ProblemMapper;
import cn.sdu.oj.domain.po.ProblemLimit;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Service
public class ProblemService {
    @Autowired
    private ProblemMapper problemMapper;

    public int addProblem(AddProblemParam param) {
        problemMapper.addProblem(param);
        if (param.getTags() != null) {
            // 插入标签
            String tags = param.getTags();
            String[] tagArr = tags.split("_");
            for (int i = 0; i < tagArr.length; i++) {
                problemMapper.addTag(param.getId(), Integer.parseInt(tagArr[i]));
            }
        }
        // 0编程 1选择 答案 2填空 答案
        if (param.getType() > 0) {
            problemMapper.addAnswer(param.getId(), param.getAnswer());
        }
        return param.getId();

    }

    /**
     * 添加一个题目限制
     *
     * @param limit
     * @return
     */
    public int addProblemLimit(ProblemLimit limit) {
        problemMapper.addProblemLimit(limit);
        return limit.getProblemId();
    }

    /**
     * 通过题目id查找对应题目限制
     *
     * @param problemId
     * @return
     */
    ProblemLimit getProblemLimitByProblemId(int problemId) {
        return problemMapper.getProblemLimitByProblemId(problemId);
    }

    public void deleteProblem(int u_id, int p_id) {
        problemMapper.deleteProblem(u_id, p_id);
    }

    public void updateProblem(AddProblemParam param) {
        problemMapper.updateProblem(param);
    }

    public void addTestPoints(int p_id, MultipartFile file, String sha256) throws Exception {
        FileUtil.createDir(FileUtil.SEPARATOR + p_id);
        String fileName = "checkpoints";
        FileUtil.createFile(FileUtil.SEPARATOR + p_id, fileName, "zip", file.getBytes());
        FileUtil.createFile(FileUtil.SEPARATOR + p_id, fileName, "sha256", sha256.getBytes(StandardCharsets.UTF_8));

    }

    /**
     * 检查一个问题是否存在
     * * @param problemId
     *
     * @return
     */
    public boolean isProblemExist(int problemId) {
        return problemMapper.isProblemExist(problemId);
    }

}
