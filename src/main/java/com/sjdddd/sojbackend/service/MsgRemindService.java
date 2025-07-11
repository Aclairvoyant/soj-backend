package com.sjdddd.sojbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjdddd.sojbackend.model.entity.MsgRemind;
import java.util.List;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
* @author shenjiadong
* @description 针对表【msg_remind(消息提醒表)】的数据库操作Service
* @createDate 2025-07-07 19:23:03
*/
public interface MsgRemindService extends IService<MsgRemind> {
    
    void addRemind(String action, Long sourceId, String sourceType, String sourceContent,
                   Long quoteId, String quoteType, String url,
                   Long senderId, Long recipientId);

    List<MsgRemind> listByRecipientId(Long recipientId);

    Page<MsgRemind> pageByRecipientId(Long recipientId, int pageNum, int pageSize);

    boolean markAsRead(Long id, Long recipientId);

    boolean markBatchAsRead(List<Long> ids, Long recipientId);

    boolean markAllAsRead(Long recipientId);

    Page<MsgRemind> pageUnreadByRecipientId(Long recipientId, int pageNum, int pageSize);

    long getUnreadCount(Long recipientId);

    long getTotalCount(Long recipientId);

    boolean deleteMessage(Long id, Long recipientId);

    boolean batchDeleteMessage(List<Long> ids, Long recipientId);

    boolean clearAllMessages(Long recipientId);
}
