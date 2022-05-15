package cn.sdu.oj.service;

import cn.sdu.oj.dao.ProblemSetMapper;
import cn.sdu.oj.dao.UserGroupMapper;
import cn.sdu.oj.domain.po.ProblemSet;
import cn.sdu.oj.domain.po.UserGroup;
import cn.sdu.oj.util.TimeUtil;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
    public class ProblemSetService {
        @Autowired
        private ProblemSetMapper problemSetMapper;

        //新建题目集
        public Integer createProblemSet(String name, String type, String introduction, Integer isPublic,String beginTime,String endTime, Integer creatorId) throws Exception {
            ProblemSet problemSet = new ProblemSet();
            problemSet.setName(name);
            problemSet.setType(type);
            problemSet.setIntroduction(introduction);
            problemSet.setIfPublic(isPublic);
            problemSet.setBeginTime(TimeUtil.stringLongToDate(beginTime));
            problemSet.setEndTime(TimeUtil.stringLongToDate(endTime));

            problemSet.setCreatorId(creatorId);
            if (problemSetMapper.createProblemSet(problemSet)){
                return problemSet.getId();
            } else return null;

        }

        public List<ProblemSet> getPublicProblemSet() {
            return problemSetMapper.getPublicProblemSet();
        }

    public JSONArray getSelfDoneProblemSet(Integer id) {
            return null;
    }
}
