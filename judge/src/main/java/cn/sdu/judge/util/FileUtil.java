package cn.sdu.judge.util;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FileUtil {
    /**
     * 从文件中读取所有的字符串
     *
     * @param file 要读取的文件,可以不存在
     * @return 文件内的内容, 文件不存在返回null, 文件内容为空返回空字符串
     */
    public static String readAllFromFile(File file) {
        if (!file.exists()) {
            return null;
        }
        try {
            Scanner scanner = new Scanner(file);
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取文件前n行
     *
     * @param file 文件
     * @param n    要读取的行数
     * @return 读取的前n行的内容, 可能少于n行
     */
    public static String topLinesFromFile(File file, int n) {
        if (!file.exists()) {
            return null;
        }
        try {
            Scanner scanner = new Scanner(file);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                if (scanner.hasNextLine()) {
                    sb.append(scanner.nextLine()).append("\n");
                } else {
                    break;
                }
            }
            scanner.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 比较两个文件的内容是否相同
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 文件内容相同返回true, 否则返回false
     */
    public static boolean fileSame(File file1, File file2) {
        if (file1 == null || file2 == null){
            return false;
        }
        if (!file1.exists() || !file2.exists()) {
            return false;
        }
        if (file1.length() != file2.length()) {
            return false;
        }
        try {
            Scanner scanner1 = new Scanner(file1);
            Scanner scanner2 = new Scanner(file2);
            while (scanner1.hasNextLine()) {
                if (!scanner1.nextLine().equals(scanner2.nextLine())) {
                    return false;
                }
            }
            scanner1.close();
            scanner2.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
