package cn.sdu.oj.util;

import java.io.*;
import java.security.MessageDigest;

public class FileUtil {
    public static String getSHA256(byte[] data) throws Exception {

        MessageDigest md5 = MessageDigest.getInstance("SHA-256");
        md5.update(data, 0, data.length);
        return toHexString(md5.digest());
    }

    private static String toHexString(byte b[]) {
        StringBuilder sb = new StringBuilder();
        for (byte aB : b) {
            sb.append(Integer.toHexString(aB & 0xFF));
        }
        return sb.toString();
    }

    private static final String ROOT_PATH = "C:\\Users\\lenovo\\Desktop\\SDUOJ\\TEST_POINT";
    public static final String SEPARATOR = "\\";

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
        File file = new File(ROOT_PATH + SEPARATOR + dirPath + SEPARATOR + prefix + "." + suffix);
        // 设置文件权限
        file.setExecutable(true, false);
        file.setReadable(true, false);
        file.setWritable(true, false);
        // 如果文件已经存在 删除源文件
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        FileOutputStream os = new FileOutputStream(file);
        os.write(data);
        os.flush();
        os.close();
    }

    public static void deleteFile(String absolutePath) {
        new File(ROOT_PATH + absolutePath).delete();
    }


}
