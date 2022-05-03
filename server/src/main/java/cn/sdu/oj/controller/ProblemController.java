package cn.sdu.oj.controller;

import cn.sdu.oj.controller.paramBean.problem.AddProblemParam;
import cn.sdu.oj.domain.po.ProblemLimit;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.service.ProblemService;
import cn.sdu.oj.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@RequestMapping("/problem")
@Api(value = "问题接口", tags = "问题接口")
public class ProblemController {
    @Autowired
    private ProblemService problemService;

    @ApiOperation("添加题目")
    @PostMapping("/addProblem")
    public ResultEntity addProblem(AddProblemParam param, @ApiIgnore @AuthenticationPrincipal User user) {
        // 处理参数
        param.setAuthor(user.getId());
        problemService.addProblem(param);
        return ResultEntity.data(param.getId());
    }

    @PostMapping("/deleteProblem")
    @ApiOperation("删除问题")
    public ResultEntity deleteProblem(@ApiParam("问题id") @RequestParam int id,
                                      @ApiIgnore @AuthenticationPrincipal User user) {
        problemService.deleteProblem(user.getId(), id);
        return ResultEntity.success();
    }

    @ApiOperation("更新题目")
    @PostMapping("/updateProblem")
    public ResultEntity updateProblem(AddProblemParam param, @ApiIgnore @AuthenticationPrincipal User user) {
        // 处理参数
        param.setAuthor(user.getId());
        problemService.updateProblem(param);
        return ResultEntity.success();

    }

    @ApiOperation("添加测试点")
    @PostMapping("/addTestPoints")
    public ResultEntity addTestPoints(
            @ApiParam("问题id") @RequestPart int problemId,
            @ApiParam("sha256校验码") @RequestPart String sha256,
            @ApiParam("测试点zip压缩文件") @RequestPart MultipartFile file) throws Exception {
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

    @ApiOperation("添加题目限制")
    @PostMapping("/addProblemLimit")
    public ResultEntity addProblemLimit(@ApiParam("问题id") @RequestParam Integer problemId, int time, int memory, int text) {
        if (problemId == null) {
            return ResultEntity.error("problemId is null");
        }
        ProblemLimit limit = new ProblemLimit(problemId, time, memory, text);
        problemService.addProblemLimit(limit);
        return ResultEntity.success();

    }

}
