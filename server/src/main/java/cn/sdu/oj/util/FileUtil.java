package cn.sdu.oj.util;


import com.jcraft.jsch.JSchException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static String getSHA256(byte[] data) throws Exception {

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            final StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                final String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
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


}
