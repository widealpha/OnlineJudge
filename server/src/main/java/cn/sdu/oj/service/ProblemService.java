package cn.sdu.oj.service;

import cn.sdu.oj.dao.ProblemMapper;
import cn.sdu.oj.domain.bo.Problem;
import cn.sdu.oj.domain.bo.ProblemWithInfo;
import cn.sdu.oj.domain.po.*;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.util.FileUtil;
import cn.sdu.oj.util.SFTPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ProblemService {
    @Autowired
    private ProblemMapper problemMapper;

    public int addProblem(ProblemWithInfo problemWithInfo) {
        // 0编程 1选择 答案 2填空 答案
        if (problemWithInfo.getType().equals(0)) {
            //编程题
            ProgramProblem programProblem = new ProgramProblem(null,
                    problemWithInfo.getName(),
                    problemWithInfo.getDescription(),
                    problemWithInfo.getExample(),
                    problemWithInfo.getDifficulty(),
                    problemWithInfo.getIsOpen(),
                    problemWithInfo.getTip(),
                    problemWithInfo.getAuthor());
            problemMapper.addProgramProblem(programProblem);
            problemWithInfo.setId(programProblem.getId());
        } else {
            //非编程题
            NonProgramProblem nonProgramProblem = new NonProgramProblem(null,
                    problemWithInfo.getName(),
                    problemWithInfo.getDescription(),
                    problemWithInfo.getExample(),
                    problemWithInfo.getDifficulty(),
                    problemWithInfo.getIsOpen(),
                    problemWithInfo.getTip(),
                    problemWithInfo.getAuthor(),
                    problemWithInfo.getAnswer());
            problemMapper.addNonProgramProblem(nonProgramProblem);
            problemWithInfo.setId(nonProgramProblem.getId());
        }
        if (problemWithInfo.getTags() != null) {
            // 插入标签
            String tags = problemWithInfo.getTags();
            String[] tagArr = tags.split("_");
            for (int i = 0; i < tagArr.length; i++) {
                problemMapper.addTag(problemWithInfo.getId(), Integer.parseInt(tagArr[i]), problemWithInfo.getType());
            }
        }


        return problemWithInfo.getId();

    }

    /**
     * 添加一个题目限制
     *
     * @param limit
     * @return
     */
    public int addProblemLimit(ProblemLimit limit) {
        problemMapper.addProblemLimit(limit);
        return limit.getProblemId();
    }

    public void updateProblemLimit(ProblemLimit limit) {
        problemMapper.updateProblemLimit(limit);
    }

    /**
     * 通过题目id查找对应题目限制
     *
     * @param problemId
     * @return
     */
    public ProblemLimit getProblemLimitByProblemId(int problemId) {
        return problemMapper.getProblemLimitByProblemId(problemId);
    }

    public List<Tag> getTopLevelTag() {
        List<Tag> topLevelTag = problemMapper.getTopLevelTag();
        return topLevelTag;
    }

    public List<Tag> getChildrenTagByParentId(int parentId) {
        return problemMapper.getChildrenTagByParentId(parentId);
    }

    public void deleteProblem(int u_id, int p_id, int type) {
        if (type == 0) {
            //删除题目限制
            problemMapper.deleteProgramProblem(u_id, p_id);
        } else {
            problemMapper.deleteNonProgramProblem(u_id, p_id);
        }
        // 删除标签
        problemMapper.deleteTag(p_id, type);
        // TODO: 2022/5/14 删除题目限制
    }

    public void updateProblem(ProblemWithInfo problemWithInfo) {
        if (problemWithInfo.getType().equals(0)) {
            //编程题
            ProgramProblem programProblem = new ProgramProblem(null,
                    problemWithInfo.getName(),
                    problemWithInfo.getDescription(),
                    problemWithInfo.getExample(),
                    problemWithInfo.getDifficulty(),
                    problemWithInfo.getIsOpen(),
                    problemWithInfo.getTip(),
                    problemWithInfo.getAuthor());
            problemMapper.updateProgramProblem(programProblem);
        } else {
            //非编程题
            NonProgramProblem nonProgramProblem = new NonProgramProblem(null,
                    problemWithInfo.getName(),
                    problemWithInfo.getDescription(),
                    problemWithInfo.getExample(),
                    problemWithInfo.getDifficulty(),
                    problemWithInfo.getIsOpen(),
                    problemWithInfo.getTip(),
                    problemWithInfo.getAuthor(),
                    problemWithInfo.getAnswer());
            problemMapper.updateNonProgramProblem(nonProgramProblem);
        }
        // 先删除之前的标签
        problemMapper.deleteTag(problemWithInfo.getId(), problemWithInfo.getType());
        if (problemWithInfo.getTags() != null) {
            // 插入标签
            String tags = problemWithInfo.getTags();
            String[] tagArr = tags.split("_");
            for (int i = 0; i < tagArr.length; i++) {
                problemMapper.addTag(problemWithInfo.getId(), Integer.parseInt(tagArr[i]), problemWithInfo.getType());
            }
        }
    }

    public void addTestPoints(int p_id, MultipartFile file, String sha256) throws Exception {
        // 校验内部文件的正确性
        FileUtil.verifyZipFormat(file.getBytes());
        // 初始化目标目标文件夹
        StringBuffer destinationDir = new StringBuffer(SFTPUtil.ROOT_PATH);
        destinationDir.append(SFTPUtil.SEPARATOR + p_id);
        // 初始化工具类
        SFTPUtil sftpUtil = new SFTPUtil();
        // 1、写入压缩包
        sftpUtil.uploadSingleFile(file.getBytes(), destinationDir.toString(), "checkpoints.zip");
        // 2、写入SHA256
        sftpUtil.uploadSingleFile(sha256.getBytes(StandardCharsets.UTF_8), destinationDir.toString(), "checkpoints.sha256");

    }

    /**
     * 检查一个问题是否存在
     * * @param problemId
     *
     * @return
     */
    public boolean isProblemExist(int problemId) {
        return problemMapper.isProblemExist(problemId);
    }

    public Problem getProblemByProblemIdAndType(int problemId, int type) {
        if (type == 0) {
            ProgramProblem problem = problemMapper.getProgramProblemById(problemId);
            return problem;
        } else {
            NonProgramProblem problem = problemMapper.getNonProgramProblemById(problemId);
            return problem;
        }

    }

    public List<Tag> getTagListByProblemIdAndType(int problemId, int type) {
        List<Tag> tagListByProblemIdAndType = problemMapper.getTagListByProblemIdAndType(problemId, type);
        return tagListByProblemIdAndType;
    }

    public List<Tag> getTagListWithPrefixByProblemIdAndType(int problemId, int type) {
        List<Tag> tagListByProblemIdAndType = problemMapper.getTagListWithPrefixByProblemIdAndType(problemId, type);
        return tagListByProblemIdAndType;
    }

    public UserInfo getAuthorInfoByProblemIdAndType(int problemId, int type) {
        if (type == 0) {
            return problemMapper.getAuthorNameByProgramProblemId(problemId);
        } else {
            return problemMapper.getAuthorNameByNonProgramProblemId(problemId);
        }
    }

}
