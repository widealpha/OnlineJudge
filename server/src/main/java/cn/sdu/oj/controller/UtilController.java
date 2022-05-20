package cn.sdu.oj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/util")
@Controller
public class UtilController {
    String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        return "";
    }
}
