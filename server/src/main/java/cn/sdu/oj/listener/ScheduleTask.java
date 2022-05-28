package cn.sdu.oj.listener;


import cn.sdu.oj.domain.bo.JudgeTask;
import cn.sdu.oj.domain.bo.LanguageEnum;
import cn.sdu.oj.domain.po.ProblemSetUserAnswer;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.service.ProblemSetService;
import cn.sdu.oj.service.SolveService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.List;


/**
 * SpringBoot定时器任务的方式一：
 * 		使用注解的形式来创建定时器
 *
 */
@Configuration
@EnableScheduling // 开启定时器
public class ScheduleTask {

    @Autowired
    private ProblemSetService problemSetService;

    @Autowired
    private SolveService solveService;

    // 添加定时器任务(单位：ms)
    /**
     * fixedRate:按照一定的速率执行，是从上一次方法执行开始的时间算起，如果上一次方法阻塞住了，下一次也是不会执行，但是在阻塞这段时间内累计应该执行的次数，当不再阻塞时，一下子把这些全部执行掉，而后再按照固定速率继续执行。
     * fixedDelay:以上一次方法执行完开始算起，如上一次方法执行阻塞住了，那么直到上一次执行完，并间隔给定的时间后，执行下一次
     * initialDelay:容器启动后，延迟指定时间后再执行一次定时器
     * cron:（在附录中单独介绍）
     */
    @Scheduled(fixedRate = 60000)  // 每6s执行一次
    public void scheduleEvent() {
        //扫描数据库获取 未提交的记录
        List<ProblemSetUserAnswer> problemSetUserAnswers = problemSetService.getUncommittedProblemSetUserAnswer();
        for (ProblemSetUserAnswer p:problemSetUserAnswers
             ) { //判断是否需要提交
            Integer problem_set_id = p.getProblem_set_id();
            if (problemSetService.getProblemSetInfo(problem_set_id).getEndTime().getTime()<=new Date().getTime()){
                //需要提交
                //拿到答案
                String answers = p.getAnswer();
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

                            }
                            solveService.trySolveProblem(judgeTask, p.getUser_id(), p.getProblem_set_id());

                        } else {
                            solveService.trySolveSyncProblem(problem_id, p.getUser_id(), p.getProblem_set_id(), answer);
                        }
                    }
                }
            }
        }


    }
}
