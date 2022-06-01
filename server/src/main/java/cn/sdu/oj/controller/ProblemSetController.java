package cn.sdu.oj.controller;

import cn.sdu.oj.domain.bo.JudgeTask;
import cn.sdu.oj.domain.bo.LanguageEnum;
import cn.sdu.oj.domain.dto.ProblemDto;
import cn.sdu.oj.domain.po.ProblemSet;
import cn.sdu.oj.domain.po.ProblemSetProblem;
import cn.sdu.oj.domain.vo.*;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.service.ProblemService;
import cn.sdu.oj.service.ProblemSetService;
import cn.sdu.oj.service.SolveService;
import cn.sdu.oj.service.UserGroupService;
import cn.sdu.oj.util.RedisUtil;
import cn.sdu.oj.util.StringUtil;
import cn.sdu.oj.util.TimeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/problemSet")
@Api(value = "题目集接口", tags = "题目集接口")
public class ProblemSetController {

    //TODO  统计

    @Autowired
    private ProblemSetService problemSetService;

    @Autowired
    private SolveService solveService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    RedisUtil redisUtil;

    @ApiOperation("所有支持的题目集类型")
    @GetMapping("/allTypes")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity<ProblemSetTypeEnum[]> allProblemSetTypes() {
        return ResultEntity.data(ProblemSetTypeEnum.values());
    }

    @ApiOperation("所有竞赛题目集的竞赛类型")
    @GetMapping("/allCompetitionTypes")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity<CompetitionType[]> allCompetitionTypes() {
        return ResultEntity.data(CompetitionType.values());
    }

    @ApiOperation("练习题目集创建|TEACHER+") //创建练习题目集   //老师或者管理员可以使用
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/createExerciseProblemSet")
    public ResultEntity<Integer> createExerciseProblemSet(
            @ApiParam(value = "名称") @RequestParam(required = true) String name,
            // @ApiParam(value = "类型") @RequestParam(required = true) Integer type,
            @ApiParam(value = "简介") @RequestParam(required = true) String introduction,
            @ApiParam(value = "是否公开") @RequestParam(required = true) Integer isPublic,
            @ApiParam(value = "开始时间", example = "2022-05-26 22:00:00") @RequestParam String beginTime,
            @ApiParam(value = "结束时间", example = "2022-06-30 22:00:00") @RequestParam String endTime,
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {

            Integer id = problemSetService.createProblemSet(name, 1, introduction, isPublic, beginTime, endTime, user.getId(), null);
            if (id != null) {
                return ResultEntity.data(id);
            } else return ResultEntity.error(StatusCode.COMMON_FAIL);

        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    @ApiOperation("测验题目集创建|TEACHER+") //创建题目集   //老师或者管理员可以使用
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/createTestProblemSet")
    public ResultEntity<Integer> createTestProblemSet(
            @ApiParam(value = "名称") @RequestParam(required = true) String name,

            @ApiParam(value = "简介") @RequestParam(required = true) String introduction,

            @ApiParam(value = "开始时间", example = "2022-05-26 22:00:00") @RequestParam String beginTime,
            @ApiParam(value = "结束时间", example = "2022-06-30 22:00:00") @RequestParam String endTime,
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            Integer id = problemSetService.createProblemSet(name, 2, introduction, -1, beginTime, endTime, user.getId(), null);
            if (id != null) {
                return ResultEntity.data(id);
            } else return ResultEntity.error(StatusCode.COMMON_FAIL);

        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    @ApiOperation("竞赛题目集创建|TEACHER+") //创建题目集   //老师或者管理员可以使用
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/createCompetitionProblemSet")
    public ResultEntity createCompetitionProblemSet(
            @ApiParam(value = "名称") @RequestParam(required = true) String name,

            @ApiParam(value = "简介") @RequestParam(required = true) String introduction,
            @ApiParam(value = "参赛队伍数") @RequestParam(required = true) Integer teamNum,

            @ApiParam(value = "开始时间", example = "2022-05-26 22:00:00") @RequestParam String beginTime,
            @ApiParam(value = "结束时间", example = "2022-06-30 22:00:00") @RequestParam String endTime,
            @ApiParam(value = "竞赛类型，0 ACM,1 IO,3 IOI") @RequestParam Integer competitionType,
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            Integer problem_set_id = problemSetService.createProblemSet(name, 3, introduction, -1, beginTime, endTime, user.getId(), competitionType);
            if (problem_set_id != null) {
                Integer user_group_id =
                        userGroupService.createUserGroup
                                (name + "参赛人员", "3", introduction, null, user.getId());
                if (user_group_id != null) {
                    userGroupService.linkUserGroupProblemSet(user_group_id, problem_set_id);
                    List<String> users = new ArrayList<>();  //返回n个账号 TODO
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userGroupId", user_group_id);
                    jsonObject.put("problemSetId", problem_set_id);
                    jsonObject.put("users", users);
                    return ResultEntity.data(jsonObject);
                } else return ResultEntity.error(StatusCode.COMMON_FAIL);

            } else return ResultEntity.error(StatusCode.COMMON_FAIL);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    @ApiOperation("向题目集里加一个题|TEACHER+") //向题目集里加一个题   //创建者可以使用
    @PostMapping("/addProblemToProblemSet")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity<Boolean> addProblemToProblemSet(
            @ApiParam(value = "题目集ID") @RequestParam Integer problemSetId,
            @ApiParam(value = "题目ID") @RequestParam Integer problemId,
            @ApiIgnore @AuthenticationPrincipal User user
    ) {
        try {
            //创建者
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

    @ApiOperation("查看公开题目集|COMMON+") //查看公开题目集   //所有人可以使用
    @PostMapping("/getPublicProblemSet")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity getPublicProblemSet() {
        try {
            List<ProblemSet> problemSets = problemSetService.getPublicProblemSet();
            return ResultEntity.success("公开题目集", problemSets);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    @ApiOperation("查看我做过的题目集|COMMON+") //查看我做过的题目集   //所有人可以使用
    @PostMapping("/getSelfDoneProblemSet")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity getSelfDoneProblemSet(@ApiIgnore @AuthenticationPrincipal User user) {
        try {
            List<ProblemSet> problemSets = problemSetService.getSelfDoneProblemSet(user.getId());

            return ResultEntity.success("我做过的题目集", problemSets);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }


    @ApiOperation("查看一个题目集的信息和题号|COMMON+") //查看一个题目集的信息   管理员，题目集创建者和题目集参与者可用
    @PostMapping("/getProblemSetInfo")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity getProblemSetInfo(
            @ApiParam(value = "题目集ID") @RequestParam(required = true) Integer problemSetId,
            @ApiParam(value = "克隆码（可选）") @RequestParam(required = false) String cloneCode,
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {

            if (problemSetService.getProblemSetInfo(problemSetId).getCreatorId().equals(user.getId())) {

                ProblemSet problemSet = problemSetService.getProblemSetInfo(problemSetId);  //获取题目集信息
                List<ProblemSetProblem> problems = problemSetService.getProblemSetProblems(problemSet.getId());
                List<ProblemDto> problemDtos = new ArrayList<>();
                for (ProblemSetProblem p : problems
                ) {
                    ResultEntity<ProblemDto> problemDtoResultEntity = problemService.findProblemInfo(p.getProblemId(), user.getId());
                    problemDtos.add(problemDtoResultEntity.getData());
                }
                ProblemSetVo problemSetVo = new ProblemSetVo(problemSet, problemDtos);
                return ResultEntity.success("查看一个题目集的信息和题号", problemSetVo);

            } else if (cloneCode != null) {
                String key = "cloneCode:" + problemSetId;
                if (redisUtil.hasKey(key)) {
                    String code = redisUtil.get(key);
                    if (code.equals(cloneCode)) {
                        ProblemSet problemSet = problemSetService.getProblemSetInfo(problemSetId);  //获取题目集信息
                        List<ProblemSetProblem> problems = problemSetService.getProblemSetProblems(problemSet.getId());
                        List<ProblemDto> problemDtos = new ArrayList<>();
                        for (ProblemSetProblem p : problems
                        ) {
                            ResultEntity<ProblemDto> problemDtoResultEntity = problemService.findProblemInfo(p.getProblemId(), user.getId());
                            problemDtos.add(problemDtoResultEntity.getData());
                        }
                        ProblemSetVo problemSetVo = new ProblemSetVo(problemSet, problemDtos);
                        return ResultEntity.success("查看一个题目集的信息和题号", problemSetVo);
                    } else return ResultEntity.error("无效的克隆码");
                } else return ResultEntity.error("题目集未被分享");
            } else {  //判断是否是参与者
                if (problemSetService.getUserCanTrySolveProblemSet(user.getId(), problemSetId)) {
                    ProblemSet problemSet = problemSetService.getProblemSetInfo(problemSetId);  //获取题目集信息
                    List<ProblemSetProblem> problems = problemSetService.getProblemSetProblems(problemSet.getId());
                    List<ProblemDto> problemDtos = new ArrayList<>();
                    for (ProblemSetProblem p : problems
                    ) {
                        ResultEntity<ProblemDto> problemDtoResultEntity = problemService.findProblemInfo(p.getProblemId(), user.getId());
                        problemDtos.add(problemDtoResultEntity.getData());
                    }
                    ProblemSetVo problemSetVo = new ProblemSetVo(problemSet, problemDtos);
                    return ResultEntity.success("查看一个题目集的信息和题号", problemSetVo);
                }
            }
            return ResultEntity.error(StatusCode.NO_PERMISSION);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    @ApiOperation("查看一个题在题目集中的正确率|COMMON+") //查看一个题的正确率   管理员，题目集创建者和题目集参与者可用
    @PostMapping("/getProblemPassRate")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity getProblemPassRate(
            @ApiParam(value = "题目ID") @RequestParam(required = true) Integer problemId,
            @ApiParam(value = "题目集ID") @RequestParam(required = false) Integer problemSetId,
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            double rate = 0;
            if (problemSetService.getProblemSetInfo(problemSetId).getCreatorId().equals(user.getId()) ||
                    problemSetService.getUserCanTrySolveProblemSet(user.getId(), problemSetId)
            ) {
                rate = problemSetService.getProblemPassRate(problemId, problemSetId);
                return ResultEntity.success("查询成功", rate);
            } else
                return ResultEntity.error(StatusCode.NO_PERMISSION);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    @ApiOperation("查看我创建的题目集|COMMON+") //查看我创建的题目集   管理员，题目集创建者可用
    @PostMapping("/getSelfCreatedProblemSet")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity getSelfCreatedProblemSet(@ApiIgnore @AuthenticationPrincipal User user) {
        try {

            List<ProblemSet> problemSets = problemSetService.getSelfCreatedProblemSet(user.getId());

            return ResultEntity.success("查看我创建的题目集", problemSets);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }


    @ApiOperation("修改题目集|COMMON+") //修改题目集   管理员，题目集创建者可用
    @PostMapping("/alterProblemSetInfo")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity alterProblemSetInfo(
            @ApiParam(value = "ID") @RequestParam(required = true) Integer id,
            @ApiParam(value = "名称") @RequestParam(required = true) String name,
            //   @ApiParam(value = "类型") @RequestParam String type,  类型不允许修改
            @ApiParam(value = "简介") @RequestParam(required = true) String introduction,
            @ApiParam(value = "是否公开") @RequestParam(required = true) Integer isPublic,
            @ApiParam(value = "开始时间", example = "2022-05-26 22:00:00") @RequestParam(required = true) String beginTime,
            @ApiParam(value = "结束时间", example = "2022-06-30 22:00:00") @RequestParam(required = true) String endTime,
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

    //获取某题目集某题的完成情况  学生看自己 老师看学生
    @ApiOperation("获取某题目集某题的完成情况|COMMON+") //获取我做过的某题目集某题的完成情况   所有人可用
    @PostMapping("/getSelfCompletion")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity getSelfCompletion(
            @ApiParam(value = "题目集id", required = true) @RequestParam Integer problemSetId, //题目集id
            @ApiParam(value = "题目id", required = true) @RequestParam Integer problemId, //题目id
            @ApiParam(value = "用户ID,若指定则是老师想看这个人的", required = false) @RequestParam Integer userId, //用户id
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            if (userId != null) {  //老师想看
                if (problemSetService.getProblemSetInfo(problemSetId).getCreatorId().equals(user.getId())) {
                    ProblemSetProblemVo problemSetProblemVo = problemSetService.getSelfCompletion(problemSetId, problemId, userId);
                    return ResultEntity.success("完成情况", problemSetProblemVo);
                } else return ResultEntity.error(StatusCode.NO_PERMISSION);

            } else //学生想看
            {
                ProblemSetProblemVo problemSetProblemVo = problemSetService.getSelfCompletion(problemSetId, problemId, user.getId());
                return ResultEntity.success("完成情况", problemSetProblemVo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    //获取某题目集的完成情况  学生看自己 老师看学生
    @ApiOperation("获取某题目集的完成情况|COMMON+") //获取我做过的某题目集某题的完成情况   所有人可用
    @PostMapping("/getSelfCompletionForProbelmSet")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity getSelfCompletionForProbelmSet(
            @ApiParam(value = "题目集id", required = true) @RequestParam Integer problemSetId, //题目集id

            @ApiParam(value = "用户ID,若指定则是老师想看这个人的", required = false) @RequestParam Integer userId, //用户id
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            ProblemSet problemSet = problemSetService.getProblemSetInfo(problemSetId);
            if (problemSet.getType() == 3) {
                return ResultEntity.error("无法查看竞赛题目集");
            }
            if (userId != null) {  //老师想看

                if (problemSet.getCreatorId().equals(user.getId())) {

                    List<ProblemSetProblem> problems = problemSetService.getProblemSetProblems(problemSetId);
                    List<ProblemSetProblemVo> problemSetProblemVos = new ArrayList<>();
                    for (ProblemSetProblem p : problems
                    ) {
                        Integer problem_id = p.getProblemId();
                        ProblemSetProblemVo problemSetProblemVo = problemSetService.getSelfCompletion(problemSetId, problem_id, userId);
                        problemSetProblemVos.add(problemSetProblemVo);
                    }
                    return ResultEntity.success("完成情况", problemSetProblemVos);
                } else return ResultEntity.error(StatusCode.NO_PERMISSION);

            } else //学生想看
            {

                List<ProblemSetProblem> problems = problemSetService.getProblemSetProblems(problemSetId);
                List<ProblemSetProblemVo> problemSetProblemVos = new ArrayList<>();
                for (ProblemSetProblem p : problems
                ) {
                    Integer problem_id = p.getProblemId();
                    ProblemSetProblemVo problemSetProblemVo = problemSetService.getSelfCompletion(problemSetId, problem_id, user.getId());
                    problemSetProblemVos.add(problemSetProblemVo);
                }
                return ResultEntity.success("完成情况", problemSetProblemVos);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    //竞赛查看自己的作答
    @ApiOperation("获取某题目集的完成情况|COMMON+") //获取我做过的某题目集某题的完成情况   所有人可用
    @PostMapping("/getSelfCompletionForCompetition")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity getSelfCompletionForCompetition(
            @ApiParam(value = "题目集id", required = true) @RequestParam Integer problemSetId, //题目集id
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            //TODO 判断是否是参赛者
            ProblemSet problemSet = problemSetService.getProblemSetInfo(problemSetId);
            if (problemSet.getType() == 3) {

                Integer type = problemSet.getCompetitionType();
                if (type == 1) {  //OI 赛制不允许比赛中看到
                    if (problemSet.getEndTime().getTime() > new Date().getTime()) {
                        return ResultEntity.error("比赛还未结束");
                    } else {
                        List<ProblemSetProblem> problems = problemSetService.getProblemSetProblems(problemSetId);
                        List<ProblemSetProblemVo> problemSetProblemVos = new ArrayList<>();
                        for (ProblemSetProblem p : problems
                        ) {
                            Integer problem_id = p.getProblemId();
                            ProblemSetProblemVo problemSetProblemVo = problemSetService.getSelfCompletion(problemSetId, problem_id, user.getId());
                            problemSetProblemVos.add(problemSetProblemVo);
                        }
                        return ResultEntity.success("完成情况", problemSetProblemVos);
                    }

                } else {  //其它 赛制允许比赛中看到
                    List<ProblemSetProblem> problems = problemSetService.getProblemSetProblems(problemSetId);
                    List<ProblemSetProblemVo> problemSetProblemVos = new ArrayList<>();
                    for (ProblemSetProblem p : problems
                    ) {
                        Integer problem_id = p.getProblemId();
                        ProblemSetProblemVo problemSetProblemVo = problemSetService.getSelfCompletion(problemSetId, problem_id, user.getId());
                        problemSetProblemVos.add(problemSetProblemVo);
                    }
                    return ResultEntity.success("完成情况", problemSetProblemVos);

                }

            } else return ResultEntity.error("非竞赛题目集");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    //
    //竞赛查看排名
    @ApiOperation("竞赛查看排名|COMMON+") //竞赛查看排名   所有人可用
    @PostMapping("/getCompetitionRank")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity getCompetitionRank(
            @ApiParam(value = "题目集id", required = true) @RequestParam Integer problemSetId, //题目集id
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            ProblemSet problemSet = problemSetService.getProblemSetInfo(problemSetId);
            if (problemSet.getType() == 3) {

                //TODO 判断是否是参赛者
                if (!problemSetService.getUserCanTrySolveProblemSet(user.getId(), problemSetId)) {
                    return ResultEntity.error(StatusCode.NO_PERMISSION);
                } else {
                    //获取用户组列表
                    List<Integer> user_group_id_list = userGroupService.getSelfJoinedUserGroup(user.getId());
                    List<Integer> members = userGroupService.getUserGroupMembers(user_group_id_list.get(0));
                    //获取题目列表
                    List<ProblemSetProblem> problems = problemSetService.getProblemSetProblems(problemSetId);
                    //存放id和分数
                    int[] users = new int[members.size()];
                    double[] scores = new double[members.size()];
                    int i = 0;
                    //对于每一个用户 区分竞赛类型计算排名
                    for (Integer a_member : members
                    ) {
                        //设置一个初始分数
                        double score = 0;
                        Integer type = problemSet.getCompetitionType();
                        List<ProblemSetProblemVo> problemSetProblemVos = new ArrayList<>();
                        for (ProblemSetProblem p : problems   //获取每一题的解答情况
                        ) {
                            Integer problem_id = p.getProblemId();
                            //解答情况
                            ProblemSetProblemVo problemSetProblemVo = problemSetService.getSelfCompletion(problemSetId, problem_id, a_member);
                            problemSetProblemVos.add(problemSetProblemVo);
                            //累计分数，不同比赛类型有不同的累计规则
                            if (type == 0) {  //ACM
                                Integer total_correct = problemSetProblemVo.getSolveRecord().getTotalCorrect();
                                Integer check_point_size = problemSetProblemVo.getSolveRecord().getCheckpointSize();

                                if (total_correct > 0 && total_correct == check_point_size) {  //全有
                                    score += problemSetProblemVo.getScore(); //记分
                                }

                            } else if (type == 1) {  //OI 赛制不允许比赛中看到排名
                                if (problemSet.getEndTime().getTime() > new Date().getTime()) {
                                    return ResultEntity.error("比赛还未结束");
                                } else {
                                    //按点给分
                                    Integer total_correct = problemSetProblemVo.getSolveRecord().getTotalCorrect();
                                    Integer check_point_size = problemSetProblemVo.getSolveRecord().getCheckpointSize();
                                    if (total_correct > 0) {
                                        score += problemSetProblemVo.getScore() * total_correct / check_point_size;
                                    }
                                }

                            } else   //IOI
                            {   //按点给分
                                Integer total_correct = problemSetProblemVo.getSolveRecord().getTotalCorrect();
                                Integer check_point_size = problemSetProblemVo.getSolveRecord().getCheckpointSize();
                                if (total_correct > 0) {
                                    score += problemSetProblemVo.getScore() * total_correct / check_point_size;
                                }
                            }
                        }
                        //获取答题时间
                        long last_commit_time =
                                problemSetService.getLastCommit(problemSetId, user.getId()).getCreateTime().getTime();
                        //对于ACM的分数进行特殊处理,加上罚时
                        if (type == 0) {
                            Integer punishRecord = problemSetService.getPunishRecord(problemSetId, user.getId());
                            last_commit_time += punishRecord * 1000 * 60;
                        }
                        long rest_time = problemSet.getEndTime().getTime() - last_commit_time;
                        //剩一分钟算一分
                        score += (double) (rest_time / 60000);
                        //将用户，分数放到数据结构里
                        users[i] = user.getId();
                        scores[i] = score;
                        i++;
                    }
                    //进行排序返回
                    QuickSort(users,scores,0,members.size()-1);
                    JSONArray jsonArray = new JSONArray();
                    for (int j = 0; j < members.size()-1; j++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId",users[i]);
                        jsonObject.put("rank",i);
                        jsonObject.put("score",scores[i]);
                        jsonArray.add(jsonObject);
                    }

                    return ResultEntity.success("排名情况", jsonArray);

                }
            } else return ResultEntity.error("非竞赛题目集");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    //重写快速排序：同时将键值进行排序
    public static void QuickSort(int arr[],double arr1[],int _left,int _right){

        int right=_right;
        int left=_left;
        int temp=0;double temp1=0;
        if(left<=right){
            temp1=arr1[left];
            temp=arr[left];
            while(left!=right){
                while(right>left&&arr[right]>temp)
                    right--;
                arr1[left]=arr1[right];
                arr[left]=arr[right];
                while(left<right&&temp>arr[left])
                    left++;
                arr1[right]=arr1[left];
                arr[right]=arr[left];
            }
            arr1[right]=temp1;
            arr[right]=temp;
            QuickSort(arr,arr1,_left,left-1);
            QuickSort(arr,arr1,right+1,_right);
        }

    }


    @ApiOperation("用户为一个题目集保存答案|COMMON+") //用户为一个题目集保存答案   答题人可用
    @PostMapping("/saveProblemAnswer")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity saveProblemAnswer(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam(value = "ID", required = true) @RequestParam Integer id, //题目集id
            @ApiParam(value = "answer", required = true, example = "[\n" +
                    "{\"problem_id\":1,\"type\":1,\"answer\":\"{\"code\":\"*****\",\"language\":\"Java\"}\"},\n" +
                    "{\"problem_id\":2,\"type\":2,\"answer\":\"A\"}\n" +
                    "]") @RequestParam String answer //题目集答案
    ) {
        try {
            //判断是否是答题人
            if (problemSetService.getUserCanTrySolveProblemSet(user.getId(), id)) {
                //查询是否提交过
                Boolean is_submit = problemSetService.judgeProblemSetSubmit(user.getId(), id);
                if (is_submit) {  //提交过
                    return ResultEntity.error("已经提交过了");
                } else {//如果没有，保存答案到联系表中
                    //判断是否存在
                    Boolean is_exist = problemSetService.judgeProblemSetUserAnswerExist(user.getId(), id);
                    //不存在 插入
                    if (!is_exist) {
                        problemSetService.insertProblemSetUserAnswer(user.getId(), id, answer);
                        return ResultEntity.success("第一次保存");
                    }
                    //存在，更新
                    else {
                        problemSetService.updateProblemSetUserAnswer(user.getId(), id, answer);
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

    @ApiOperation("用户为一个题目集提交答案|COMMON+") //用户为一个题目集保存答案   答题人可用
    @PostMapping("/submitProblemAnswer")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity submitProblemAnswer(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam(value = "ID", required = true) @RequestParam Integer id //题目集id
    ) {
        try {
            //判断是否是答题人
            if (problemSetService.getUserCanTrySolveProblemSet(user.getId(), id)) {
                //查询时间
                if (problemSetService.getProblemSetInfo(id).getEndTime().getTime() >= new Date().getTime()) {
                    //查询是否提交过
                    Boolean is_submit = problemSetService.judgeProblemSetSubmit(user.getId(), id);
                    if (is_submit) {  //提交过
                        return ResultEntity.error("已经提交过了");
                    } else {//如果没有，提交答案到判题
                        //拿到答案
                        String answers = problemSetService.getProblemSetUserAnswer(user.getId(), id);
                        if (answers != null) {
                            JSONArray jsonArray = JSONArray.parseArray(answers);
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Integer problem_id = jsonObject.getInteger("problem_id");
                                Integer type = jsonObject.getInteger("type");
                                String answer = jsonObject.getString("answer");
                                if (type == 0)//编程题
                                {
                                    JSONObject a = JSON.parseObject(answer);
                                    JudgeTask judgeTask = new JudgeTask();
                                    judgeTask.setProblemId(problem_id);
                                    judgeTask.setCode(a.getString("code"));
                                    judgeTask.setLanguage(LanguageEnum.valueOf(a.getString("language")));
                                    if (judgeTask.getLanguage() == null) {
                                        return ResultEntity.error(StatusCode.LANGUAGE_NOT_SUPPORT);
                                    }
                                    solveService.trySolveProblem(judgeTask, user.getId(), id);

                                } else {
                                    solveService.trySolveSyncProblem(problem_id, user.getId(), id, answer);
                                }
                            }
                            return ResultEntity.success("提交成功，正在判题");
                        } else return ResultEntity.error("空的答卷");
                    }
                } else return ResultEntity.error("已经结束，无法提交");
                //否则返回
            } else return ResultEntity.error("不能答题");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    //克隆题目集

    @ApiOperation("克隆题目集|TEACHER+") //获取题目集的克隆码 老师可用
    @PostMapping("/cloneProblemSet")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity cloneProblemSet(
            @ApiParam(value = "ID", required = true) @RequestParam Integer problemSetId, //题目集id
            @ApiParam(value = "克隆码", required = true) @RequestParam String cloneCode,
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            if (cloneCode != null) {
                String key = "cloneCode:" + problemSetId;
                if (redisUtil.hasKey(key)) {
                    String code = redisUtil.get(key);
                    if (code.equals(cloneCode)) {

                        ProblemSet problemSet = problemSetService.getProblemSetInfo(problemSetId);  //获取题目集信息
                        Integer new_problem_set_id = problemSetService.createProblemSet(problemSet.getName(),
                                problemSet.getType(), problemSet.getIntroduction(), problemSet.getIsPublic(),
                                TimeUtil.dateToStringLong(problemSet.getBeginTime()),
                                TimeUtil.dateToStringLong(problemSet.getEndTime()), user.getId(), null);

                        List<ProblemSetProblem> problems = problemSetService.getProblemSetProblems(problemSet.getId());
                        for (ProblemSetProblem p : problems
                        ) {
                            try {
                                ResultEntity<Integer> new_problem_id = problemService.cloneProblem(p.getProblemId(), user.getId());
                                problemSetService.addProblemToProblemSet(new_problem_id.getData(), new_problem_set_id);
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }
                        }
                        return ResultEntity.success("克隆成功", new_problem_set_id);
                    } else return ResultEntity.error("无效的克隆码");
                } else return ResultEntity.error("题目集未被分享");
            } else return ResultEntity.error(StatusCode.PARAM_EMPTY);

        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }


    //生成克隆码
    @ApiOperation("生成克隆码|TEACHER+") //获取题目集的克隆码 创建者可用
    @PostMapping("/getProblemSetCloneCode")
    @PreAuthorize("hasRole('TEACHER')")
    public ResultEntity getProblemSetCloneCode(
            @ApiParam(value = "ID", required = true) @RequestParam Integer problemSetId, //题目集id
            @ApiParam(value = "有效期(给天数）", required = false) @RequestParam Integer day,
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            if (problemSetService.getProblemSetInfo(problemSetId).getCreatorId().equals(user.getId())) {
                String key = "cloneCode:" + problemSetId;

                if (redisUtil.hasKey(key)) {
                    String code = redisUtil.get(key);
                    return ResultEntity.data(code);
                } else {
                    String value = StringUtil.getRandomString(6);
                    Integer validDay = 7;
                    if (day != null) {
                        validDay = day;
                    }
                    redisUtil.setEx(key, value, validDay, TimeUnit.DAYS);
                    return ResultEntity.success("设置成功", value);
                }
            } else return ResultEntity.error(StatusCode.NO_PERMISSION);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

}