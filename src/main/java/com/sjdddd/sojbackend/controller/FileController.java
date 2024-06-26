package com.sjdddd.sojbackend.controller;

import com.sjdddd.sojbackend.common.BaseResponse;
import com.sjdddd.sojbackend.common.ResultUtils;
import com.sjdddd.sojbackend.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@Api(tags = "文件接口")
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;


    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public BaseResponse<String> upload(@RequestPart MultipartFile file) {
        return ResultUtils.success(fileService.fileUpload(file));
    }

    @PostMapping("/tmp")
    @ApiOperation("获取临时文件访问链接")
    public BaseResponse<String> getTempAccess(String key) {
        return ResultUtils.success(fileService.getTmpAccess(key));
    }
}
