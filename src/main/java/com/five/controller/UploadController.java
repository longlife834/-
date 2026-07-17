package com.five.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping({"/api/upload", "/upload"})
public class UploadController {
    //接收头像图片，转为bases64Data url返回给前端 采用了Base64内联传输，避免静态文件存储和访问的额外开销
    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    @PostMapping("/avatar")
    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
       //如果用户没选文件或文件为空返回400错误
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文件不能为空");
        }
        try {
            //读取字节文件并判断类型
            byte[] bytes = file.getBytes();

            //获取浏览器提供的MIME类型
            String mimeType = file.getContentType();
            if (mimeType == null || !mimeType.startsWith("image/")) {
                String originalFilename = file.getOriginalFilename();
                String ext = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
                }
                mimeType = switch (ext) {
                    case "png" -> "image/png";
                    case "gif" -> "image/gif";
                    case "webp" -> "image/webp";
                    case "bmp" -> "image/bmp";
                    case "svg" -> "image/svg+xml";
                    default -> "image/jpeg";
                };
            }

            String base64 = Base64.getEncoder().encodeToString(bytes);
            String dataUrl = "data:" + mimeType + ";base64," + base64;

            log.info("头像上传成功: {} ({} bytes)", file.getOriginalFilename(), bytes.length);
            return ResponseEntity.ok(dataUrl);
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("上传失败: " + e.getMessage());
        }
    }
}
