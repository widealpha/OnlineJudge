package cn.sdu.oj.service;

import cn.sdu.oj.dao.*;
import cn.sdu.oj.domain.bo.LanguageEnum;
import cn.sdu.oj.domain.dto.ProblemDto;
import cn.sdu.oj.domain.po.*;
import cn.sdu.oj.domain.vo.DifficultyEnum;
import cn.sdu.oj.domain.vo.ProblemTypeEnum;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.exception.TagNotExistException;
import cn.sdu.oj.util.FileUtil;
import cn.sdu.oj.util.SFTPUtil;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.lang.Collections;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Service
public class ProblemService {

    @Resource
    AsyncProblemMapper asyncProblemMapper;
    @Resource
    SyncProblemMapper syncProblemMapper;
    @Resource
    GeneralProblemMapper generalProblemMapper;
    @Resource
    TagMapper tagMapper;

    public ResultEntity<List<Tag>> allTags() {
        return ResultEntity.data(tagMapper.selectAllTags());
    }

    public ResultEntity<ProblemDto> findProblemInfo(int problemId, int userId) {
        ProblemDto problemDto = new ProblemDto();
        GeneralProblem generalProblem = generalProblemMapper.selectGeneralProblem(problemId);
        if (generalProblem == null) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST);
        } else {
            List<Tag> tags = generalProblemMapper.selectProblemTags(problemId);
            problemDto.setId(problemId);
            problemDto.setType(generalProblem.getType());
            problemDto.setTypeName(ProblemTypeEnum.typeName(generalProblem.getType()));
            problemDto.setTags(tags);
            problemDto.setCreator(generalProblem.getCreator());
            problemDto.setDifficulty(generalProblem.getDifficulty());
            problemDto.setDifficultyName(DifficultyEnum.difficultyName(generalProblem.getDifficulty()));

            if (generalProblem.getType() == ProblemTypeEnum.PROGRAMING.id) {
                AsyncProblem asyncProblem = asyncProblemMapper.selectProblem(generalProblem.getTypeProblemId());
                problemDto.setName(asyncProblem.getName());
                problemDto.setDescription(asyncProblem.getDescription());
                problemDto.setExample(asyncProblem.getExample());
                problemDto.setMemoryLimit(asyncProblem.getMemoryLimit());
                problemDto.setTimeLimit(asyncProblem.getTimeLimit());
                problemDto.setCodeLengthLimit(asyncProblem.getCodeLengthLimit());
                problemDto.setExistCheckpoints(asyncProblem.isExistCheckpoints());
                problemDto.setModifiedTime(asyncProblem.getModifiedTime());
                if (asyncProblem.getSupportLanguages().isEmpty()){
                    problemDto.setSupportLanguages(Arrays.asList(LanguageEnum.values()));
                } else {
                    List<LanguageEnum> supportLanguages = new ArrayList<>();
                    for (String language : JSON.parseArray(asyncProblem.getSupportLanguages(), String.class)) {
                        supportLanguages.add(LanguageEnum.valueOf(language));
                    }
                    problemDto.setSupportLanguages(supportLanguages);
                }
            } else {
                SyncProblem syncProblem = syncProblemMapper.selectProblem(generalProblem.getTypeProblemId());
                problemDto.setName(syncProblem.getName());
                problemDto.setDescription(syncProblem.getDescription());
                problemDto.setAnswer(syncProblem.getAnswer());
                problemDto.setOptions(syncProblem.getOptions());
                problemDto.setModifiedTime(syncProblem.getModifiedTime());
            }
            //非创建者无法看到答案
            if (problemDto.getCreator() != userId) {
                problemDto.setAnswer(null);
            }
            return ResultEntity.data(problemDto);
        }
    }

    @Transactional
    public ResultEntity<Integer> addProgramingProblem(AsyncProblem problem, List<Integer> tags) throws TagNotExistException {
        if (asyncProblemMapper.insertProblem(problem)) {
            GeneralProblem generalProblem = new GeneralProblem();
            generalProblem.setType(0);
            generalProblem.setTypeProblemId(problem.getId());
            generalProblem.setCreator(problem.getCreator());
            generalProblem.setDifficulty(problem.getDifficulty());
            if (generalProblemMapper.insertGeneralProblem(generalProblem)) {
                int problemId = generalProblem.getId();
                changeProblemTags(problemId, tags);
                return ResultEntity.data(generalProblem.getId());
            }
        }
        return ResultEntity.error(StatusCode.DATA_ALREADY_EXIST);
    }

    @Transactional
    public ResultEntity<Integer> addOtherProblem(SyncProblem problem, List<Integer> tags) throws TagNotExistException {
        if (syncProblemMapper.insertProblem(problem)) {
            GeneralProblem generalProblem = new GeneralProblem();
            generalProblem.setType(problem.getType());
            generalProblem.setTypeProblemId(problem.getId());
            generalProblem.setCreator(problem.getCreator());
            generalProblem.setDifficulty(problem.getDifficulty());
            if (generalProblemMapper.insertGeneralProblem(generalProblem)) {
                int problemId = generalProblem.getId();
                changeProblemTags(problemId, tags);
                return ResultEntity.data(generalProblem.getId());
            }
        }
        return ResultEntity.error(StatusCode.DATA_ALREADY_EXIST);
    }

    @Transactional
    public ResultEntity<Boolean> updateProgramingProblem(int problemId, AsyncProblem problem, List<Integer> tags) throws TagNotExistException {
        GeneralProblem generalProblem = generalProblemMapper.selectGeneralProblem(problemId);
        //如果数据不存在或者表示为非编程题，返回数据不存在
        if (generalProblem == null || generalProblem.getType() != 0) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST);
        } else {
            problem.setId(generalProblem.getTypeProblemId());
            generalProblem.setDifficulty(problem.getDifficulty());
            generalProblemMapper.updateGeneralProblem(generalProblem);
        }
        if (asyncProblemMapper.updateProblem(problem)) {
            changeProblemTags(problemId, tags);
            return ResultEntity.data(true);
        }
        return ResultEntity.error(StatusCode.DATA_NOT_EXIST);
    }

    @Transactional
    public ResultEntity<Boolean> updateOtherProblem(int problemId, SyncProblem problem, List<Integer> tags) throws TagNotExistException {
        GeneralProblem generalProblem = generalProblemMapper.selectGeneralProblem(problemId);
        //如果数据不存在或者表示为编程题，返回数据不存在,否则寻找专属的typeProblemId
        if (generalProblem == null || generalProblem.getType() == 0) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST);
        } else {
            problem.setId(generalProblem.getTypeProblemId());
            //更新generalProblem的难度与种类
            generalProblem.setDifficulty(problem.getDifficulty());
            generalProblem.setType(problem.getType());
            generalProblemMapper.updateGeneralProblem(generalProblem);
        }
        if (syncProblemMapper.updateProblem(problem)) {
            changeProblemTags(problemId, tags);
            return ResultEntity.data(true);
        }
        return ResultEntity.error(StatusCode.DATA_NOT_EXIST);
    }

    private void changeProblemTags(int problemId, List<Integer> tags) throws TagNotExistException {
        generalProblemMapper.deleteProblemTags(problemId);
        for (Integer tagId : tags) {
            if (tagId == null || !tagMapper.exist(tagId)) {
                throw new TagNotExistException();
            } else if (!generalProblemMapper.addProblemTag(problemId, tagId)) {
                throw new TagNotExistException();
            }
        }
    }

    @Transactional
    public ResultEntity<Boolean> updateProblemLimit(ProblemLimit problemLimit) {
        Integer typeProblemId = generalProblemMapper.selectTypeProblemIdById(problemLimit.getProblemId());
        if (typeProblemId == null) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST);
        }
        AsyncProblem asyncProblem = asyncProblemMapper.selectProblem(typeProblemId);
        if (asyncProblem == null) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST);
        }
        asyncProblem.setMemoryLimit(problemLimit.getMemory());
        asyncProblem.setCodeLengthLimit(problemLimit.getCodeLength());
        asyncProblem.setTimeLimit(problemLimit.getTime());
        return ResultEntity.data(asyncProblemMapper.updateProblemLimit(asyncProblem));
    }

    public ResultEntity<Boolean> deleteProblem(int problemId, int userId) {
        GeneralProblem generalProblem = generalProblemMapper.selectGeneralProblem(problemId);
        if (generalProblem == null) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST);
        } else if (generalProblem.getCreator() != userId) {
            return ResultEntity.error(StatusCode.NO_PERMISSION_OR_EMPTY);
        } else {
            return ResultEntity.data(generalProblemMapper.deleteGeneralProblem(problemId));
        }
    }

    /**
     * 克隆题目,仅允许有克隆码的用户使用
     *
     * @param problemId 需要克隆的题目Id
     * @return clone完成的题目Id
     */
    @Transactional
    public ResultEntity<Integer> cloneProblem(int problemId, int userId) {
        GeneralProblem generalProblem = generalProblemMapper.selectGeneralProblem(problemId);
        if (generalProblem == null) {
            return null;
        }
        int type = generalProblem.getType();
        //是编程题
        if (type == ProblemTypeEnum.PROGRAMING.id) {
            AsyncProblem asyncProblem = asyncProblemMapper.selectProblem(generalProblem.getTypeProblemId());
            asyncProblem.setCreator(userId);
            asyncProblemMapper.insertProblem(asyncProblem);
            generalProblem.setTypeProblemId(asyncProblem.getId());
        } else {
            SyncProblem syncProblem = syncProblemMapper.selectProblem(generalProblem.getTypeProblemId());
            syncProblem.setCreator(userId);
            syncProblemMapper.insertProblem(syncProblem);
            generalProblem.setTypeProblemId(syncProblem.getId());
        }
        generalProblem.setCreator(userId);
        generalProblemMapper.insertGeneralProblem(generalProblem);
        return ResultEntity.data(generalProblem.getId());
    }

    public List<Tag> getTopLevelTag() {
        return tagMapper.getTopLevelTag();
    }

    public List<Tag> getChildrenTagByParentId(int parentId) {
        return tagMapper.getChildrenTagByParentId(parentId);
    }

    public ResultEntity<Boolean> uploadCheckpoints(int problemId, int userId, MultipartFile file, String sha256) throws Exception {
        GeneralProblem generalProblem = generalProblemMapper.selectGeneralProblem(problemId);
        if (generalProblem == null || generalProblem.getCreator() != userId || file.isEmpty()) {
            return ResultEntity.error(StatusCode.NO_PERMISSION_OR_EMPTY);
        } else if (generalProblem.getType() != ProblemTypeEnum.PROGRAMING.id) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST, "非编程题不可添加测试点");
        } else if (!FileUtil.verifyZipFormat(file.getBytes())) {  // 校验zip文件的格式正确性
            return ResultEntity.error(StatusCode.PARAM_NOT_VALID);
        }
        // 初始化目标目标文件夹
        StringBuilder destinationDir = new StringBuilder(SFTPUtil.ROOT_PATH);
        destinationDir.append(SFTPUtil.SEPARATOR).append(problemId);
        // 初始化工具类
        SFTPUtil sftpUtil = new SFTPUtil();
        // 1、写入压缩包
        sftpUtil.uploadSingleFile(file.getBytes(), destinationDir.toString(), "checkpoints.zip");
        if (!sha256.equals(FileUtil.sha256(file.getBytes()))) {
            return ResultEntity.error(StatusCode.VALIDATE_ERROR, "文件校验失败,请重新上传");
        }
        // 2、写入SHA256
        sftpUtil.uploadSingleFile(sha256.getBytes(StandardCharsets.UTF_8), destinationDir.toString(), "checkpoints.sha256");
        return ResultEntity.data(asyncProblemMapper.updateExistCheckpoints(generalProblem.getTypeProblemId(), true));
    }

    public void downloadCheckpoints(int problemId, int userId, HttpServletResponse response) {
        GeneralProblem generalProblem = generalProblemMapper.selectGeneralProblem(problemId);
        if (generalProblem == null || generalProblem.getCreator() != userId) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        response.setContentType("application/x-zip-compressed");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=checkpoints.zip");
        ServletOutputStream outputStream;
        try {
            outputStream = response.getOutputStream();
            // 初始化目标目标文件夹
            SFTPUtil sftpUtil = new SFTPUtil();
            sftpUtil.downloadSingleFile(SFTPUtil.ROOT_PATH + SFTPUtil.SEPARATOR + problemId, "checkpoints.zip", outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception ignore) {
        }
    }

    public ResultEntity<Map<String, String>> previewCheckpoints(int problemId, int userId) {
        GeneralProblem generalProblem = generalProblemMapper.selectGeneralProblem(problemId);
        if (generalProblem == null || generalProblem.getCreator() != userId) {
            return ResultEntity.error(StatusCode.NO_PERMISSION_OR_EMPTY);
        }
        return ResultEntity.data(new HashMap<>());
    }


    public ResultEntity<List<ProblemDto>> allMyCreateProblems(int userId) {
        List<ProblemDto> res = new ArrayList<>();
        List<GeneralProblem> generalProblems = generalProblemMapper.selectGeneralProblemIdByCreator(userId);
        for (GeneralProblem generalProblem : generalProblems) {
            ProblemDto problemDto = findProblemInfo(generalProblem.getId(), userId).getData();
            if (problemDto != null) {
                problemDto.setDescription(null);
                res.add(problemDto);
            }
        }
        return ResultEntity.data(res);
    }
}
