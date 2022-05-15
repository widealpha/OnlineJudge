package cn.sdu.oj.controller;

import cn.sdu.oj.domain.po.ProblemSet;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.service.ProblemSetService;
import cn.sdu.oj.service.UserGroupService;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
            String [] types = {"普通","课堂测验","竞赛"};boolean type_valid= false;
            for (String a:types) {
                if (type.equals(a)){
                    type_valid =true;break;
                }
            }
            if (type_valid){
                Integer id =  problemSetService.createProblemSet(name,type,introduction,isPublic,beginTime,endTime,user.getId());
                if (id!=null){
                    return ResultEntity.data(id);
                }else return ResultEntity.error(StatusCode.COMMON_FAIL);
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
            JSONArray jsonArray = problemSetService.getSelfDoneProblemSet(user.getId());

            return ResultEntity.success("我做过的题目集", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

}