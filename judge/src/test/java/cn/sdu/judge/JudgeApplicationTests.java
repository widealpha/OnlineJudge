package cn.sdu.judge;

import cn.sdu.judge.gateway.SftpGateway;
import cn.sdu.judge.util.SftpFileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.sftp.session.SftpFileInfo;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class JudgeApplicationTests {
    @Resource
    SftpFileUtil sftpFileUtil;
    @Test
    void contextLoads() {
        sftpFileUtil.checkpoints(0);
    }

}
