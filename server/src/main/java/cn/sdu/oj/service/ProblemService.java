package cn.sdu.oj.service;

import cn.sdu.oj.controller.paramBean.problem.AddProblemParam;
import cn.sdu.oj.dao.ProblemMapper;
import cn.sdu.oj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void addTestPoints(int p_id, MultipartFile file, String sha256) throws Exception {
        FileUtil.createDir(FileUtil.SEPARATOR + p_id);
        String fileName = "checkpoints";
        FileUtil.createFile(FileUtil.SEPARATOR + p_id, fileName, "zip", file.getBytes());
        FileUtil.createFile(FileUtil.SEPARATOR + p_id, fileName, "sha256", sha256.getBytes(StandardCharsets.UTF_8));

    }


}
