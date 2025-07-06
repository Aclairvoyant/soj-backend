package com.sjdddd.sojbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjdddd.sojbackend.model.entity.UserCheckin;

import java.util.List;

public interface UserCheckinService extends IService<UserCheckin> {
    List<String> getCheckinDays(Long userId);
    boolean checkTodayChecked(Long userId);
    boolean doCheckin(Long userId);
} 