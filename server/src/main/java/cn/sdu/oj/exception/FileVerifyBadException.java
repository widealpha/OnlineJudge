package cn.sdu.oj.exception;

/**
 * @author 陈景涛
 * 当文件校验失败时抛出异常
 */
public class FileVerifyBadException extends Exception {
    public FileVerifyBadException(String msg) {
        super(msg);
    }
}
