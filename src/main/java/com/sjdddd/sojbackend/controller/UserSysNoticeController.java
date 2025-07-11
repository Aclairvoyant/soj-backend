package com.sjdddd.sojbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sjdddd.sojbackend.common.BaseResponse;
import com.sjdddd.sojbackend.common.ResultUtils;
import com.sjdddd.sojbackend.model.entity.UserSysNotice;
import com.sjdddd.sojbackend.model.entity.AdminSysNotice;
import com.sjdddd.sojbackend.model.vo.UserSysNoticeVO;
import com.sjdddd.sojbackend.service.UserSysNoticeService;
import com.sjdddd.sojbackend.service.AdminSysNoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/userSysNotice")
public class UserSysNoticeController {

    @Resource
    private UserSysNoticeService userSysNoticeService;

    @Resource
    private AdminSysNoticeService adminSysNoticeService;

    // 分页查询用户系统通知
    @GetMapping("/page")
    public BaseResponse<Page<UserSysNoticeVO>> page(@RequestParam Long recipientId,
                                                    @RequestParam int pageNum,
                                                    @RequestParam int pageSize) {
        LambdaQueryWrapper<UserSysNotice> query = new LambdaQueryWrapper<>();
        query.eq(UserSysNotice::getRecipientId, recipientId)
             .eq(UserSysNotice::getIsDelete, 0)
             .orderByDesc(UserSysNotice::getCreateTime);
        Page<UserSysNotice> page = userSysNoticeService.page(new Page<>(pageNum, pageSize), query);
        Page<UserSysNoticeVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(notice -> {
            UserSysNoticeVO vo = new UserSysNoticeVO();
            BeanUtils.copyProperties(notice, vo);
            return vo;
        }).collect(Collectors.toList()));
        return ResultUtils.success(voPage);
    }

    // 删除用户系统通知（逻辑删除）
    @PostMapping("/delete")
    public BaseResponse<Boolean> delete(@RequestParam Long id, @RequestParam Long recipientId) {
        LambdaQueryWrapper<UserSysNotice> query = new LambdaQueryWrapper<>();
        query.eq(UserSysNotice::getId, id).eq(UserSysNotice::getRecipientId, recipientId);
        UserSysNotice update = new UserSysNotice();
        update.setIsDelete(1);
        boolean result = userSysNoticeService.update(update, query);
        return ResultUtils.success(result);
    }

    // 未读筛选分页
    @GetMapping("/unreadPage")
    public BaseResponse<Page<UserSysNoticeVO>> unreadPage(@RequestParam Long recipientId,
                                                         @RequestParam int pageNum,
                                                         @RequestParam int pageSize) {
        LambdaQueryWrapper<UserSysNotice> query = new LambdaQueryWrapper<>();
        query.eq(UserSysNotice::getRecipientId, recipientId)
             .eq(UserSysNotice::getIsDelete, 0)
             .eq(UserSysNotice::getState, 0)
             .orderByDesc(UserSysNotice::getCreateTime);
        Page<UserSysNotice> page = userSysNoticeService.page(new Page<>(pageNum, pageSize), query);
        Page<UserSysNoticeVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        // 联查系统通知内容
        List<Long> noticeIds = page.getRecords().stream().map(UserSysNotice::getNoticeId).collect(Collectors.toList());
        Map<Long, AdminSysNotice> noticeMap = new java.util.HashMap<>();
        if (!noticeIds.isEmpty()) {
            adminSysNoticeService.listByIds(noticeIds).forEach(n -> noticeMap.put(n.getId(), n));
        }
        voPage.setRecords(page.getRecords().stream().map(notice -> {
            UserSysNoticeVO vo = new UserSysNoticeVO();
            BeanUtils.copyProperties(notice, vo);
            AdminSysNotice sysNotice = noticeMap.get(notice.getNoticeId());
            if (sysNotice != null) {
                vo.setNoticeTitle(sysNotice.getTitle());
                vo.setNoticeContent(sysNotice.getContent());
                vo.setNoticeType(sysNotice.getType());
                vo.setNoticeCreateTime(sysNotice.getCreateTime());
            }
            return vo;
        }).collect(Collectors.toList()));
        return ResultUtils.success(voPage);
    }

    // 批量删除
    @PostMapping("/batchDelete")
    public BaseResponse<Boolean> batchDelete(@RequestBody List<Long> ids, @RequestParam Long recipientId) {
        LambdaQueryWrapper<UserSysNotice> query = new LambdaQueryWrapper<>();
        query.in(UserSysNotice::getId, ids).eq(UserSysNotice::getRecipientId, recipientId);
        UserSysNotice update = new UserSysNotice();
        update.setIsDelete(1);
        boolean result = userSysNoticeService.update(update, query);
        return ResultUtils.success(result);
    }

    // 批量标记为已读
    @PostMapping("/batchRead")
    public BaseResponse<Boolean> batchRead(@RequestBody List<Long> ids, @RequestParam Long recipientId) {
        LambdaQueryWrapper<UserSysNotice> query = new LambdaQueryWrapper<>();
        query.in(UserSysNotice::getId, ids).eq(UserSysNotice::getRecipientId, recipientId);
        UserSysNotice update = new UserSysNotice();
        update.setState(1);
        boolean result = userSysNoticeService.update(update, query);
        return ResultUtils.success(result);
    }
} 