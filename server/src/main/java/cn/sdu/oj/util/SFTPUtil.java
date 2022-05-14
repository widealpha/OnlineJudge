package cn.sdu.oj.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SFTPUtil {
    public static final String ROOT_PATH = "/sduoj_sftp";
    public static final String SEPARATOR = "/";
    Session session = null;
    Channel channel = null;
    final String sftpHost = "121.196.101.7";
    final String sftpUsername = "sduoj_sftp";
    final String sftpPassword = "sduoj2022";
    final int sftpPort = 22;

    /**
     * 获取sftp channel对象
     *
     * @return
     * @throws JSchException
     */
    public ChannelSftp getChannelSftp() throws JSchException {
        // 创建JSch对象
        JSch jsch = new JSch();
        // 获取sesson对象
        session = jsch.getSession(sftpUsername, sftpHost, sftpPort);
        // 设置sftp访问密码
        session.setPassword(sftpPassword);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        // 为session重新设置参数
        session.setConfig(config);
        // 设置超时
        session.setTimeout(30000);
        session.connect();
        // 打开sftp通道
        channel = session.openChannel("sftp");
        // 建立sftp通道连接
        channel.connect();
        return (ChannelSftp) channel;
    }

    public void closeChannel() {
        if (channel != null) {
            channel.disconnect();
            log.info("断开channel");
        }
        if (session != null) {
            session.disconnect();
            log.info("断开session");
        }
    }

    public static void main(String[] args) throws JSchException {

        /**
         * JSch支持三种文件传输模式：
         * OVERWRITE 完全覆盖模式，这是JSch的 默认
         * 文件传输模式，即如果目标文件已经存在，传输的文件将完全覆盖目标文件，产生新的文件。
         * RESUME 恢复模式，如果文件已经传输一部分，这时由于网络或其他任何原因导致文件传输中断，如果下一次传输相同的文件，
         * 则会从上一次中断的地方续传。
         * APPEND 追加模式，如果目标文件已存在，传输的文件将在目标文件后追加。
         */
        SFTPUtil sftpChannel = new SFTPUtil();
        ChannelSftp channel = sftpChannel.getChannelSftp();
        String srcDirectory = "E:\\源码\\java\\alibaba-exam\\", destDirectory = "/sduoj_sftp";
        sftpChannel.chuanshu(channel, srcDirectory, destDirectory);
        channel.quit();
        sftpChannel.closeChannel();


    }

    public static void upload(String srcFilePath, String destFilePath) throws JSchException {
        SFTPUtil sftpUtil = new SFTPUtil();
        ChannelSftp channel = sftpUtil.getChannelSftp();
        sftpUtil.chuanshu(channel, srcFilePath, destFilePath);
        return;
    }

    /**
     * 向服务器指定目录下以覆盖模式传输文件|文件夹
     *
     * @param channel    sftp通道对象
     * @param 源文件[目录]路径, 可为文件或文件目录
     * @return
     * @author yuguagua
     * @email feimeideyanggao@126.com
     * @create 2021年2月6日
     * @destFilePath 目标文件夹目录, 必须存在在且必须为目录!!!不可以是文件!!!, 后缀不要加 / , 传输的文件夹会新建在此目录下
     */
    public void chuanshu(ChannelSftp channel, String srcFilePath, String destFilePath) {
        File file = new File(srcFilePath);
        String subDestFilePath = destFilePath + "/" + file.getName(); // 真·文件目录
        // 1. 源文件为目录, 则列表显示其下所有的文件夹、文件
        if (file.isDirectory()) {
            // 1.1 在destFilePath目录下检查本目录是否存在, 存在则删除再创建; 不存在则新创建
            try {
                channel.cd(subDestFilePath);
                log.info("目标服务器上 目录: " + subDestFilePath + " 存在, 先予以删除");
                channel.cd(destFilePath);
                deleteTargetFile(channel, subDestFilePath);
                log.info("目标服务器上 目录: " + subDestFilePath + " 删除成功");
                channel.mkdir(subDestFilePath);
                log.info("目标服务器上 目录: " + subDestFilePath + " 删除后创建成功");
            } catch (SftpException e) {
                log.info("目标服务器上 目录: " + subDestFilePath + " 不存在, 正在创建");
                try {
                    channel.mkdir(subDestFilePath);
                } catch (SftpException e1) {
                    log.info("目标服务器上 创建目录: " + subDestFilePath + " 出错." + e1.getLocalizedMessage());
                }
            }
            File subfiles[] = file.listFiles();
            for (int i = 0; i < subfiles.length; i++) {
                chuanshu(channel, subfiles[i].getAbsolutePath(), subDestFilePath);
            }
        } else {

            // 2. 源文件为文件，将文件传输到 destFilePath目录下
            // 2.1 检查destFilePath目录是否存在, 不存在则创建
            try {
                channel.cd(destFilePath);
            } catch (SftpException e) {
                log.info("目录: " + destFilePath + " 不存在, 正在创建");
                try {
                    channel.mkdir(destFilePath);
                } catch (SftpException e1) {
                    log.info("创建目录: " + destFilePath + " 出错." +
                            e1.getLocalizedMessage());

                }
            }
            try {
                channel.cd(destFilePath);
                channel.put(srcFilePath, destFilePath + "/" + file.getName(), ChannelSftp.OVERWRITE);
                log.info("传输文件: " + srcFilePath + "  到  " + destFilePath + "/" + file.getName() + "  成功.");
            } catch (SftpException e) {
                log.info("传输文件: " + srcFilePath + "  到  " + destFilePath + "/" + file.getName() + "  出错."
                        + e.getLocalizedMessage());
            }
        }

    }

    /**
     * 删除服务器指定的文件夹及其里面的所有内容
     *
     * @param targetFilePath 要删除的文件或文件夹路径, 应为绝对路径, 且非空
     * @return
     * @author yuguagua
     * @email feimeideyanggao@126.com
     * @create 2021年2月6日
     */
    public void deleteTargetFile(ChannelSftp channel, String targetFilePath) {
        try {
            // 1. 路径为文件夹
            if (channel.stat(targetFilePath).isDir()) {
                channel.cd(targetFilePath);
                Vector<LsEntry> entries = channel.ls(".");
                for (LsEntry subFilePath : entries) {
                    String fileName = subFilePath.getFilename();
                    if (".".equals(fileName) || "..".equals(fileName)) {
                        continue;
                    }
                    deleteTargetFile(channel, subFilePath.getFilename());
                }
                channel.cd("..");
                channel.rmdir(targetFilePath);
                log.info("删除文件夹: " + targetFilePath + " 成功. ");
            } else {
                // 2. 路径为文件
                try {
                    channel.rm(targetFilePath);
                    log.info("删除文件: " + targetFilePath + " 成功. ");
                } catch (SftpException e) {
                    log.info("删除文件: " + targetFilePath + " 出错. " + e.getMessage());
                }
            }
        } catch (SftpException e) {
            log.info("判断文件: " + targetFilePath + " 类型出错. " + e.getMessage());
        }

    }

    public void uploadSingleFile(byte[] data, String destinationDir, String fileName) throws Exception {
        SFTPUtil sftpUtil = new SFTPUtil();
        ChannelSftp channel = sftpUtil.getChannelSftp();
        // 获取输入流
        InputStream input = new ByteArrayInputStream(data);
        long start = System.currentTimeMillis();
        try {
            //如果文件夹不存在，则创建文件夹
            //切换到指定文件夹
            channel.cd(destinationDir);
        } catch (SftpException e) {
            //创建不存在的文件夹，并切换到文件夹
            channel.mkdir(destinationDir);
            channel.cd(destinationDir);
        }
        channel.put(input, fileName);
        log.info("文件上传成功！！ 耗时：{}ms", (System.currentTimeMillis() - start));

    }
}

