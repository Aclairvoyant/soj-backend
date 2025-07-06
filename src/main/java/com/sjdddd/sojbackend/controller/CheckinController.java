package com.sjdddd.sojbackend.controller;

import com.sjdddd.sojbackend.common.BaseResponse;
import com.sjdddd.sojbackend.common.ResultUtils;
import com.sjdddd.sojbackend.model.entity.User;
import com.sjdddd.sojbackend.service.UserCheckinService;
import com.sjdddd.sojbackend.service.UserService;

import io.swagger.annotations.Api;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
@Api(tags = "每日打卡接口")
public class CheckinController {

    @Resource
    private UserCheckinService userCheckinService;
    @Resource
    private UserService userService;

    @GetMapping("/list")
    public BaseResponse<Map<String, Object>> getCheckinList(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        Map<String, Object> result = new HashMap<>();
        result.put("checkInDays", userCheckinService.getCheckinDays(userId));
        result.put("todayChecked", userCheckinService.checkTodayChecked(userId));
        return ResultUtils.success(result);
    }

    @PostMapping("/do")
    public BaseResponse<Boolean> doCheckin(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        boolean success = userCheckinService.doCheckin(userId);
        return ResultUtils.success(success);
    }
} 