package cn.sdu.oj.controller;

import cn.sdu.oj.domain.bo.Problem;
import cn.sdu.oj.domain.bo.ProblemWithInfo;
import cn.sdu.oj.domain.dto.ProblemDto;
import cn.sdu.oj.domain.po.*;
import cn.sdu.oj.domain.po.Tag;
import cn.sdu.oj.domain.vo.DifficultyEnum;
import cn.sdu.oj.domain.vo.ProbelmInfoVo;
import cn.sdu.oj.domain.vo.ProblemTypeEnum;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.exception.TagNotExistException;
import cn.sdu.oj.service.ProblemService;
import cn.sdu.oj.util.FileUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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
            @ApiParam("例子") @RequestParam(required = false) String example,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam("标签Id数组(JSON数组)") @RequestParam String tags) {
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
        }
    }


    @ApiOperation("更新编程题目|TEACHER+")
    @PostMapping("/updateProgramingProblem")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Boolean> updateOtherProblem(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("题目Id") @RequestParam Integer problemId,
            @ApiParam("题目名字") @RequestParam String name,
            @ApiParam("题目表述,题干") @RequestParam String description,
            @ApiParam("例子") @RequestParam(required = false) String example,
            @ApiParam("难度") @RequestParam Integer difficulty,
            @ApiParam("标签Id数组(JSON数组)") @RequestParam String tags) {
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
            @ApiParam("测试点压缩包,格式按照之前拟定") @RequestParam MultipartFile file) {
        try {
            problemService.addTestPoints(problemId, file, sha256);
            return ResultEntity.data(true);
        } catch (Exception e) {
            return ResultEntity.data(StatusCode.COMMON_FAIL, false);
        }
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
        } catch (Exception e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "答案格式错误");
        }
        if (JSON.parseArray(answer).size() == 0) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "选项格式不合法");
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
        } catch (Exception e) {
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID, "标签不存在");
        }
    }

    @ApiOperation("更新非编程题目|TEACHER+")
    @PostMapping("/updateOtherProblem")
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



//    @ApiOperation("添加题目")
//    @PostMapping("/addProblem")
//    @PreAuthorize("hasRole('TEACHER')")
//    public ResultEntity addProblem(
//            @ApiParam("题目名字") @RequestParam String name,
//            @ApiParam("题目表述,题干") @RequestParam String description,
//            @ApiParam("例子") @RequestParam(required = false) String example,
//            @ApiParam("难度") @RequestParam(required = false) Integer difficulty,
//            @ApiParam("是否开放，0私有，1开放，默认私有") @RequestParam(required = false) Integer isOpen,
//            @ApiParam("提示") @RequestParam(required = false) String tip,
//            @ApiParam("标签，用下划线分割的一组id") @RequestParam(required = false) String tags,
//            @ApiParam("类型，0为编程题，否则为非编程题") @RequestParam Integer type,
//            @ApiParam("答案，对于非编程题") @RequestParam(required = false) String answer,
//            @ApiIgnore @AuthenticationPrincipal User user) {
//        // 处理参数
//        ProblemWithInfo info = new ProblemWithInfo(null, name, description, example, difficulty, isOpen, tip
//                , user.getId(), answer, tags, type);
//        problemService.addProblem(info);
//        return ResultEntity.data(info.getId());
//    }
//
//    @PostMapping("/deleteProblem")
//    @ApiOperation("删除问题")
//    @PreAuthorize("hasRole('TEACHER')")
//    public ResultEntity deleteProblem(@ApiParam("问题id") @RequestParam int id,
//                                      @ApiParam("0为编程，否则为其他") @RequestParam int type,
//                                      @ApiIgnore @AuthenticationPrincipal User user) {
//        problemService.deleteProblem(user.getId(), id, type);
//        return ResultEntity.success();
//    }
//
//    @ApiOperation("更新题目")
//    @PostMapping("/updateProblem")
//    @PreAuthorize("hasRole('TEACHER')")
//    public ResultEntity updateProblem(
//            @ApiParam(value = "题目的id") @RequestParam int id,
//            @ApiParam("题目名字") @RequestParam String name,
//            @ApiParam("题目表述,题干") @RequestParam String description,
//            @ApiParam("例子") @RequestParam(required = false) String example,
//            @ApiParam("难度") @RequestParam(required = false) Integer difficulty,
//            @ApiParam("是否开放，0私有，1开放，默认私有") @RequestParam(required = false) Integer isOpen,
//            @ApiParam("提示") @RequestParam(required = false) String tip,
//            @ApiParam("标签，用下划线分割的一组id") @RequestParam(required = false) String tags,
//            @ApiParam("类型，0为编程题，否则为非编程题") @RequestParam Integer type,
//            @ApiParam("答案，对于非编程题") @RequestParam(required = false) String answer,
//            @ApiIgnore @AuthenticationPrincipal User user
//    ) {
//        // 处理参数
//        // 处理参数
//        ProblemWithInfo info = new ProblemWithInfo(id, name, description, example, difficulty, isOpen, tip
//                , user.getId(), answer, tags, type);
//        problemService.updateProblem(info);
//        return ResultEntity.success();
//
//    }
//
//    @ApiOperation("添加测试点")
//    @PostMapping("/addTestPoints")
//    @PreAuthorize("hasRole('TEACHER')")
//    public ResultEntity addTestPoints(
//            @ApiParam("问题id") @RequestParam int problemId,
//            @ApiParam("sha256校验码") @RequestParam String sha256,
//            @ApiParam("测试点压缩包") @RequestParam MultipartFile file) throws Exception {
//        if (file == null || file.isEmpty()) {
//            return ResultEntity.error("file is null");
//        }
//        String verify = FileUtil.getSHA256(file.getBytes());
//        if (!verify.equals(sha256)) {
//            return ResultEntity.error("file is bad");
//        }
//        // 写文件
//        problemService.addTestPoints(problemId, file, sha256);
//        return ResultEntity.success();
//    }
//
//    @ApiOperation("添加题目限制")
//    @PostMapping("/addProblemLimit")
//    @PreAuthorize("hasRole('TEACHER')")
//    public ResultEntity addProblemLimit(
//            @ApiParam("问题id") @RequestParam Integer problemId,
//            @ApiParam("运行时间,单位ms") @RequestParam(required = false) int time,
//            @ApiParam("运行内存,单位KB") @RequestParam(required = false) int memory,
//            @ApiParam("代码长度") @RequestParam(required = false) int text) {
//
//        if (problemId == null) {
//            return ResultEntity.error("problemId is null");
//        }
//        ProblemLimit limit = new ProblemLimit(problemId, time, memory, text);
//        problemService.addProblemLimit(limit);
//        return ResultEntity.success();
//
//    }
//
//    @PostMapping("/getProblemAllInfoByProblemIdAndType")
//    @ApiOperation("获取题目详细信息")
//    @PreAuthorize("hasRole('COMMON')")
//    public ResultEntity getProblemAllInfoByProblemIdAndType(
//            @ApiParam("问题id") @RequestParam int problemId,
//            @ApiParam("问题类型") @RequestParam int type) {
//        Problem problem = problemService.getProblemByProblemIdAndType(problemId, type);
//        List<Tag> tagList = problemService.getTagListWithPrefixByProblemIdAndType(problemId, type);
//        UserInfo userInfo = problemService.getAuthorInfoByProblemIdAndType(problemId, type);
//        ProblemLimit limit = problemService.getProblemLimitByProblemId(problemId);
//        ProbelmInfoVo probelmInfoVo = new ProbelmInfoVo(problem, limit, userInfo, tagList);
//        return ResultEntity.data(probelmInfoVo);
//    }

//    @PostMapping("/updateProblemLimit")
//    @ApiOperation("修改问题运行限制")
//    @PreAuthorize("hasRole('TEACHER')")
//    public ResultEntity updateProblemLimit(
//            @ApiParam("问题id") @RequestParam Integer problemId,
//            @ApiParam("运行时间,单位ms") @RequestParam(required = false) int time,
//            @ApiParam("运行内存,单位KB") @RequestParam(required = false) int memory,
//            @ApiParam("代码长度") @RequestParam(required = false) int text) {
//        ProblemLimit limit = new ProblemLimit(problemId, time, memory, text);
//        problemService.updateProblemLimit(limit);
//        return ResultEntity.success();
//    }

    @PostMapping("/getTopLevelTag")
    @ApiOperation("获取顶级标签")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity getTopLevelTag() {
        List<Tag> topLevelTag = problemService.getTopLevelTag();
        return ResultEntity.data(topLevelTag);
    }

    @PostMapping("/getChildrenTagByParentTagId")
    @ApiOperation("根据父标签id获取子标签")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity getChildrenTagByParentTagId(@ApiParam("父标签的id") @RequestParam int parentTagId) {
        List<Tag> childrenList = problemService.getChildrenTagByParentId(parentTagId);
        for (Tag a:childrenList
             ) { Integer id  =a.getId();
            List<Tag> c = problemService.getChildrenTagByParentId(id);
            if (c!=null && c.size()!=0){
                a.setHasChild(true);
            } else  a.setHasChild(false);
        }
        return ResultEntity.data(childrenList);
    }


}
