package cn.sdu.oj.util;


import cn.sdu.oj.exception.FileVerifyBadException;
import com.jcraft.jsch.JSchException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {

    private static final String ROOT_PATH = "/home/sftp_root/sduoj_sftp";
    public static final String SEPARATOR = "/";

    public static byte[] getAllBytes(File file) throws Exception {
        ByteArrayOutputStream byteArrayOs = new ByteArrayOutputStream();
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buf = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            byteArrayOs.write(buf, 0, len);
        }
        return byteArrayOs.toByteArray();
    }

    /**
     * 计算文件的sha256
     *
     * @param file 需要计算hash的文件
     * @return 文件sha256哈希值, 可能返回null
     */
    public static String sha256(File file) {
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
        } catch (NoSuchAlgorithmException | IOException e) {
            return null;
        }
    }

    /**
     * 计算文件的sha256
     *
     * @param bytes 需要计算的byte
     * @return 文件sha256哈希值, 可能返回null
     */
    public static String sha256(byte[] bytes) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            final StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                final String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static boolean isExist(String path) {
        /**
         * 文件是否存在 这个路径是相对路径 第一个文件不带/
         */
        return new File(ROOT_PATH + path).exists();
    }

    /**
     * 创建新目录 如果已经存在  直接返回true
     */
    public static boolean createDir(String path) {

        /**
         * 创建新目录 如果已经存在  直接返回true
         */
        if (isExist(path)) {
            return true;
        } else {
            File file = new File(ROOT_PATH + path);
            file.setExecutable(true, false);
            file.setReadable(true);
            file.setWritable(true);
            return file.mkdirs();
        }
    }

    public static void createFile(String dirPath, String prefix, String suffix, byte[] data) throws IOException {

        Path path = Files.createTempFile(prefix, "." + suffix);

        File tempFile = path.toFile();
        String name = tempFile.getName();
        tempFile.renameTo(new File(tempFile.getAbsolutePath().replace(name, prefix + "." + suffix)));
        System.out.println(tempFile.getAbsolutePath());

        // file.createNewFile();
        FileOutputStream os = new FileOutputStream(tempFile);
        os.write(data);
        os.flush();
        os.close();
        try {
            SFTPUtil.upload(tempFile.getAbsolutePath(), "/sduoj_sftp/" + dirPath);
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String absolutePath) {
        new File(ROOT_PATH + absolutePath).delete();
    }

    public static boolean verifyZipFormat(byte[] data) throws Exception {
        // 写完才发现不需要写临时文件 焯
        // Path tmpPath = Files.createTempFile("tmp", ".zip");
        // File tmpFile = tmpPath.toFile();
        // FileOutputStream outputStream = new FileOutputStream(tmpFile);
        // outputStream.write(data);
        // outputStream.flush();
        // outputStream.close();
        // String absolutePath = tmpFile.getAbsolutePath();
        // System.out.println(absolutePath);

        //构建解压输入流
        // Charset.forName("GBK") 不加这一句会因为编码问题报错
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        ZipInputStream zin = new ZipInputStream(inputStream, StandardCharsets.UTF_8);
        ZipEntry entry = null;
        List<String> input = new ArrayList<>();
        List<String> output = new ArrayList<>();
        while ((entry = zin.getNextEntry()) != null) {
            String name = entry.getName();
            if (name.contains(".in")) {
                input.add(name);
            } else {
                output.add(name);
            }
        }
        zin.close();
        inputStream.close();
        if (input.isEmpty() || output.isEmpty()) {
            return false;
        }
        //校验规则，每个in都要有对应的out
        for (int i = 1; i <= input.size(); i++) {
            if (!output.contains(i + ".out") || !input.contains(i + ".in")) {
                return false;
            }
        }
        return true;
    }

}
