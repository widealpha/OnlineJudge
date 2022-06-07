package cn.sdu.oj.service;

import cn.sdu.oj.dao.*;
import cn.sdu.oj.domain.po.*;
import cn.sdu.oj.domain.vo.ProblemSetProblemVo;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProblemSetService {
    @Autowired
    private ProblemSetMapper problemSetMapper;
    @Autowired
    private SolveRecordMapper solveRecordMapper;
    @Autowired
    private AnswerRecordMapper answerRecordMapper;
    @Autowired
    private ProblemSetProblemMapper problemSetProblemMapper;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private GeneralProblemMapper generalProblemMapper;

    @Autowired
    private ProblemSetUserGroupMapper problemSetUserGroupMapper;


    @Autowired
    private ProblemSetUserAnswerMapper problemSetUserAnswerMapper;

    //新建题目集
    public Integer createProblemSet(String name, Integer type, String introduction, Integer isPublic, String beginTime, String endTime, Integer creatorId, Integer competitionType) throws Exception {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setName(name);
        problemSet.setType(type);
        problemSet.setIntroduction(introduction);
        problemSet.setIsPublic(isPublic);
        problemSet.setBeginTime(TimeUtil.stringLongToDate(beginTime));
        problemSet.setEndTime(TimeUtil.stringLongToDate(endTime));

        problemSet.setCompetitionType(competitionType);

        problemSet.setCreatorId(creatorId);
        if (problemSetMapper.createProblemSet(problemSet)) {
            return problemSet.getId();
        } else return null;

    }

    public List<ProblemSet> getPublicProblemSet() {
        return problemSetMapper.getPublicProblemSet();
    }

    public List<ProblemSet> getSelfDoneProblemSet(Integer id) {
        //获取编程题的记录
        List<Integer> list1 = solveRecordMapper.selectSelfDoneProblemSetByUserId(id);
        //获取客观题的记录
        List<Integer> list2 = answerRecordMapper.selectSelfDoneProblemSetByUserId(id);
        for (Integer a : list2) {
            if (list1.contains(a)) {
                continue;
            } else {
                list1.add(a);
            }
        }
        List<ProblemSet> problemSets = new ArrayList<>();
        for (Integer b : list1) {
            problemSets.add(problemSetMapper.getProblemSetInfo(b));
        }
        return problemSets;
    }

    public ProblemSet getProblemSetInfo(Integer id) {
        return problemSetMapper.getProblemSetInfo(id);
    }

    public List<ProblemSet> getSelfCreatedProblemSet(Integer id) {
        return problemSetMapper.getSelfCreatedProblemSet(id);
    }

    public boolean alterProblemSetInfo(ProblemSet problemSet) {
        return problemSetMapper.alterProblemSetInfo(problemSet);
    }

    public List<ProblemSetProblem> getProblemSetProblems(Integer id) {
        return problemSetProblemMapper.getProblemSetProblem(id);
    }

    public ProblemSetProblemVo getSelfCompletion(Integer problemSetId, Integer problemId, Integer userId) {

        ProblemSetProblemVo problemSetProblemVo = new ProblemSetProblemVo();
        GeneralProblem generalProblem = generalProblemMapper.selectGeneralProblem(problemId);
        problemSetProblemVo.setType(generalProblem.getType());
        //这个题的总分
        Integer total_score = problemSetProblemMapper.getProblemSetProblemScore(problemSetId, problemId).getScore();

        if (generalProblem.getType() == 0) // 编程题
        {
            //拿到解题记录（最后一次）
            SolveRecord solveRecord = solveRecordMapper.getSelfCompletion(problemId, problemSetId, userId);
            problemSetProblemVo.setScore(total_score);
            problemSetProblemVo.setSolveRecord(solveRecord);

        } else if (generalProblem.getType() != 1)//非编程题 默认只有是否正确
        {
            //拿到解题记录（最后一次）
            AnswerRecord answerRecord = answerRecordMapper.getSelfCompletion(problemId, problemSetId, userId);
            problemSetProblemVo.setScore(total_score);
            problemSetProblemVo.setAnswerRecord(answerRecord);
        }

        return problemSetProblemVo;
    }

    //判断 一个用户是否能做这个题
    public ResultEntity<Boolean> getUserCanTrySolveProblem(Integer user_id, Integer problem_id, Integer problem_set_id) {
        if (!problemSetProblemMapper.existProblemSetProblem(problem_set_id, problem_id)) {
            return ResultEntity.error("题目不在题目集中", false);
        } else {
            ProblemSet problemSet = problemSetMapper.getProblemSetInfo(problem_set_id);
            if (problemSet.getIsPublic() == 1) {  //public
                return ResultEntity.success("公开题目集", true);
            } else { // not public
                List<ProblemSetUserGroup> problemSetUserGroups = problemSetUserGroupMapper.getUserGroupByProblemSet(problem_set_id);
                for (ProblemSetUserGroup problemSetUserGroup : problemSetUserGroups) {
                    Integer user_group_id = problemSetUserGroup.getUserGroupId();
                    if (userGroupService.judgeUserGroupContainUser(user_id, user_group_id)) {
                        return ResultEntity.success("可以判题", true);
                    }
                }
                return ResultEntity.error("不可判题", false);
            }
        }
    }

    //判断 一个用户是否能做这个题目集
    public boolean getUserCanTrySolveProblemSet(Integer user_id, Integer problem_set_id) {

        ProblemSet problemSet = problemSetMapper.getProblemSetInfo(problem_set_id);
        if (problemSet.getIsPublic() == 1) {  //public
            return true;
        } else { // not public
            List<ProblemSetUserGroup> problemSetUserGroups = problemSetUserGroupMapper.getUserGroupByProblemSet(problem_set_id);
            for (ProblemSetUserGroup problemSetUserGroup : problemSetUserGroups
            ) {
                Integer user_group_id = problemSetUserGroup.getUserGroupId();
                if (userGroupService.judgeUserGroupContainUser(user_id, user_group_id)) {
                    return false;
                }
            }
            return false;
        }
    }

    public Boolean judgeProblemSetSubmit(Integer user_id, Integer problem_set_id) {
        Integer submit = problemSetUserAnswerMapper.judgeProblemSetSubmit(user_id, problem_set_id);
        return submit != null && submit == 1;
    }

    //判断题目集里有没有这个题
    public Boolean judgeProblemSetHasProblem(Integer problem_set_id, Integer problem_id) {
        List<ProblemSetProblem> problems = problemSetProblemMapper.getProblemSetProblem(problem_set_id);
        for (ProblemSetProblem p : problems) {
            if (p.getProblemId().equals(problem_id)) {
                return true;
            }
        }
        return false;
    }

    public void addProblemToProblemSet(Integer problem_id, Integer problem_set_id) {
        problemSetProblemMapper.addProblemToProblemSet(problem_id, problem_set_id);
    }

    public Boolean judgeProblemSetUserAnswerExist(Integer user_id, Integer problem_set_id) {
        Integer i = problemSetUserAnswerMapper.judgeProblemSetUserAnswerExist(user_id, problem_set_id);
        if (i == null || i == 0) {
            return false;
        } else return true;

    }

    public void insertProblemSetUserAnswer(Integer user_id, Integer problem_set_id, String answer) {
        problemSetUserAnswerMapper.insertProblemSetUserAnswer(user_id, problem_set_id, answer);
    }

    public void updateProblemSetUserAnswer(Integer user_id, Integer problem_set_id, String answer) {
        problemSetUserAnswerMapper.updateProblemSetUserAnswer(user_id, problem_set_id, answer);
    }

    public String getProblemSetUserAnswer(Integer user_id, Integer problem_set_id) {
        return problemSetUserAnswerMapper.getProblemSetUserAnswer(user_id, problem_set_id);
    }

    public List<ProblemSetUserAnswer> getUncommittedProblemSetUserAnswer() {
        return problemSetUserAnswerMapper.getUncommittedProblemSetUserAnswer();
    }

    public double getProblemPassRate(Integer problem_id, Integer problem_set_id) {
        GeneralProblem generalProblem = generalProblemMapper.selectGeneralProblem(problem_id);
        //    if (problem_set_id == null) {

//            if (generalProblem.getType() == 0) // 编程题
//            {
//                Integer answer_num = solveRecordMapper.getProblemRecordNum(generalProblem.getTypeProblemId());
//                Integer correct_num = solveRecordMapper.getProblemRecordCorrectNum(generalProblem.getTypeProblemId());
//                double rate = (double) correct_num / answer_num;
//                return rate;
//
//            } else  //非编程题 默认只有是否正确
//            {
//                Integer answer_num = answerRecordMapper.getProblemRecordNum(generalProblem.getTypeProblemId());
//                Integer correct_num = answerRecordMapper.getProblemRecordCorrectNum(generalProblem.getTypeProblemId());
//                double rate = (double) correct_num / answer_num;
//                return rate;
//            }
//        } else {
        //指定在题目集中查找

        if (generalProblem.getType() == 0) // 编程题
        {
            Integer answer_num = solveRecordMapper.getProblemRecordNum(generalProblem.getTypeProblemId(), problem_set_id);
            Integer correct_num = solveRecordMapper.getProblemRecordCorrectNum(generalProblem.getTypeProblemId(), problem_set_id);
            double rate = (double) correct_num / answer_num;
            return rate;

        } else //非编程题 默认只有是否正确
        {
            Integer answer_num = answerRecordMapper.getProblemRecordNum(generalProblem.getTypeProblemId(), problem_set_id);
            Integer correct_num = answerRecordMapper.getProblemRecordCorrectNum(generalProblem.getTypeProblemId(), problem_set_id);
            double rate = (double) correct_num / answer_num;
            return rate;
        }

        //     }
    }

    public Integer getPunishRecord(Integer problemSetId, Integer a_member) {
        return solveRecordMapper.getPunishRecord(problemSetId, a_member);
    }

    public SolveRecord getLastCommit(Integer problemSetId, Integer id) {
        return solveRecordMapper.getLastCommit(problemSetId, id);
    }

    public void deleteProblemSet(Integer problemSetId) {
        problemSetMapper.deleteProblemSet(problemSetId);
    }

    public List<ProblemSet> selectPublicProblemSetByName(String name) {
        return problemSetMapper.selectPublicProblemSetByName(name);
    }


    //todo 做权限认证
    public ResultEntity<List<UserGroup>> problemSetUserGroups(int problemSetId, Integer id) {
        List<UserGroup> userGroupsIds = new ArrayList<>();
        for (ProblemSetUserGroup e : problemSetUserGroupMapper.getUserGroupByProblemSet(problemSetId)) {
            if (e.getUserGroupId() != null){
                userGroupsIds.add(userGroupService.getUserGroupInfoById(e.getUserGroupId()));
            }
        }
        return ResultEntity.data(userGroupsIds);
    }
}
