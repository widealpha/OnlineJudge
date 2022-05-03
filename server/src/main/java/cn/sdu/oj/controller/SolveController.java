package cn.sdu.oj.controller;

import cn.sdu.oj.domain.bo.JudgeTask;
import cn.sdu.oj.domain.bo.LanguageEnum;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.service.SolveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/solve")
@Api(value = "判题接口", tags = "判题接口")
public class SolveController {
    @Resource
    private SolveService solveService;

    @ApiOperation("提交判题代码")
    @PostMapping("trySolveProblem")
    @PreAuthorize("hasRole('COMMON')")
    ResultEntity trySolveProblem(@AuthenticationPrincipal User user,
                                 @ApiParam("题目Id") @RequestParam int problemId,
                                 @ApiParam("判题题目代码") @RequestParam String code,
                                 @ApiParam("判题语言") @RequestParam String language) {
        JudgeTask judgeTask = new JudgeTask();
        judgeTask.setProblemId(problemId);
        judgeTask.setCode(code);
        judgeTask.setLanguage(LanguageEnum.valueOf(language));
        return solveService.trySolveProblem(judgeTask, user.getId());
    }
}
