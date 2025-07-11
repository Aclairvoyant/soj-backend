package com.sjdddd.sojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import com.sjdddd.sojbackend.mapper.MsgRemindMapper;
import com.sjdddd.sojbackend.model.entity.MsgRemind;
import com.sjdddd.sojbackend.service.MsgRemindService;

import java.util.List;

import org.springframework.stereotype.Service;

/**
* @author shenjiadong
* @description 针对表【msg_remind(消息提醒表)】的数据库操作Service实现
* @createDate 2025-07-07 19:23:03
*/
@Service
public class MsgRemindServiceImpl extends ServiceImpl<MsgRemindMapper, MsgRemind>
    implements MsgRemindService {
    
    
    @Override
    public void addRemind(String action, Long sourceId, String sourceType, String sourceContent,
                          Long quoteId, String quoteType, String url,
                          Long senderId, Long recipientId) {
        MsgRemind remind = new MsgRemind();
        remind.setAction(action);
        remind.setSourceId(sourceId);
        remind.setSourceType(sourceType);
        remind.setSourceContent(sourceContent);
        remind.setQuoteId(quoteId);
        remind.setQuoteType(quoteType);
        remind.setUrl(url);
        remind.setSenderId(senderId);
        remind.setRecipientId(recipientId);
        remind.setState(0); // 未读
        remind.setIsDelete(0);
        this.save(remind);
    }

    @Override
    public List<MsgRemind> listByRecipientId(Long recipientId) {
        LambdaQueryWrapper<MsgRemind> query = new LambdaQueryWrapper<>();
        query.eq(MsgRemind::getRecipientId, recipientId)
             .eq(MsgRemind::getIsDelete, 0)
             .orderByDesc(MsgRemind::getCreateTime);
        return this.list(query);
    }

    @Override
    public Page<MsgRemind> pageByRecipientId(Long recipientId, int pageNum, int pageSize) {
        LambdaQueryWrapper<MsgRemind> query = new LambdaQueryWrapper<>();
        query.eq(MsgRemind::getRecipientId, recipientId)
             .eq(MsgRemind::getIsDelete, 0)
             .orderByDesc(MsgRemind::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), query);
    }

    @Override
    public boolean markAsRead(Long id, Long recipientId) {
        LambdaUpdateWrapper<MsgRemind> update = new LambdaUpdateWrapper<>();
        update.eq(MsgRemind::getId, id)
              .eq(MsgRemind::getRecipientId, recipientId)
              .set(MsgRemind::getState, 1);
        return this.update(update);
    }

    @Override
    public boolean markBatchAsRead(List<Long> ids, Long recipientId) {
        if (ids == null || ids.isEmpty()) return false;
        LambdaUpdateWrapper<MsgRemind> update = new LambdaUpdateWrapper<>();
        update.in(MsgRemind::getId, ids)
              .eq(MsgRemind::getRecipientId, recipientId)
              .set(MsgRemind::getState, 1);
        return this.update(update);
    }

    @Override
    public boolean markAllAsRead(Long recipientId) {
        LambdaUpdateWrapper<MsgRemind> update = new LambdaUpdateWrapper<>();
        update.eq(MsgRemind::getRecipientId, recipientId)
              .eq(MsgRemind::getIsDelete, 0)
              .eq(MsgRemind::getState, 0)
              .set(MsgRemind::getState, 1);
        return this.update(update);
    }

    @Override
    public Page<MsgRemind> pageUnreadByRecipientId(Long recipientId, int pageNum, int pageSize) {
        LambdaQueryWrapper<MsgRemind> query = new LambdaQueryWrapper<>();
        query.eq(MsgRemind::getRecipientId, recipientId)
             .eq(MsgRemind::getIsDelete, 0)
             .eq(MsgRemind::getState, 0)
             .orderByDesc(MsgRemind::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), query);
    }

    @Override
    public long getUnreadCount(Long recipientId) {
        LambdaQueryWrapper<MsgRemind> query = new LambdaQueryWrapper<>();
        query.eq(MsgRemind::getRecipientId, recipientId)
             .eq(MsgRemind::getIsDelete, 0)
             .eq(MsgRemind::getState, 0);
        return this.count(query);
    }

    @Override
    public long getTotalCount(Long recipientId) {
        LambdaQueryWrapper<MsgRemind> query = new LambdaQueryWrapper<>();
        query.eq(MsgRemind::getRecipientId, recipientId)
             .eq(MsgRemind::getIsDelete, 0);
        return this.count(query);
    }

    @Override
    public boolean deleteMessage(Long id, Long recipientId) {
        LambdaUpdateWrapper<MsgRemind> update = new LambdaUpdateWrapper<>();
        update.eq(MsgRemind::getId, id)
              .eq(MsgRemind::getRecipientId, recipientId)
              .set(MsgRemind::getIsDelete, 1);
        return this.update(update);
    }

    @Override
    public boolean batchDeleteMessage(List<Long> ids, Long recipientId) {
        if (ids == null || ids.isEmpty()) return false;
        LambdaUpdateWrapper<MsgRemind> update = new LambdaUpdateWrapper<>();
        update.in(MsgRemind::getId, ids)
              .eq(MsgRemind::getRecipientId, recipientId)
              .set(MsgRemind::getIsDelete, 1);
        return this.update(update);
    }

    @Override
    public boolean clearAllMessages(Long recipientId) {
        LambdaUpdateWrapper<MsgRemind> update = new LambdaUpdateWrapper<>();
        update.eq(MsgRemind::getRecipientId, recipientId)
              .eq(MsgRemind::getIsDelete, 0)
              .set(MsgRemind::getIsDelete, 1);
        return this.update(update);
    }
}




