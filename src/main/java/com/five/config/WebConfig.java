package com.five.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class WebConfig {
  //防止用户用户上传过大的头像撑爆服务器内存或磁盘
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //设置单个文件最大上传为5MB
        factory.setMaxFileSize(DataSize.ofMegabytes(5));
        //设置整个请求 文件加表单 最大为5MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(5));
        return factory.createMultipartConfig();
    }
}
