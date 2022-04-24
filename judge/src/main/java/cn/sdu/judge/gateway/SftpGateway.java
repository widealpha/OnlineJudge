package cn.sdu.judge.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.sftp.session.SftpFileInfo;

import java.io.File;
import java.util.List;

@MessagingGateway
public interface SftpGateway {
    /**
     * 获取远程指定目录下文件列表
     * @param directory 远程目录
     * @return List<SftpFileInfo>
     */
    @Gateway(requestChannel = "lsChannel")
    List<SftpFileInfo> listFile(String directory);

    /**
     * 下载指定文件
     * @param directory 远程文件名（包含目录）
     * @return 下载后的文件
     */
    @Gateway(requestChannel = "getChannel")
    File getFile(String directory);
}