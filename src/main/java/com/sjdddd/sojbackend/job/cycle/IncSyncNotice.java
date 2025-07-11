package com.sjdddd.sojbackend.job.cycle;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sjdddd.sojbackend.model.entity.AdminSysNotice;
import com.sjdddd.sojbackend.model.entity.User;
import com.sjdddd.sojbackend.model.entity.UserSysNotice;
import com.sjdddd.sojbackend.service.AdminSysNoticeService;
import com.sjdddd.sojbackend.service.UserService;
import com.sjdddd.sojbackend.service.UserSysNoticeService;


import java.util.stream.Collectors;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.annotation.Resource;

@Component
public class IncSyncNotice {

    @Resource
    private AdminSysNoticeService adminSysNoticeService;

    @Resource
    private UserSysNoticeService userSysNoticeService;

    @Resource
    private UserService userService;

    // 每隔五分钟执行：将最近半年内登录过的用户，推送最近半年内未读的系统通知
    @Scheduled(cron = "0 0/1 * * * *")
        public void syncNoticeToRecentHalfYearUser() {
            QueryWrapper<AdminSysNotice> noticeQuery = new QueryWrapper<>();
            noticeQuery.eq("state", 0).eq("isDelete", 0);
            List<AdminSysNotice> notices = adminSysNoticeService.list(noticeQuery);
            if (notices.isEmpty()) return;

            Date halfYearAgo = Date.from(LocalDateTime.now().minusMonths(6)
                    .atZone(ZoneId.systemDefault()).toInstant());

            QueryWrapper<User> userQuery = new QueryWrapper<>();
            userQuery.ge("lastLoginTime", halfYearAgo).eq("isDelete", 0);
            List<User> userList = userService.list(userQuery);

            for (AdminSysNotice notice : notices) {
                if ("All".equalsIgnoreCase(notice.getType())) {
                    List<UserSysNotice> userSysNoticeList = userList.stream().map(user -> {
                        UserSysNotice usn = new UserSysNotice();
                        usn.setNoticeId(notice.getId());
                        usn.setRecipientId(user.getId());
                        usn.setType("sys");
                        usn.setState(0);
                        usn.setIsDelete(0);
                        return usn;
                    }).collect(Collectors.toList());
                    userSysNoticeService.saveBatch(userSysNoticeList);
                    notice.setState(1); // 已推送
                } else if ("Single".equalsIgnoreCase(notice.getType()) && notice.getRecipientId() != null) {
                    UserSysNotice usn = new UserSysNotice();
                    usn.setNoticeId(notice.getId());
                    usn.setRecipientId(notice.getRecipientId());
                    usn.setType("mine");
                    usn.setState(0);
                    usn.setIsDelete(0);
                    userSysNoticeService.saveOrUpdate(usn,
                            new QueryWrapper<UserSysNotice>().eq("noticeId", notice.getId()).eq("recipientId", notice.getRecipientId()));
                    notice.setState(1);
                }
            }
            adminSysNoticeService.updateBatchById(notices);
        }
}