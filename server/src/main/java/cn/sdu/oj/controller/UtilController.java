package cn.sdu.oj.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RequestMapping("/util")
@RestController
@Api(value = "工具接口", tags = "工具接口")
public class UtilController {

    @ApiOperation("上传文件")
    @PostMapping("/upload")
    @PreAuthorize("hasRole('COMMON')")
    String uploadImage(@ApiParam("上传的头像文件") @RequestPart MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }
        String filename = System.currentTimeMillis() + ".jpg";
        File localFile = new File("/home/share/image/");
        try {
            file.transferTo(localFile);
            return "http://121.196.101.7/image/" + filename;
        } catch (IOException e) {
            return null;
        }
    }


}
