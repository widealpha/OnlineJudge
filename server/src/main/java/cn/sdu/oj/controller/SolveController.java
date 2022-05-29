package cn.sdu.oj.controller;

import cn.sdu.oj.domain.bo.JudgeTask;
import cn.sdu.oj.domain.bo.LanguageEnum;
import cn.sdu.oj.domain.dto.SolveResultDto;
import cn.sdu.oj.domain.po.AnswerRecord;
import cn.sdu.oj.domain.po.ProblemSet;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.service.ProblemSetService;
import cn.sdu.oj.service.SolveService;
import com.alibaba.fastjson.JSONException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Date;


@RestController
@RequestMapping("/solve")
@Api(value = "判题接口", tags = "判题接口")
public class SolveController {
    @Resource
    private SolveService solveService;
    @Resource
    private ProblemSetService problemSetService;

    @ApiOperation("提交判题代码")
    @PostMapping("trySolveProblem")
    @PreAuthorize("hasRole('COMMON')")
    ResultEntity<String> trySolveProblem(@ApiIgnore @AuthenticationPrincipal User user,
                                         @ApiParam("题目Id") @RequestParam int problemId,
                                         @ApiParam("题目集编号") @RequestParam int problemSetId,
                                         @ApiParam("判题题目代码") @RequestParam String code,
                                         @ApiParam("判题语言") @RequestParam String language) {
        ProblemSet problemSet = problemSetService.getProblemSetInfo(problemSetId);
        if (problemSet == null) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST, "题目集不存在");
        } else if (problemSet.getEndTime().getTime() < new Date().getTime()
                || problemSet.getBeginTime().getTime() > new Date().getTime())  {
            return ResultEntity.error("已过题目集截止时间", null);
        }
        JudgeTask judgeTask = new JudgeTask();
        judgeTask.setProblemId(problemId);
        judgeTask.setCode(code);
        judgeTask.setLanguage(LanguageEnum.valueOf(language));
        if (judgeTask.getLanguage() == null) {
            return ResultEntity.error(StatusCode.LANGUAGE_NOT_SUPPORT);
        }
        return solveService.trySolveProblem(judgeTask, user.getId(), problemSetId);
    }

    @ApiOperation("提交自定义测试代码")
    @PostMapping("runCodeTest")
    @PreAuthorize("hasRole('COMMON')")
    ResultEntity<String> runCodeTest(@ApiIgnore @AuthenticationPrincipal User user,
                                     @ApiParam("题目Id") @RequestParam int problemId,
                                     @ApiParam("题目集编号") @RequestParam int problemSetId,
                                     @ApiParam("题目代码") @RequestParam String code,
                                     @ApiParam("判题语言") @RequestParam String language,
                                     @ApiParam("自定义的测试点") @RequestParam String customInput) {
        ProblemSet problemSet = problemSetService.getProblemSetInfo(problemSetId);
        if (problemSet == null) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST, "题目集不存在");
        } else if (problemSet.getEndTime().getTime() < new Date().getTime()
                || problemSet.getBeginTime().getTime() > new Date().getTime()) {
            return ResultEntity.error("禁止在非作答时间提交测试", null);
        }
        JudgeTask judgeTask = new JudgeTask();
        judgeTask.setProblemId(problemId);
        judgeTask.setCode(code);
        judgeTask.setLanguage(LanguageEnum.valueOf(language));
        judgeTask.setTestMode(true);
        judgeTask.setTestInput(customInput);
        if (judgeTask.getLanguage() == null) {
            return ResultEntity.error(StatusCode.LANGUAGE_NOT_SUPPORT);
        }
        return solveService.runTestCode(judgeTask, user.getId(), problemSetId);
    }


    @ApiOperation("提交非编程题判题")
    @PostMapping("trySolveSyncProblem")
    @PreAuthorize("hasRole('COMMON')")
    ResultEntity<AnswerRecord> trySolveSyncProblem(@ApiIgnore @AuthenticationPrincipal User user,
                                                   @ApiParam("题目Id") @RequestParam int problemId,
                                                   @ApiParam("题目集编号") @RequestParam int problemSetId,
                                                   @ApiParam(value = "答案内容(JSON格式)", example = "[\"A\"]") @RequestParam String answer) {
        ProblemSet problemSet = problemSetService.getProblemSetInfo(problemSetId);
        if (problemSet == null) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST, "题目集不存在");
        }  else if (problemSet.getEndTime().getTime() < new Date().getTime()
                || problemSet.getBeginTime().getTime() > new Date().getTime()) {
            return ResultEntity.error("答卷已经截至", null);
        }
        try {
            return solveService.trySolveSyncProblem(problemId, user.getId(), problemSetId, answer);
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "非JSON标准格式");
        }
    }

    @ApiOperation("获取判题支持的语言")
    @GetMapping("supportLanguages")
    ResultEntity<String[]> supportLanguages() {
        return ResultEntity.data(new String[]{
                "JAVA8", "PYTHON3", "C++17", "C99"
        });
    }

    @ApiOperation("判题结果查询")
    @PostMapping("solveTaskResult")
    @PreAuthorize("hasRole('COMMON')")
    ResultEntity<SolveResultDto> solveResult(@ApiIgnore @AuthenticationPrincipal User user,
                                             @ApiParam("任务编号") @RequestParam int taskId) {
        return solveService.solveResult(taskId, user.getId());
    }
}
