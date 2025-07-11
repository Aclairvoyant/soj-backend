package com.sjdddd.sojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sjdddd.sojbackend.mapper.AdminSysNoticeMapper;
import com.sjdddd.sojbackend.model.entity.AdminSysNotice;
import com.sjdddd.sojbackend.model.entity.User;
import com.sjdddd.sojbackend.model.entity.UserSysNotice;
import com.sjdddd.sojbackend.service.AdminSysNoticeService;
import com.sjdddd.sojbackend.service.UserService;
import com.sjdddd.sojbackend.service.UserSysNoticeService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
* @author shenjiadong
* @description 针对表【admin_sys_notice(管理员通知表)】的数据库操作Service实现
* @createDate 2025-07-07 19:22:43
*/
@Service
public class AdminSysNoticeServiceImpl extends ServiceImpl<AdminSysNoticeMapper, AdminSysNotice>
    implements AdminSysNoticeService {

        @Resource
        private UserService userService;

        @Resource
        private UserSysNoticeService userSysNoticeService;

        @Override
        public List<AdminSysNotice> getNoticesByRecipientId(Long recipientId, Integer state) {
            LambdaQueryWrapper<AdminSysNotice> query = new LambdaQueryWrapper<>();
            query.eq(AdminSysNotice::getRecipientId, recipientId);
            if (state != null) {
                query.eq(AdminSysNotice::getState, state);
            }
            query.eq(AdminSysNotice::getIsDelete, 0);
            query.orderByDesc(AdminSysNotice::getCreateTime);
            return this.list(query);
        }
    
        @Override
        public boolean markAsPulled(Long id) {
            AdminSysNotice notice = this.getById(id);
            if (notice == null) return false;
            notice.setState(1);
            return this.updateById(notice);
        }

        @Override
        public boolean sendNoticeToUser(Long recipientId, String title, String content, Long adminId, String type) {
            AdminSysNotice notice = new AdminSysNotice();
            notice.setTitle(title);
            notice.setContent(content);
            notice.setType(type != null ? type : "Single");
            notice.setRecipientId(recipientId);
            notice.setAdminId(adminId);
            notice.setState(0);
            notice.setIsDelete(0);
            return this.save(notice);
        }

        @Override
        public boolean sendNoticeToAll(String title, String content, Long adminId) {
            AdminSysNotice notice = new AdminSysNotice();
            notice.setTitle(title);
            notice.setContent(content);
            notice.setType("All");
            notice.setAdminId(adminId);
            notice.setState(0);
            notice.setIsDelete(0);
            return this.save(notice);
        }

        @Override
        public boolean sendNoticeToUsers(List<Long> recipientIds, String title, String content, Long adminId) {
            if (recipientIds == null || recipientIds.isEmpty()) {
                return false;
            }
            
            List<AdminSysNotice> notices = recipientIds.stream().map(recipientId -> {
                AdminSysNotice notice = new AdminSysNotice();
                notice.setTitle(title);
                notice.setContent(content);
                notice.setType("Single");
                notice.setRecipientId(recipientId);
                notice.setAdminId(adminId);
                notice.setState(0);
                notice.setIsDelete(0);
                return notice;
            }).collect(Collectors.toList());
            
            return this.saveBatch(notices);
        }

        @Override
        public boolean deleteNotice(Long id, Long adminId) {
            LambdaQueryWrapper<AdminSysNotice> query = new LambdaQueryWrapper<>();
            query.eq(AdminSysNotice::getId, id)
                 .eq(AdminSysNotice::getAdminId, adminId);
            
            AdminSysNotice notice = this.getOne(query);
            if (notice == null) return false;
            
            notice.setIsDelete(1);
            return this.updateById(notice);
        }

        @Override
        public List<AdminSysNotice> getNoticesByAdminId(Long adminId) {
            LambdaQueryWrapper<AdminSysNotice> query = new LambdaQueryWrapper<>();
            query.eq(AdminSysNotice::getAdminId, adminId)
                 .eq(AdminSysNotice::getIsDelete, 0)
                 .orderByDesc(AdminSysNotice::getCreateTime);
            return this.list(query);
        }
        
}




