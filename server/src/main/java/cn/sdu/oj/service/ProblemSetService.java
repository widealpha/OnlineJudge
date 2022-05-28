package cn.sdu.oj.service;

import cn.sdu.oj.dao.*;
import cn.sdu.oj.domain.po.*;
import cn.sdu.oj.domain.vo.ProblemSetProblemVo;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private ProblemSetUserGroupMapper problemSetUserGroupMapper;


    @Autowired
    private ProblemSetUserAnswerMapper problemSetUserAnswerMapper;

    //新建题目集
    public Integer createProblemSet(String name, Integer type, String introduction, Integer isPublic, String beginTime, String endTime, Integer creatorId) throws Exception {
        ProblemSet problemSet = new ProblemSet();
        problemSet.setName(name);
        problemSet.setType(type);
        problemSet.setIntroduction(introduction);
        problemSet.setIsPublic(isPublic);
        problemSet.setBeginTime(TimeUtil.stringLongToDate(beginTime));
        problemSet.setEndTime(TimeUtil.stringLongToDate(endTime));

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

    public List<ProblemSetProblemVo> getSelfCompletion(List<ProblemSetProblem> problemSetProblems, Integer user_id) {
        List<ProblemSetProblemVo> problemSetProblemVos = new ArrayList<>();
        for (ProblemSetProblem p : problemSetProblems) {
            if (p.getScore() == 0) // 编程题
            {
                Integer c = solveRecordMapper.getSelfCompletion(p.getProblemId(), p.getProblemSetId(), user_id);

                problemSetProblemVos.add(new ProblemSetProblemVo(p, c));
            } else if (p.getScore() != 1)//非编程题
            {
                Integer is_correct = answerRecordMapper.getSelfCompletion(p.getProblemId(), p.getProblemSetId(), p, user_id);
                problemSetProblemVos.add(new ProblemSetProblemVo(p, is_correct));
            }
        }
        return problemSetProblemVos;
    }

    //判断 一个用户是否能做这个题
    public ResultEntity<Boolean> getUserCanTrySolveProblem(Integer user_id, Integer problem_id, Integer problem_set_id) {

        List<ProblemSetProblem> problemSetProblem = problemSetProblemMapper.getProblemSetProblem(problem_set_id);
        for (ProblemSetProblem p : problemSetProblem
        ) {
            if (Objects.equals(p.getProblemId(), problem_id)) { //题目在题目集中
                ProblemSet problemSet = problemSetMapper.getProblemSetInfo(problem_set_id);
                if (problemSet.getIsPublic() == 1) {  //public
                    return ResultEntity.success("公开题目集", true);
                } else { // not public
                    List<ProblemSetUserGroup> problemSetUserGroups = problemSetUserGroupMapper.getUserGroupByProblemSet(problem_set_id);
                    for (ProblemSetUserGroup problemSetUserGroup : problemSetUserGroups
                    ) {
                        Integer user_group_id = problemSetUserGroup.getUser_group_id();
                        if (userGroupService.judgeUserGroupContainUser(user_id, user_group_id)) {
                            return ResultEntity.success("可以判题", true);
                        }
                    }
                    return ResultEntity.error("不可判题", false);
                }
            }
        }
        return ResultEntity.error("题目不在题目集中", false);
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
                Integer user_group_id = problemSetUserGroup.getUser_group_id();
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
}
