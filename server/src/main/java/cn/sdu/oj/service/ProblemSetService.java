package cn.sdu.oj.service;

import cn.sdu.oj.dao.*;
import cn.sdu.oj.domain.po.ProblemSet;
import cn.sdu.oj.domain.po.ProblemSetProblem;
import cn.sdu.oj.domain.po.UserGroup;
import cn.sdu.oj.domain.vo.ProblemSetProblemVo;
import cn.sdu.oj.util.TimeUtil;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    //新建题目集
    public Integer createProblemSet(String name, String type, String introduction, Integer isPublic, String beginTime, String endTime, Integer creatorId) throws Exception {
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
        List<ProblemSet> problemSets = null;
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
            if (p.getType() == 0) // 编程题
            {
                Integer c = solveRecordMapper.getSelfCompletion(p.getProblem_id(),p.getProblem_set_id(),user_id);;
                problemSetProblemVos.add(new ProblemSetProblemVo(p, c));
            } else if (p.getType() == 1)//非编程题
            {
                Integer is_correct = answerRecordMapper.getSelfCompletion(p.getProblem_id(),p.getProblem_set_id(),p,user_id);
                problemSetProblemVos.add(new ProblemSetProblemVo(p, is_correct));
            }
        }
        return problemSetProblemVos;
    }
}
