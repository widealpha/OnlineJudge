package cn.sdu.oj;

import cn.sdu.oj.util.FileUtil;
import cn.sdu.oj.util.SFTPUtil;
import com.jcraft.jsch.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by nbcoolkid on 2017-12-27.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        String origin = "E:\\源码\\java\\alibaba-exam\\src";
        // File file = new File(origin);
        //
        // byte[] data = FileUtil.getAllBytes(file);
        //
        // Path path = Files.createTempDirectory("wasd");
        // File tmpFile = path.toFile();
        // System.out.println(tmpFile.getAbsolutePath());
        // OutputStream out = new FileOutputStream(tmpFile);
        // out.write(data);
        SFTPUtil.upload(origin, "/sduoj_sftp");
        System.out.println("wasdwasd");

    }

}

