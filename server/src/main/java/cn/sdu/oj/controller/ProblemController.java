package cn.sdu.oj.controller;

import cn.sdu.oj.domain.bo.Problem;
import cn.sdu.oj.domain.bo.ProblemWithInfo;
import cn.sdu.oj.domain.po.ProblemLimit;
import cn.sdu.oj.domain.po.Tag;
import cn.sdu.oj.domain.po.UserInfo;
import cn.sdu.oj.domain.vo.ProbelmInfoVo;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.service.ProblemService;
import cn.sdu.oj.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/problem")
@Api(value = "问题接口", tags = "问题接口")
public class ProblemController {
    @Autowired
    private ProblemService problemService;

    @ApiOperation("添加题目")
    @PostMapping("/addProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity addProblem(
            @ApiParam("题目名字") @RequestParam String name,
            @ApiParam("题目表述,题干") @RequestParam String description,
            @ApiParam("例子") String example,
            @ApiParam("难度") Integer difficulty,
            @ApiParam("是否开放，0私有，1开放，默认私有") Integer isOpen,
            @ApiParam("提示") String tip,
            @ApiParam("标签，用下划线分割的一组id") String tags,
            @ApiParam("类型，0为编程题，否则为非编程题") @RequestParam Integer type,
            @ApiParam("答案，对于非编程题") String answer,
            @ApiIgnore @AuthenticationPrincipal User user) {
        // 处理参数
        ProblemWithInfo info = new ProblemWithInfo(null, name, description, example, difficulty, isOpen, tip
                , user.getId(), answer, tags, type);
        problemService.addProblem(info);
        return ResultEntity.data(info.getId());
    }

    @PostMapping("/deleteProblem")
    @ApiOperation("删除问题")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity deleteProblem(@ApiParam("问题id") @RequestParam int id,
                                      @ApiParam("0为编程，否则为其他") @RequestParam int type,
                                      @ApiIgnore @AuthenticationPrincipal User user) {
        problemService.deleteProblem(user.getId(), id, type);
        return ResultEntity.success();
    }

    @ApiOperation("更新题目")
    @PostMapping("/updateProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity updateProblem(
            @ApiParam("题目的id") @RequestParam int id,
            @ApiParam("题目名字") @RequestParam String name,
            @ApiParam("题目表述,题干") @RequestParam String description,
            @ApiParam("例子") String example,
            @ApiParam("难度") Integer difficulty,
            @ApiParam("是否开放，0私有，1开放，默认私有") Integer isOpen,
            @ApiParam("提示") String tip,
            @ApiParam("标签，用下划线分割的一组id") String tags,
            @ApiParam("类型，0为编程题，否则为非编程题") @RequestParam Integer type,
            @ApiParam("答案，对于非编程题") String answer,
            @ApiIgnore @AuthenticationPrincipal User user
    ) {
        // 处理参数
        // 处理参数
        ProblemWithInfo info = new ProblemWithInfo(id, name, description, example, difficulty, isOpen, tip
                , user.getId(), answer, tags, type);
        problemService.updateProblem(info);
        return ResultEntity.success();

    }

    @ApiOperation("添加测试点")
    @PostMapping("/addTestPoints")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity addTestPoints(
            @ApiParam("问题id") @RequestParam int problemId,
            @ApiParam("sha256校验码") @RequestParam String sha256,
            @ApiParam("测试点压缩包") @RequestParam MultipartFile file) throws Exception {
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
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity addProblemLimit(
            @ApiParam("问题id") @RequestParam Integer problemId,
            @ApiParam("运行时间") int time,
            @ApiParam("运行内存") int memory,
            @ApiParam("代码长度") int text) {

        if (problemId == null) {
            return ResultEntity.error("problemId is null");
        }
        ProblemLimit limit = new ProblemLimit(problemId, time, memory, text);
        problemService.addProblemLimit(limit);
        return ResultEntity.success();

    }

    @PostMapping("/getProblemAllInfoByProblemIdAndType")
    @ApiOperation("获取题目详细信息")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity getProblemAllInfoByProblemIdAndType(
            @ApiParam("问题id") @RequestParam int problemId,
            @ApiParam("问题类型") @RequestParam int type) {
        Problem problem = problemService.getProblemByProblemIdAndType(problemId, type);
        List<Tag> tagList = problemService.getTagListByProblemIdAndType(problemId, type);
        UserInfo userInfo = problemService.getAuthorInfoByProblemIdAndType(problemId, type);
        ProblemLimit limit = problemService.getProblemLimitByProblemId(problemId);
        ProbelmInfoVo probelmInfoVo = new ProbelmInfoVo(problem, limit, userInfo, tagList);
        return ResultEntity.data(probelmInfoVo);
    }

}
