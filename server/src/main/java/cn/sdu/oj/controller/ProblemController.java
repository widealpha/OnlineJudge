package cn.sdu.oj.controller;

import cn.sdu.oj.controller.paramBean.problem.AddProblemParam;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.service.ProblemService;
import cn.sdu.oj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/problem")
public class ProblemController {
    @Autowired
    private ProblemService problemService;

    @PostMapping("/addProblem")
    public ResultEntity addProblem(AddProblemParam param, @AuthenticationPrincipal User user) {
        // 处理参数
        param.setAuthor(user.getId());
        problemService.addProblem(param);
        return ResultEntity.data(param.getId());
    }

    @PostMapping("/deleteProblem")
    public ResultEntity deleteProblem(int id, @AuthenticationPrincipal User user) {
        problemService.deleteProblem(user.getId(), id);
        return ResultEntity.success();
    }

    @PostMapping("/updateProblem")
    public ResultEntity updateProblem(AddProblemParam param, @AuthenticationPrincipal User user) {
        // 处理参数
        param.setAuthor(user.getId());
        problemService.updateProblem(param);
        return ResultEntity.success();

    }


    @PostMapping("/addTestPoints")
    public ResultEntity addTestPoints(int problemId, String sha256, MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            return ResultEntity.error("file is null");
        }
        String verify = FileUtil.getSHA256(file.getBytes());
        if (!verify.equals(sha256)) {
            return ResultEntity.error("file is bad");
        }
        // 写文件
        problemService.addTestPoints(problemId, file, sha256);
        return ResultEntity.success();
    }


}
