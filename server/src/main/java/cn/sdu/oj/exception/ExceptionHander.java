package cn.sdu.oj.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHander {
    private static final Logger log = LoggerFactory.getLogger(ExceptionHander.class);

    @ExceptionHandler(Exception.class)
    public void getException(Exception e) {
        log.error("服务器发生异常", e);
    }
}
