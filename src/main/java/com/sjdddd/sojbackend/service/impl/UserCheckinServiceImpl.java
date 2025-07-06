package com.sjdddd.sojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjdddd.sojbackend.mapper.UserCheckinMapper;
import com.sjdddd.sojbackend.model.entity.UserCheckin;
import com.sjdddd.sojbackend.service.UserCheckinService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCheckinServiceImpl extends ServiceImpl<UserCheckinMapper, UserCheckin> implements UserCheckinService {

    @Override
    public List<String> getCheckinDays(Long userId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<UserCheckin> list = this.list(new QueryWrapper<UserCheckin>().eq("userId", userId));
        return list.stream().map(c -> sdf.format(c.getCheckinDate())).collect(Collectors.toList());
    }

    @Override
    public boolean checkTodayChecked(Long userId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        UserCheckin checkin = this.getOne(new QueryWrapper<UserCheckin>()
                .eq("userId", userId)
                .eq("checkinDate", today));
        return checkin != null;
    }

    @Override
    public boolean doCheckin(Long userId) {
        if (checkTodayChecked(userId)) return false;
        UserCheckin checkin = new UserCheckin();
        checkin.setUserId(userId);
        checkin.setCheckinDate(new Date());
        checkin.setCreateTime(new Date());
        return this.save(checkin);
    }
} 