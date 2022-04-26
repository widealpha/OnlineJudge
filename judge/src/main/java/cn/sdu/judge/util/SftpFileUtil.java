package cn.sdu.judge.util;

import cn.sdu.judge.bean.Checkpoint;
import cn.sdu.judge.gateway.SftpGateway;
import cn.sdu.judge.listener.JudgeMsgListener;
import cn.sdu.judge.mapper.Sha256Mapper;
import io.netty.buffer.ByteBufInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.sftp.session.SftpFileInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class SftpFileUtil {

    private static final Logger logger = LoggerFactory.getLogger(JudgeMsgListener.class);
    @Resource
    SftpGateway sftpGateway;
    @Value("${sftp.remote-root-dir}")
    String rootDir;
    @Value("${sftp.local-cache-dir}")
    String localCacheDir;

    /**
     * 返回题目id对应的所有测试点
     *
     * @param problemId 题目id
     * @return 所有的测试点, 返回null代表过程异常
     */
    public List<Checkpoint> checkpoints(int problemId) {
        List<Checkpoint> checkpoints = new ArrayList<>();
        String remoteProblemPath = rootDir + "/" + problemId;
        String localProblemPath = localCacheDir + "/" + problemId;
        String localCheckpointsPath = localProblemPath + "/checkpoints/";
        File localProblemDir = new File(localProblemPath);
        File localCheckpointsDir = new File(localCheckpointsPath);
        //如果本地题目对应的文件夹不存在，新建文件夹
        if (!localProblemDir.exists()) {
            //新建文件夹失败返回null
            if (!localProblemDir.mkdir()) {
                return null;
            }
        }
        if (!localCheckpointsDir.exists()) {
            //新建文件夹失败返回null
            if (!localCheckpointsDir.mkdir()) {
                return null;
            }
        }
        File localCheckPointSha256File = new File(localProblemPath + "/checkpoints.sha256");
        //判断本地是否存在sha256校验文件,如果存在和云端文件比较校验值
        if (localCheckPointSha256File.exists()) {
            String localSha256 = FileUtil.readAllFromFile(localCheckPointSha256File);
            File remoteCheckPointSha256File = sftpGateway.getFile(remoteProblemPath + "/checkpoints.sha256");
            String remoteSha256 = FileUtil.readAllFromFile(remoteCheckPointSha256File);
            //如果云端校验码不存在,代表题目不存在,返回null
            if (remoteSha256 == null) {
                return null;
            }
            //如果云端的校验码与本地校验码不相等,本地文件不是最新的,需要从云端重新拉取文件
            if (!remoteSha256.equals(localSha256)) {
                if (!downloadValidateRemoteCheckpointFile(remoteProblemPath, localCheckpointsPath, remoteSha256)) {
                    return null;
                }
            }
        } else { //如果本地校验码不存在,直接拉取新的zip
            //拉取文件会自动放在缓存目录
            File remoteCheckPointSha256File = sftpGateway.getFile(remoteProblemPath + "/checkpoints.sha256");
            String remoteSha256 = FileUtil.readAllFromFile(remoteCheckPointSha256File);
            if (!downloadValidateRemoteCheckpointFile(remoteProblemPath, localCheckpointsPath, remoteSha256)) {
                return null;
            }
        }
        File localCheckPointDir = new File(localCheckpointsPath);
        //列出本地缓存文件夹的所有文件
        String[] localCheckpointsName = localCheckPointDir.list();
        //如果没有文件代表缓存失败返回null
        if (localCheckpointsName == null) {
            return null;
        }
        //按照递增顺序从1开始分别是in和out文件,封装后返回上一层
        for (int i = 1; i <= localCheckpointsName.length / 2; i++) {
            Checkpoint checkpoint = new Checkpoint();
            checkpoint.setInput(new File(localCheckpointsPath + "/" + i + ".in"));
            checkpoint.setOutput(new File(localCheckpointsPath + "/" + i + ".out"));
            checkpoint.setOrder(i);
            checkpoint.setProblemId(problemId);
            checkpoints.add(checkpoint);
        }
        return checkpoints;
    }


    boolean downloadValidateRemoteCheckpointFile(String remoteProblemPath, String localCheckpointsPath, String remoteSha256) {
        //从云端重新获取测试点文件
        File remoteCheckpointsZipFile = sftpGateway.getFile(remoteProblemPath + "/checkpoints.zip");
        for (int i = 0; i < 3; i++) {
            if (Objects.equals(sha256(remoteCheckpointsZipFile), remoteSha256)) {
                break;
            } else {
                remoteCheckpointsZipFile.delete();
                remoteCheckpointsZipFile = sftpGateway.getFile(remoteProblemPath + "/checkpoints.zip");
            }
        }
        //解压文件到文件缓存目录/checkpoints/文件夹下
        //如果解压失败返回false
        return unzip(remoteCheckpointsZipFile, localCheckpointsPath);
    }

    /**
     * 计算文件的sha256
     *
     * @param file 需要计算hash的文件
     * @return 文件sha256哈希值, 可能返回null
     */
    String sha256(File file) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(Files.readAllBytes(file.toPath()));
            final StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                final String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256不支持", e);
            return null;
        } catch (IOException e) {
            logger.error("IO错误:", e);
            return null;
        }
    }

    /**
     * 解压zip到文件夹
     *
     * @param file   带解压文件
     * @param zipDir 解压到文件夹
     * @return 解压是否成功
     */
    private boolean unzip(File file, String zipDir) {
        final int BUFFER = 1024;
        try {
            BufferedOutputStream dest;
            BufferedInputStream is;
            ZipEntry entry;
            ZipFile zipfile = new ZipFile(file);

            Enumeration<? extends ZipEntry> e = zipfile.entries();
            while (e.hasMoreElements()) {
                entry = e.nextElement();
                is = new BufferedInputStream(zipfile.getInputStream(entry));
                int count;
                byte[] dataByte = new byte[BUFFER];
                FileOutputStream fos = new FileOutputStream(zipDir + entry.getName());
                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = is.read(dataByte, 0, BUFFER)) != -1) {
                    dest.write(dataByte, 0, count);
                }
                dest.flush();
                dest.close();
                is.close();
            }
            return true;
        } catch (Exception e) {
            logger.error(zipDir + "解压失败", e);
            e.printStackTrace();
            return false;
        }
    }

}
