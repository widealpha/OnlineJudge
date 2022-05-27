package cn.sdu.oj.controller;

import cn.sdu.oj.domain.po.ProblemSet;
import cn.sdu.oj.domain.po.ProblemSetProblem;
import cn.sdu.oj.domain.vo.ProblemSetProblemVo;
import cn.sdu.oj.domain.vo.ProblemSetTypeEnum;
import cn.sdu.oj.domain.vo.ProblemSetVo;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.service.ProblemSetService;
import cn.sdu.oj.service.UserGroupService;
import cn.sdu.oj.util.TimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/problemSet")
@Api(value = "题目集接口", tags = "题目集接口")
public class ProblemSetController {                  // TODO 权限
    //TODO  统计
    @Autowired
    private ProblemSetService problemSetService;

    @Autowired
    private UserGroupService userGroupService;

    @ApiOperation("所有支持的题目集类型")
    @GetMapping("/allTypes")
    public ResultEntity<ProblemSetTypeEnum[]> allProblemSetTypes() {
        return ResultEntity.data(ProblemSetTypeEnum.values());
    }

    @ApiOperation("题目集创建|TEACHER+") //创建题目集   //老师或者管理员可以使用
    @PostMapping("/createProblemSet")
    public ResultEntity<Integer> createProblemSet(
            @ApiParam(value = "名称") @RequestParam String name,
            @ApiParam(value = "类型") @RequestParam Integer type,
            @ApiParam(value = "简介") @RequestParam String introduction,
            @ApiParam(value = "是否公开") @RequestParam Integer isPublic,
            @ApiParam(value = "开始时间", example = "2022-05-26 22:00:00") @RequestParam String beginTime,
            @ApiParam(value = "结束时间", example = "2022-06-30 22:00:00") @RequestParam String endTime,
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            int[] types = {1, 2, 3};
            boolean type_valid = false;
            for (int a : types) {
                if (type == a) {
                    type_valid = true;
                    break;
                }
            }
            if (type_valid) {
                Integer id = problemSetService.createProblemSet(name, type, introduction, isPublic, beginTime, endTime, user.getId());
                if (id != null) {
                    return ResultEntity.data(id);
                } else return ResultEntity.error(StatusCode.COMMON_FAIL);
            } else return ResultEntity.error(StatusCode.PARAM_NOT_VALID);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    @ApiOperation("向题目集里加一个题") //向题目集里加一个题   //创建者可以使用
    @PostMapping("/addProblemToProblemSet")
    public ResultEntity<Boolean> addProblemToProblemSet(
            @ApiParam(value = "题目集ID") @RequestParam Integer problemSetId,
            @ApiParam(value = "题目ID") @RequestParam Integer problemId,
            @ApiIgnore @AuthenticationPrincipal User user
    ) {
        try {
            if (problemSetService.getProblemSetInfo(problemSetId).getCreatorId().equals(user.getId())) {
                if (problemSetService.judgeProblemSetHasProblem(problemSetId, problemId)) {
                    return ResultEntity.error("题目集已有该题");
                } else {
                    problemSetService.addProblemToProblemSet(problemId, problemSetId);
                    return ResultEntity.success();
                }
            } else return ResultEntity.error(StatusCode.NO_PERMISSION);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    @ApiOperation("查看公开题目集") //查看公开题目集   //所有人可以使用
    @PostMapping("/getPublicProblemSet")
    public ResultEntity getPublicProblemSet() {
        try {
            List<ProblemSet> problemSets = problemSetService.getPublicProblemSet();
            return ResultEntity.success("公开题目集", problemSets);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    @ApiOperation("查看我做过的题目集") //查看我做过的题目集   //所有人可以使用
    @PostMapping("/getSelfDoneProblemSet")
    public ResultEntity getSelfDoneProblemSet(@ApiIgnore @AuthenticationPrincipal User user) {
        try {
            List<ProblemSet> problemSets = problemSetService.getSelfDoneProblemSet(user.getId());

            return ResultEntity.success("我做过的题目集", problemSets);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }


    @ApiOperation("查看一个题目集的信息和题号") //查看一个题目集的信息   管理员，题目集创建者和题目集参与者可用
    @PostMapping("/getProblemSetInfo")
    public ResultEntity getProblemSetInfo(
            @ApiParam(value = "题目集ID") @RequestParam Integer problem_set_id,
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            //TODO 管理员，题目集创建者和题目集参与者可用

            ProblemSet problemSet = problemSetService.getProblemSetInfo(problem_set_id);  //获取题目集信息
            List<ProblemSetProblem> problems = problemSetService.getProblemSetProblems(problemSet.getId());
            ProblemSetVo problemSetVo = new ProblemSetVo(problemSet, problems);

            return ResultEntity.success("查看一个题目集的信息和题号", problemSetVo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    @ApiOperation("查看我创建的题目集") //查看我创建的题目集   管理员，题目集创建者可用
    @PostMapping("/getSelfCreatedProblemSet")
    public ResultEntity getSelfCreatedProblemSet(@ApiIgnore @AuthenticationPrincipal User user) {
        try {
            List<ProblemSet> problemSets = problemSetService.getSelfCreatedProblemSet(user.getId());

            return ResultEntity.success("查看我创建的题目集", problemSets);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }


    @ApiOperation("修改题目集") //修改题目集   管理员，题目集创建者可用  TODO
    @PostMapping("/alterProblemSetInfo")
    public ResultEntity alterProblemSetInfo(
            @ApiParam(value = "ID") @RequestParam Integer id,
            @ApiParam(value = "名称") @RequestParam String name,
            //   @ApiParam(value = "类型") @RequestParam String type,  类型不允许修改
            @ApiParam(value = "简介") @RequestParam String introduction,
            @ApiParam(value = "是否公开") @RequestParam Integer isPublic,
            @ApiParam(value = "开始时间", example = "2022-05-26 22:00:00") @RequestParam String beginTime,
            @ApiParam(value = "结束时间", example = "2022-06-30 22:00:00") @RequestParam String endTime,
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            ProblemSet problemSet = problemSetService.getProblemSetInfo(id);
            if (!user.getId().equals(problemSet.getCreatorId())) {
                return ResultEntity.error(StatusCode.NO_PERMISSION);
            } else {
                if (name != null && name != "") {
                    problemSet.setName(name);
                }
                if (introduction != null && introduction != "") {
                    problemSet.setIntroduction(introduction);
                }
                if (isPublic != null) {
                    problemSet.setIsPublic(isPublic);
                }
                if (beginTime != null && beginTime != "") {
                    problemSet.setBeginTime(TimeUtil.stringLongToDate(beginTime));
                }
                if (endTime != null && endTime != "") {
                    problemSet.setEndTime(TimeUtil.stringLongToDate(endTime));
                }
                if (problemSetService.alterProblemSetInfo(problemSet))
                    return ResultEntity.success("修改题目集成功");
                else return ResultEntity.error(StatusCode.COMMON_FAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    //获取我做过的某一题目集的完成情况
    @ApiOperation("获取我做过的某题目集的完成情况") //获取我做过的某一题目集的完成情况   所有人可用  TODO
    @PostMapping("/getSelfCompletion")
    public ResultEntity getSelfCompletion(
            @ApiParam(value = "ID", required = true) @RequestParam Integer id, //题目集id
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            int i = 0;
            List<ProblemSet> problemSets = problemSetService.getSelfDoneProblemSet(user.getId());

            for (ProblemSet p : problemSets) {
                if (p.getId().equals(id)) {
                    i++;
                    List<ProblemSetProblem> problemSetProblems = problemSetService.getProblemSetProblems(id);
                    List<ProblemSetProblemVo> problemSetProblemVos = problemSetService.getSelfCompletion(problemSetProblems, user.getId());
                    return ResultEntity.success("完成情况", problemSetProblemVos);
                }
            }
            if (i == 0) {
                return ResultEntity.error("没有做过该题目集");
            }
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    //题目集创建者获取所有人的完成情况
    // geng done all except competition

    @ApiOperation("题目集创建者获取所有人的完成情况") //题目集创建者获取所有人的完成情况   创建人可用  TODO
    @PostMapping("/getALLCompletion")
    public ResultEntity getALLCompletion(
            @ApiParam(value = "ID", required = true) @RequestParam Integer id, //题目集id
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            //获取用户组所有人的id

            //对于每个人进行操作
            int i = 0;
            List<ProblemSet> problemSets = problemSetService.getSelfDoneProblemSet(user.getId());

            for (ProblemSet p : problemSets) {
                if (p.getId().equals(id)) {
                    i++;
                    List<ProblemSetProblem> problemSetProblems = problemSetService.getProblemSetProblems(id);
                    List<ProblemSetProblemVo> problemSetProblemVos = problemSetService.getSelfCompletion(problemSetProblems, user.getId());
                    return ResultEntity.success("完成情况", problemSetProblemVos);
                }
            }
            if (i == 0) {
                return ResultEntity.error("没有做过该题目集");
            }
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    @ApiOperation("用户为一个题目集保存答案") //用户为一个题目集保存答案   答题人可用
    @PostMapping("/saveProblemAnswer")
    public ResultEntity saveProblemAnswer(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam(value = "ID", required = true) @RequestParam Integer id, //题目集id
            @ApiParam(value = "answer", required = true) @RequestParam String answer //题目集答案
    ) {
        try {
            //判断是否是答题人
            if (problemSetService.getUserCanTrySolveProblemSet(user.getId(), id)) {
                //查询是否提交过
                Boolean is_submit =problemSetService.judgeProblemSetSubmit(user.getId(), id);
             if (is_submit){  //提交过
                return ResultEntity.error("已经提交过了");
             } else {//如果没有，保存答案到联系表中
                 //判断是否存在
                Boolean is_exist = problemSetService.judgeProblemSetUserAnswerExist(user.getId(),id);
                    //不存在 插入
                 if (!is_exist){
                     problemSetService.insertProblemSetUserAnswer(user.getId(),id,answer);
                     return ResultEntity.success("第一次保存");
                 }
                    //存在，更新
                 else {
                     problemSetService.updateProblemSetUserAnswer(user.getId(),id,answer);
                     return ResultEntity.success("更新保存");
                 }
             }
                //否则返回
            } else return ResultEntity.error("不能答题");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

//    @ApiOperation("用户为一个题目集提交答案") //用户为一个题目集保存答案   答题人可用
//    @PostMapping("/saveProblemAnswer")
//    public ResultEntity saveProblemAnswer(
//            @ApiIgnore @AuthenticationPrincipal User user) {
//        try {
//            //判断是否是答题人
//
//            //保存答案到联系表中
//
//            return ResultEntity.success("查看我创建的题目集", problemSets);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResultEntity.error(StatusCode.COMMON_FAIL);
//        }
//    }


}