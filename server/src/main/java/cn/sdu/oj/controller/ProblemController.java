package cn.sdu.oj.controller;

import cn.sdu.oj.domain.dto.ProblemDto;
import cn.sdu.oj.domain.po.*;
import cn.sdu.oj.domain.po.Tag;
import cn.sdu.oj.domain.vo.DifficultyEnum;
import cn.sdu.oj.domain.vo.ProblemTypeEnum;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.exception.TagNotExistException;
import cn.sdu.oj.service.ProblemService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/problem")
@Api(value = "问题接口", tags = "问题接口")
public class ProblemController {
    @Autowired
    private ProblemService problemService;

    @ApiOperation("支持的难度")
    @GetMapping("allDifficulties")
    public ResultEntity<DifficultyEnum[]> allDifficulties() {
        return ResultEntity.data(DifficultyEnum.values());
    }

    @ApiOperation("支持的题目种类")
    @GetMapping("allTypes")
    public ResultEntity<ProblemTypeEnum[]> allTypes() {
        return ResultEntity.data(ProblemTypeEnum.values());
    }

    @ApiOperation("题目详细信息|TEACHER+")
    @PostMapping("info")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<ProblemDto> problemInfo(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目id") @RequestParam int problemId) {
        return problemService.findProblemInfo(problemId, user.getId());
    }

    @ApiOperation("添加编程题目|TEACHER+")
    @PostMapping("/addProgramingProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Integer> addProgramingProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目名字") @RequestParam String name,
            @ApiParam("题目表述,题干") @RequestParam String description,
            @ApiParam("题目结果样例") @RequestParam(required = false) String example,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam(value = "标签Id数组(JSON数组)", defaultValue = "[1,2]") @RequestParam String tags) {
        AsyncProblem asyncProblem = new AsyncProblem();
        asyncProblem.setName(name);
        asyncProblem.setDescription(description);
        asyncProblem.setExample(example);
        asyncProblem.setDifficulty(difficulty);
        asyncProblem.setCreator(user.getId());
        try {
            return problemService.addProgramingProblem(asyncProblem, JSON.parseArray(tags, Integer.class));
        } catch (TagNotExistException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不是JSON数组");
        }
    }

    @ApiOperation("更新编程题目|TEACHER+")
    @PostMapping("/updateProgramingProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Boolean> updateProgramingProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目Id") @RequestParam Integer problemId,
            @ApiParam("题目名字") @RequestParam String name,
            @ApiParam("题目表述,题干") @RequestParam String description,
            @ApiParam("例子") @RequestParam(required = false) String example,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam(value = "标签Id数组(JSON数组)", example = "[1,2]") @RequestParam String tags) {
        AsyncProblem asyncProblem = new AsyncProblem();
        asyncProblem.setName(name);
        asyncProblem.setDescription(description);
        asyncProblem.setExample(example);
        asyncProblem.setDifficulty(difficulty);
        asyncProblem.setCreator(user.getId());
        try {
            return problemService.updateProgramingProblem(problemId, asyncProblem, JSON.parseArray(tags, Integer.class));
        } catch (TagNotExistException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不是JSON数组");
        }
    }

    @ApiOperation("更新编程题目限制信息|TEACHER+")
    @PostMapping("/updateLimit")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Boolean> updateProblemLimit(
            @ApiParam("题目id") @RequestParam Integer problemId,
            @ApiParam("限制时间ms") @RequestParam Integer timeLimit,
            @ApiParam("限制内存kb") @RequestParam Integer memoryLimit,
            @ApiParam("代码长度byte") @RequestParam Integer codeLengthLimit) {
        ProblemLimit problemLimit = new ProblemLimit();
        problemLimit.setProblemId(problemId);
        problemLimit.setTime(timeLimit);
        problemLimit.setMemory(memoryLimit);
        problemLimit.setCodeLength(codeLengthLimit);
        return problemService.updateProblemLimit(problemLimit);
    }

    @ApiOperation("更新编程题目测试点|TEACHER+")
    @PostMapping("/uploadCheckpoints")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Boolean> uploadCheckpoints(
            @ApiParam("题目id") @RequestPart Integer problemId,
            @ApiParam("测试点sha256校验码") @RequestPart String sha256,
            @ApiParam("测试点压缩包,格式按照之前拟定") @RequestPart MultipartFile file,
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            return problemService.uploadCheckpoints(problemId, user.getId(), file, sha256);
        } catch (Exception e) {
            return ResultEntity.data(StatusCode.COMMON_FAIL, false);
        }
    }

    @ApiOperation("下载题目测试点|TEACHER+")
    @GetMapping("downloadCheckpoints")
    @PreAuthorize("hasRole('TEACHER')")
    public void downloadCheckpoints(
            @ApiParam("题目id") @RequestParam Integer problemId,
            @ApiIgnore HttpServletResponse response,
            @ApiIgnore @AuthenticationPrincipal User user) {
        problemService.downloadCheckpoints(problemId, user.getId(), response);
    }

    @ApiOperation("添加选择/判断/填空/简答|TEACHER+")
    @PostMapping("/addTypeProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Integer> addTypeProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目内容") @RequestParam String name,
            @ApiParam("题目描述") @RequestParam(required = false) String description,
            @ApiParam(value = "题目答案(简答题需提供参考答案,需要均以jsonArray形式给出)", example = "[\"A\"]") @RequestParam String answer,
            @ApiParam(value = "选项(选择题必须有)", example = "{\"A\":\"1\",\"B\": \"未知\"}") @RequestParam(required = false) String options,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam("种类(单选/多选/判断/填空/简答)") @RequestParam int type,
            @ApiParam(value = "标签Id数组(JSON数组)", example = "[1,2]") @RequestParam String tags) {
        try {
            List<String> answerList = JSON.parseArray(answer, String.class);
            if (type == ProblemTypeEnum.PROGRAMING.id) {
                return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "编程题请通过专有添加");
            } else if (type == ProblemTypeEnum.SELECTION.id) {
                if (options == null || answerList.isEmpty()) {
                    return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "选择题选项与答案不准为空");
                }
            } else if (type == ProblemTypeEnum.MULTIPLE_SELECTION.id) {
                if (options == null || answerList.isEmpty()) {
                    return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "选择题选项与答案不准为空");
                }
            } else if (type == ProblemTypeEnum.JUDGEMENT.id) {
                if (answerList.size() != 1) {
                    return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "判断题必须有且仅有一份答案");
                }
            }
            if (JSON.parseArray(answer).size() == 0) {
                return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "选项格式不合法");
            }
        } catch (Exception e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "答案格式错误或选项格式不合法");
        }

        SyncProblem syncProblem = new SyncProblem();
        syncProblem.setName(name);
        syncProblem.setType(type);
        syncProblem.setDescription(description);
        syncProblem.setDifficulty(difficulty);
        syncProblem.setCreator(user.getId());
        syncProblem.setAnswer(answer);
        syncProblem.setOptions(options);
        try {
            return problemService.addOtherProblem(syncProblem, JSON.parseArray(tags, Integer.class));
        } catch (TagNotExistException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不是JSON数组");
        }
    }

    @ApiOperation("更新非编程题目|TEACHER+")
    @PostMapping("/updateTypeProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Boolean> updateOtherProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目Id") @RequestParam Integer problemId,
            @ApiParam("题目内容") @RequestParam String name,
            @ApiParam("题目描述") @RequestParam(required = false) String description,
            @ApiParam(value = "题目答案(简答题需提供参考答案,需要均以jsonArray形式给出)", example = "[\"A\"]") @RequestParam String answer,
            @ApiParam(value = "选项(选择题必须有)", example = "{\"A\":\"1\",\"B\": \"未知\"}") @RequestParam(required = false) String options,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam("种类(单选/多选/判断/填空/简答)") @RequestParam int type,
            @ApiParam(value = "标签Id数组(JSON数组)", example = "[1,2]") @RequestParam String tags) {
        try {
            List<String> answerList = JSON.parseArray(answer, String.class);
            if (type == ProblemTypeEnum.PROGRAMING.id) {
                return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "编程题请通过专有添加");
            } else if (type == ProblemTypeEnum.SELECTION.id) {
                if (options == null || answerList.isEmpty()) {
                    return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "选择题选项与答案不准为空");
                }
            } else if (type == ProblemTypeEnum.MULTIPLE_SELECTION.id) {
                if (options == null || answerList.isEmpty()) {
                    return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "选择题选项与答案不准为空");
                }
            } else if (type == ProblemTypeEnum.JUDGEMENT.id) {
                if (answerList.size() != 1) {
                    return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "判断题必须有且仅有一份答案");
                }
            }
        } catch (Exception e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "答案格式错误");
        }
        SyncProblem syncProblem = new SyncProblem();
        syncProblem.setName(name);
        syncProblem.setDescription(description);
        syncProblem.setDifficulty(difficulty);
        syncProblem.setCreator(user.getId());
        syncProblem.setType(type);
        syncProblem.setOptions(options);
        try {
            return problemService.updateOtherProblem(problemId, syncProblem, JSON.parseArray(tags, Integer.class));
        } catch (TagNotExistException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不是JSON数组");
        }
    }

    @ApiOperation("删除题目|TEACHER+")
    @PostMapping("/deleteProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Boolean> deleteProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目Id") @RequestParam Integer problemId) {
        return problemService.deleteProblem(problemId, user.getId());
    }

    @ApiOperation("所有的标签")
    @GetMapping("/allTags")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity<List<Tag>> allTags() {
        return problemService.allTags();
    }

    @PostMapping("/getTopLevelTag")
    @ApiOperation("获取顶级标签")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity<List<Tag>> getTopLevelTag() {
        List<Tag> topLevelTag = problemService.getTopLevelTag();
        return ResultEntity.data(topLevelTag);
    }

    @PostMapping("/getChildrenTagByParentTagId")
    @ApiOperation("根据父标签id获取子标签")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity<List<Tag>> getChildrenTagByParentTagId(@ApiParam("父标签的id") @RequestParam int parentTagId) {
        List<Tag> childrenList = problemService.getChildrenTagByParentId(parentTagId);
        for (Tag a : childrenList) {
            Integer id = a.getId();
            List<Tag> c = problemService.getChildrenTagByParentId(id);
            a.setHasChild(c != null && c.size() != 0);
        }
        return ResultEntity.data(childrenList);
    }


}
