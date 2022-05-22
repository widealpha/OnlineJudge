package cn.sdu.oj.controller;

import cn.sdu.oj.domain.po.ProblemSet;
import cn.sdu.oj.domain.po.ProblemSetProblem;
import cn.sdu.oj.domain.vo.ProblemSetProblemVo;
import cn.sdu.oj.domain.vo.ProblemSetVo;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.service.ProblemSetService;
import cn.sdu.oj.service.UserGroupService;
import cn.sdu.oj.util.TimeUtil;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.el.lang.ELArithmetic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/problemSet")
@Api(value = "题目集接口", tags = "题目集接口")
public class ProblemSetController {                  // TODO 权限
    //TODO  统计
    @Autowired
    private ProblemSetService problemSetService;

    @ApiOperation("题目集创建") //创建题目集   //老师或者管理员可以使用
    @PostMapping("/createProblemSet")
    public ResultEntity createProblemSet(
            @ApiParam(value = "名称") @RequestParam String name,
            @ApiParam(value = "类型") @RequestParam String type,
            @ApiParam(value = "简介") @RequestParam String introduction,
            @ApiParam(value = "是否公开") @RequestParam Integer isPublic,
            @ApiParam(value = "开始时间") @RequestParam String beginTime,
            @ApiParam(value = "结束时间") @RequestParam String endTime,
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            String[] types = {"普通", "临时测验", "竞赛"};
            boolean type_valid = false;
            for (String a : types) {
                if (type.equals(a)) {
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
    public ResultEntity getProblemSetInfo(@ApiIgnore @AuthenticationPrincipal User user) {
        try {
            ProblemSet problemSet = problemSetService.getProblemSetInfo(user.getId());  //获取题目集信息
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
            @ApiParam(value = "开始时间") @RequestParam String beginTime,
            @ApiParam(value = "结束时间") @RequestParam String endTime,
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
            @ApiParam(value = "ID") @RequestParam Integer id, //题目集id
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            int i =0;
            List<ProblemSet> problemSets = problemSetService.getSelfDoneProblemSet(user.getId());

            for (ProblemSet p : problemSets) {
                if (p.getId().equals(id)) {
                    i++;
                    List<ProblemSetProblem> problemSetProblems = problemSetService.getProblemSetProblems(id);
                    List<ProblemSetProblemVo> problemSetProblemVos = problemSetService.getSelfCompletion(problemSetProblems, user.getId());
                    return ResultEntity.success("完成情况", problemSetProblemVos);
                }
            }
            if (i==0) {
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
            @ApiParam(value = "ID") @RequestParam Integer id, //题目集id
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            //获取用户组所有人的id

            //对于每个人进行操作
            int i =0;
            List<ProblemSet> problemSets = problemSetService.getSelfDoneProblemSet(user.getId());

            for (ProblemSet p : problemSets) {
                if (p.getId().equals(id)) {
                    i++;
                    List<ProblemSetProblem> problemSetProblems = problemSetService.getProblemSetProblems(id);
                    List<ProblemSetProblemVo> problemSetProblemVos = problemSetService.getSelfCompletion(problemSetProblems, user.getId());
                    return ResultEntity.success("完成情况", problemSetProblemVos);
                }
            }
            if (i==0) {
                return ResultEntity.error("没有做过该题目集");
            }
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }



}