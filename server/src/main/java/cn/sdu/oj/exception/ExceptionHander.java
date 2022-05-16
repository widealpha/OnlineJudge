package cn.sdu.oj.exception;

import cn.sdu.oj.entity.ResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHander {
    private static final Logger log = LoggerFactory.getLogger(ExceptionHander.class);
    @ExceptionHandler(FileVerifyBadException.class)
    @ResponseBody
    public ResultEntity getFileVerifyBadException(FileVerifyBadException e) {
        log.error("文件校验未通过", e);
        return ResultEntity.error("the file is bad");
    }
}
