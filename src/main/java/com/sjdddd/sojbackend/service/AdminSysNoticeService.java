package com.sjdddd.sojbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjdddd.sojbackend.model.entity.AdminSysNotice;
import java.util.List;

/**
* @author shenjiadong
* @description 针对表【admin_sys_notice(管理员通知表)】的数据库操作Service
* @createDate 2025-07-07 19:22:43
*/
public interface AdminSysNoticeService extends IService<AdminSysNotice> {

    // 获取通知列表
    List<AdminSysNotice> getNoticesByRecipientId(Long recipientId, Integer state);

    // 标记为已读
    boolean markAsPulled(Long id);
    
    // 发送通知给指定用户
    boolean sendNoticeToUser(Long recipientId, String title, String content, Long adminId, String type);
    
    // 发送通知给所有用户
    boolean sendNoticeToAll(String title, String content, Long adminId);
    
    // 批量发送通知
    boolean sendNoticeToUsers(List<Long> recipientIds, String title, String content, Long adminId);
    
    // 删除通知
    boolean deleteNotice(Long id, Long adminId);
    
    // 获取管理员发送的通知列表
    List<AdminSysNotice> getNoticesByAdminId(Long adminId);
}
