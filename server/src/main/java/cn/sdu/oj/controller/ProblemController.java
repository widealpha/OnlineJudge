package cn.sdu.oj.controller;

import cn.sdu.oj.domain.bo.LanguageEnum;
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
import com.alibaba.fastjson.*;
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
import java.util.Objects;

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
            @ApiParam(value = "标签Id数组(JSON数组)", defaultValue = "[1,2]") @RequestParam String tags,
            @ApiParam(value = "支持的语言(JSON数组),默认所有", example = "[C,CPP]")
            @RequestParam(required = false) String supportLanguages) {
        AsyncProblem asyncProblem = new AsyncProblem();
        asyncProblem.setName(name);
        asyncProblem.setDescription(description);
        asyncProblem.setExample(example);
        asyncProblem.setDifficulty(difficulty);
        asyncProblem.setCreator(user.getId());
        try {
            if (supportLanguages == null || supportLanguages.isEmpty()) {
                asyncProblem.setSupportLanguages(JSON.toJSONString(LanguageEnum.values()));
            } else {
                JSONArray array = new JSONArray();
                for (String language : JSON.parseArray(supportLanguages, String.class)) {
                    array.add(LanguageEnum.valueOf(LanguageEnum.valueOf(language).name()));
                }
                if (array.isEmpty()) {
                    return ResultEntity.error(StatusCode.PARAM_EMPTY, "判题语言不可置空");
                }
                asyncProblem.setSupportLanguages(array.toJSONString());
            }
        } catch (Exception e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "支持的语言错误");
        }
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
            @ApiParam(value = "标签Id数组(JSON数组)", example = "[1,2]") @RequestParam String tags,
            @ApiParam(value = "支持的语言(JSON数组),默认所有", example = "[C,CPP]")
            @RequestParam(required = false) String supportLanguages) {
        AsyncProblem asyncProblem = new AsyncProblem();
        asyncProblem.setName(name);
        asyncProblem.setDescription(description);
        asyncProblem.setExample(example);
        asyncProblem.setDifficulty(difficulty);
        asyncProblem.setCreator(user.getId());
        try {
            if (supportLanguages == null) {
                asyncProblem.setSupportLanguages(JSON.toJSONString(LanguageEnum.values()));
            } else {
                JSONArray array = new JSONArray();
                for (String language : JSON.parseArray(supportLanguages, String.class)) {
                    array.add(LanguageEnum.valueOf(LanguageEnum.valueOf(language).name()));
                }
                if (array.isEmpty()) {
                    return ResultEntity.error(StatusCode.PARAM_EMPTY, "判题语言不可置空");
                }
                asyncProblem.setSupportLanguages(array.toJSONString());
            }
        } catch (Exception e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "支持的语言错误");
        }
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
            @ApiParam("题目id") @RequestParam Integer problemId,
            @ApiParam("测试点sha256校验码") @RequestParam String sha256,
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

    @ApiOperation("添加单项选择题|TEACHER+")
    @PostMapping("/addSelectionProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Integer> addSelectionProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目内容") @RequestParam String name,
            @ApiParam("题目描述") @RequestParam(required = false) String description,
            @ApiParam(value = "题目答案 jsonMap的key", example = "A") @RequestParam String answer,
            @ApiParam(value = "选项(jsonMap形式)", example = "{\"A\":\"1\",\"B\": \"未知\"}") @RequestParam String options,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam(value = "标签Id数组(JSON数组)", example = "[1,2]") @RequestParam String tags) {
        try {
            JSONObject object = JSONObject.parseObject(options);
            if (object.size() < 2) {
                return ResultEntity.error(StatusCode.PARAM_EMPTY, "选项不能少于2个");
            } else if (object.size() > 10) {
                return ResultEntity.error(StatusCode.PARAM_EMPTY, "选项不能超过10个");
            } else if (!object.containsKey(answer)) {
                return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "答案必须在选项中");
            }
            SyncProblem syncProblem = new SyncProblem();
            syncProblem.setName(name);
            syncProblem.setType(ProblemTypeEnum.SELECTION.id);
            syncProblem.setDescription(description);
            syncProblem.setDifficulty(difficulty);
            syncProblem.setCreator(user.getId());
            syncProblem.setAnswer(answer);
            syncProblem.setOptions(options);
            return problemService.addOtherProblem(syncProblem, JSON.parseArray(tags, Integer.class));
        } catch (TagNotExistException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "参数非合法JSON格式");
        }
    }

    @ApiOperation("添加多项选择题|TEACHER+")
    @PostMapping("/addMultiSelectionProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Integer> addMultiSelectionProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目内容") @RequestParam String name,
            @ApiParam("题目描述") @RequestParam(required = false) String description,
            @ApiParam(value = "题目答案(jsonArray形式,需满足单选条件)", example = "[\"A\",\"B\"]") @RequestParam String answer,
            @ApiParam(value = "选项(jsonMap形式)", example = "{\"A\":\"1\",\"B\": \"未知\"}") @RequestParam String options,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam(value = "标签Id数组(JSON数组)", example = "[1,2]") @RequestParam String tags) {
        try {
            JSONObject object = JSONObject.parseObject(options);
            if (object.size() < 2) {
                return ResultEntity.error(StatusCode.PARAM_EMPTY, "选项不能少于2个");
            } else if (object.size() > 10) {
                return ResultEntity.error(StatusCode.PARAM_EMPTY, "选项不能超过10个");
            }
            List<String> answers = JSON.parseArray(answer, String.class);
            for (String ans : answers) {
                if (!object.containsKey(ans)) {
                    return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "答案必须在选项中");
                }
            }
            SyncProblem syncProblem = new SyncProblem();
            syncProblem.setName(name);
            syncProblem.setType(ProblemTypeEnum.MULTIPLE_SELECTION.id);
            syncProblem.setDescription(description);
            syncProblem.setDifficulty(difficulty);
            syncProblem.setCreator(user.getId());
            syncProblem.setAnswer(answer);
            syncProblem.setOptions(options);
            return problemService.addOtherProblem(syncProblem, JSON.parseArray(tags, Integer.class));
        } catch (TagNotExistException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "参数非合法JSON格式");
        }
    }


    @ApiOperation("添加填空题|TEACHER+")
    @PostMapping("/addCompletionProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Integer> addCompletionProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目内容") @RequestParam String name,
            @ApiParam("题目描述") @RequestParam(required = false) String description,
            @ApiParam(value = "题目答案(jsonArray形式,每个空一个答案[\"1\",\"KB\"])") @RequestParam String answer,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam(value = "标签Id数组(JSON数组)", example = "[1,2]") @RequestParam String tags) {
        try {
            JSONObject object = JSONObject.parseObject(answer);
            if (object.isEmpty()) {
                return ResultEntity.error(StatusCode.PARAM_EMPTY, "答案不能为空");
            }
            SyncProblem syncProblem = new SyncProblem();
            syncProblem.setName(name);
            syncProblem.setType(ProblemTypeEnum.COMPLETION.id);
            syncProblem.setDescription(description);
            syncProblem.setDifficulty(difficulty);
            syncProblem.setCreator(user.getId());
            syncProblem.setAnswer(answer);
            return problemService.addOtherProblem(syncProblem, JSON.parseArray(tags, Integer.class));
        } catch (TagNotExistException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "参数非合法JSON格式");
        }
    }

    @ApiOperation("添加判断题|TEACHER+")
    @PostMapping("/addJudgementProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Integer> addJudgementProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目内容") @RequestParam String name,
            @ApiParam("题目描述") @RequestParam(required = false) String description,
            @ApiParam("题目答案(true/false)") @RequestParam boolean answer,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam(value = "标签Id数组(JSON数组)", example = "[1,2]") @RequestParam String tags) {
        try {
            SyncProblem syncProblem = new SyncProblem();
            syncProblem.setName(name);
            syncProblem.setType(ProblemTypeEnum.JUDGEMENT.id);
            syncProblem.setDescription(description);
            syncProblem.setDifficulty(difficulty);
            syncProblem.setCreator(user.getId());
            syncProblem.setAnswer(String.valueOf(answer));
            return problemService.addOtherProblem(syncProblem, JSON.parseArray(tags, Integer.class));
        } catch (TagNotExistException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "参数非合法JSON格式");
        }
    }

    @ApiOperation("添加简答题|TEACHER+")
    @PostMapping("/addShortAnswerProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Integer> addShortAnswerProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目内容") @RequestParam String name,
            @ApiParam("题目描述") @RequestParam(required = false) String description,
            @ApiParam(value = "题目答案") @RequestParam(required = false) String answer,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam(value = "标签Id数组(JSON数组)", example = "[1,2]") @RequestParam String tags) {
        try {
            SyncProblem syncProblem = new SyncProblem();
            syncProblem.setName(name);
            syncProblem.setType(ProblemTypeEnum.SHORT.id);
            syncProblem.setDescription(description);
            syncProblem.setDifficulty(difficulty);
            syncProblem.setCreator(user.getId());
            syncProblem.setAnswer(Objects.requireNonNullElse(answer, ""));
            return problemService.addOtherProblem(syncProblem, JSON.parseArray(tags, Integer.class));
        } catch (TagNotExistException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "参数非合法JSON格式");
        }
    }

    @ApiOperation("更新选择题|TEACHER+")
    @PostMapping("/updateSelectionProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Boolean> updateSelectionProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目id") @RequestParam int problemId,
            @ApiParam("题目内容") @RequestParam String name,
            @ApiParam("题目描述") @RequestParam(required = false) String description,
            @ApiParam(value = "题目答案 jsonMap的key", example = "A") @RequestParam String answer,
            @ApiParam(value = "选项(jsonMap形式)", example = "{\"A\":\"1\",\"B\": \"未知\"}") @RequestParam String options,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam(value = "标签Id数组(JSON数组)", example = "[1,2]") @RequestParam String tags) {
        try {
            JSONObject object = JSONObject.parseObject(options);
            if (object.size() < 2) {
                return ResultEntity.error(StatusCode.PARAM_EMPTY, "选项不能少于2个");
            } else if (object.size() > 10) {
                return ResultEntity.error(StatusCode.PARAM_EMPTY, "选项不能超过10个");
            } else if (!object.containsKey(answer)) {
                return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "答案必须在选项中");
            }
            SyncProblem syncProblem = new SyncProblem();
            syncProblem.setName(name);
            syncProblem.setType(ProblemTypeEnum.SELECTION.id);
            syncProblem.setDescription(description);
            syncProblem.setDifficulty(difficulty);
            syncProblem.setCreator(user.getId());
            syncProblem.setAnswer(answer);
            syncProblem.setOptions(options);
            return problemService.updateOtherProblem(problemId, syncProblem, JSON.parseArray(tags, Integer.class));
        } catch (TagNotExistException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "参数非合法JSON格式");
        }
    }

    @ApiOperation("更新多项选择题|TEACHER+")
    @PostMapping("/updateMultiSelectionProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Integer> updateMultiSelectionProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目id") @RequestParam int problemId,
            @ApiParam("题目内容") @RequestParam String name,
            @ApiParam("题目描述") @RequestParam(required = false) String description,
            @ApiParam(value = "题目答案(jsonArray形式,每个都要在options里)", example = "[\"A\",\"B\"]") @RequestParam String answer,
            @ApiParam(value = "选项(jsonMap形式)", example = "{\"A\":\"1\",\"B\": \"未知\"}") @RequestParam String options,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam(value = "标签Id数组(JSON数组)", example = "[1,2]") @RequestParam String tags) {
        try {
            JSONObject object = JSONObject.parseObject(options);
            if (object.size() < 2) {
                return ResultEntity.error(StatusCode.PARAM_EMPTY, "选项不能少于2个");
            } else if (object.size() > 10) {
                return ResultEntity.error(StatusCode.PARAM_EMPTY, "选项不能超过10个");
            }
            List<String> answers = JSON.parseArray(answer, String.class);
            for (String ans : answers) {
                if (!object.containsKey(ans)) {
                    return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "答案必须在选项中");
                }
            }
            SyncProblem syncProblem = new SyncProblem();
            syncProblem.setName(name);
            syncProblem.setType(ProblemTypeEnum.MULTIPLE_SELECTION.id);
            syncProblem.setDescription(description);
            syncProblem.setDifficulty(difficulty);
            syncProblem.setCreator(user.getId());
            syncProblem.setAnswer(answer);
            syncProblem.setOptions(options);
            return problemService.addOtherProblem(syncProblem, JSON.parseArray(tags, Integer.class));
        } catch (TagNotExistException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "参数非合法JSON格式");
        }
    }


    @ApiOperation("更新填空题|TEACHER+")
    @PostMapping("/updateCompletionProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Boolean> updateCompletionProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目id") @RequestParam int problemId,
            @ApiParam("题目内容") @RequestParam String name,
            @ApiParam("题目描述") @RequestParam(required = false) String description,
            @ApiParam(value = "题目答案(jsonArray形式,每个空一个答案[\"1\",\"KB\"])") @RequestParam String answer,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam(value = "标签Id数组(JSON数组)", example = "[1,2]") @RequestParam String tags) {
        try {
            JSONObject object = JSONObject.parseObject(answer);
            if (object.isEmpty()) {
                return ResultEntity.error(StatusCode.PARAM_EMPTY, "答案不能为空");
            }
            SyncProblem syncProblem = new SyncProblem();
            syncProblem.setName(name);
            syncProblem.setType(ProblemTypeEnum.COMPLETION.id);
            syncProblem.setDescription(description);
            syncProblem.setDifficulty(difficulty);
            syncProblem.setCreator(user.getId());
            syncProblem.setAnswer(answer);
            return problemService.updateOtherProblem(problemId, syncProblem, JSON.parseArray(tags, Integer.class));
        } catch (TagNotExistException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "参数非合法JSON格式");
        }
    }


    @ApiOperation("更新判断题|TEACHER+")
    @PostMapping("/updateJudgementProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Boolean> updateJudgementProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目id") @RequestParam int problemId,
            @ApiParam("题目内容") @RequestParam String name,
            @ApiParam("题目描述") @RequestParam(required = false) String description,
            @ApiParam("题目答案(true/false)") @RequestParam boolean answer,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam(value = "标签Id数组(JSON数组)", example = "[1,2]") @RequestParam String tags) {
        try {
            SyncProblem syncProblem = new SyncProblem();
            syncProblem.setName(name);
            syncProblem.setType(ProblemTypeEnum.JUDGEMENT.id);
            syncProblem.setDescription(description);
            syncProblem.setDifficulty(difficulty);
            syncProblem.setCreator(user.getId());
            syncProblem.setAnswer(String.valueOf(answer));
            return problemService.updateOtherProblem(problemId, syncProblem, JSON.parseArray(tags, Integer.class));
        } catch (TagNotExistException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "参数非合法JSON格式");
        }
    }


    @ApiOperation("更新简答题|TEACHER+")
    @PostMapping("/updateShortAnswerProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Boolean> updateShortAnswerProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目id") @RequestParam int problemId,
            @ApiParam("题目内容") @RequestParam String name,
            @ApiParam("题目描述") @RequestParam(required = false) String description,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam(value = "题目答案") @RequestParam(required = false) String answer,
            @ApiParam(value = "标签Id数组(JSON数组)", example = "[1,2]") @RequestParam String tags) {
        try {
            SyncProblem syncProblem = new SyncProblem();
            syncProblem.setName(name);
            syncProblem.setType(ProblemTypeEnum.SHORT.id);
            syncProblem.setDescription(description);
            syncProblem.setDifficulty(difficulty);
            syncProblem.setCreator(user.getId());
            syncProblem.setAnswer(Objects.requireNonNullElse(answer, ""));
            return problemService.updateOtherProblem(problemId, syncProblem, JSON.parseArray(tags, Integer.class));
        } catch (TagNotExistException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        } catch (JSONException e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "参数非合法JSON格式");
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

    @ApiOperation("用户出的所有题目|TEACHER+")
    @PostMapping("/allMyCreateProblems")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<List<ProblemDto>> allMyCreateProblems(
            @ApiIgnore @AuthenticationPrincipal User user
    ) {
        return problemService.allMyCreateProblems(user.getId());
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
