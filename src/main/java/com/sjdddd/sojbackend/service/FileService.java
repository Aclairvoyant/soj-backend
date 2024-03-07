package com.sjdddd.sojbackend.service;

import org.springframework.web.multipart.MultipartFile;


public interface FileService {


    /**
     * 文件上传
     *
     * @param file 文件
     * @return {@link String}
     */
    String fileUpload(MultipartFile file);

    /**
     * 获得临时访问
     *
     * @param key 关键
     * @return {@link String}
     */
    String getTmpAccess(String key);
}
