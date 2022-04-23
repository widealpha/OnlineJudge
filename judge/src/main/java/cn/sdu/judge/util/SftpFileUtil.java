package cn.sdu.judge.util;

import cn.sdu.judge.bean.Checkpoint;
import cn.sdu.judge.bean.LanguageEnum;
import cn.sdu.judge.bean.SpecialJudge;
import cn.sdu.judge.gateway.SftpGateway;
import cn.sdu.judge.listener.JudgeMsgListener;
import cn.sdu.judge.mapper.Sha256Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.sftp.session.SftpFileInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
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

    @Resource
    Sha256Mapper sha256Mapper;

    /**
     * 返回题目id对应的所有测试点
     *
     * @param problemId 题目id
     * @return 所有的测试点
     */
    public List<Checkpoint> checkpoints(int problemId) {
        List<Checkpoint> checkpoints = new ArrayList<>();
        String problemDir = rootDir + "/" + problemId;
        String cacheDir = localCacheDir + "/" + problemId;
        List<SftpFileInfo> files = sftpGateway.listFile(problemDir);
        if (files == null || files.isEmpty()) {
            return checkpoints;
        }
        String checkpointsSha256 = sha256Mapper.checkpointZipSha256(problemId);
        File checkpointSha256File = sftpGateway.getFile(problemDir + "/checkpoints.sha256");

        String sha256 = readFromFile(checkpointSha256File);
        if (sha256 == null) {
            return null;
        }
        //本地缓存位置
        File zipFile = new File(cacheDir + "/checkpoints.zip");

        File checkpointDir = new File(cacheDir + "/checkpoint/");
        //如果sha256本地与云端不一致，或者本地缓存不存在重新拉取文件
        if (!sha256.equals(checkpointsSha256) || !zipFile.exists()){
            //获取云端zip
            zipFile = sftpGateway.getFile(problemDir + "/checkpoints.zip");
            sha256Mapper.updateCheckpointZipSha256(problemId, sha256);
            //删除本地已解压文件
            if (checkpointDir.isDirectory()) {
                String[] children = checkpointDir.list();
                if (children != null) {
                    for (String fileName : children) {
                        new File(fileName).delete();
                    }
                }
            }
            checkpointDir.delete();
            //重新解压获取的文件
            if (!unzip(zipFile, localCacheDir + "/checkpoint/")) {
                return null;
            }
        }
        String[] children = checkpointDir.list();
        if (children == null) {
            return null;
        }
        for (int i = 1; i <= children.length / 2; i++) {
            Checkpoint checkpoint = new Checkpoint();
            checkpoint.setInput(new File(i + ".in"));
            checkpoint.setOutput(new File(i + ".out"));
            checkpoint.setOrder(i);
            checkpoint.setProblemId(problemId);
            checkpoints.add(checkpoint);
        }
        return checkpoints;
    }


    private String readFromFile(File file) {
        try {
            if (!file.exists()) {
                return null;
            }
            Scanner scanner = new Scanner(file);
            return scanner.nextLine();
        } catch (IOException e) {
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
