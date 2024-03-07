package com.sjdddd.sojbackend.service.impl;

import com.aliyun.oss.OSS;
import com.sjdddd.sojbackend.config.OssConfig;
import com.sjdddd.sojbackend.constant.FileConstant;
import com.sjdddd.sojbackend.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Resource
    private OssConfig ossConfig;


    @Resource
    private OSS ossClient;

    @Override
    public String fileUpload(MultipartFile file) {
        String bucketName = ossConfig.getBucketName();
        try {
            // 创建OSSClient实例。
            // 上传文件流。
            InputStream inputStream = file.getInputStream();
            String fileName = file.getOriginalFilename();
            //生成随机唯一值，使用uuid，添加到文件名称里面
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            fileName = uuid + fileName;
            //按照当前日期，创建文件夹，上传到创建文件夹里面
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            String timeUrl = df.format(new Date());
            fileName = timeUrl + "/" + System.currentTimeMillis() + "-" + fileName;
            //调用方法实现上传
            ossClient.putObject(bucketName, fileName, inputStream);
            return FileConstant.OSS_HOST + fileName;
        } catch (IOException e) {
            log.info("upload error", e);
            return null;
        }
    }


    @Override
    public String getTmpAccess(String key) {
        if (StringUtils.isBlank(key)) {
            return "";
        }
        if (key.startsWith("http")) {
            return key;
        }
        String bucketName = ossConfig.getBucketName();
        return ossClient.generatePresignedUrl(bucketName, key, new Date(System.currentTimeMillis() + 60 * 1000)).toString();
    }
}
