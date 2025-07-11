package com.sjdddd.sojbackend.controller;

import com.sjdddd.sojbackend.model.dto.notice.AdminSysNoticeAddRequest;
import com.sjdddd.sojbackend.model.entity.AdminSysNotice;
import com.sjdddd.sojbackend.model.entity.User;
import com.sjdddd.sojbackend.model.vo.AdminSysNoticeVO;
import com.sjdddd.sojbackend.service.AdminSysNoticeService;
import com.sjdddd.sojbackend.service.UserService;
import com.sjdddd.sojbackend.annotation.AuthCheck;
import com.sjdddd.sojbackend.annotation.LoginCheck;
import com.sjdddd.sojbackend.common.BaseResponse;
import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.common.ResultUtils;
import com.sjdddd.sojbackend.exception.ThrowUtils;
import com.sjdddd.sojbackend.service.UserSysNoticeService;
import com.sjdddd.sojbackend.model.entity.UserSysNotice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/adminSysNotice")
public class AdminSysNoticeController {

    @Resource
    private AdminSysNoticeService adminSysNoticeService;

    @Resource
    private UserSysNoticeService userSysNoticeService;
    
    @Resource
    private UserService userService;

    // 管理员发送通知（支持批量用户）
    @PostMapping("/send")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> sendNotice(@RequestBody AdminSysNoticeAddRequest request, HttpServletRequest httpRequest) {
        // 参数验证
        ThrowUtils.throwIf(StringUtils.isBlank(request.getTitle()), ErrorCode.PARAMS_ERROR, "通知标题不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(request.getContent()), ErrorCode.PARAMS_ERROR, "通知内容不能为空");
        
        // 获取当前管理员用户
        User currentUser = userService.getLoginUser(httpRequest);
        ThrowUtils.throwIf(currentUser == null, ErrorCode.NOT_LOGIN_ERROR);
        
        boolean result = false;
        if (request.getRecipientIdList() != null && !request.getRecipientIdList().isEmpty()) {
            // 批量推送
            result = adminSysNoticeService.sendNoticeToUsers(request.getRecipientIdList(), 
                request.getTitle(), request.getContent(), currentUser.getId());
        } else if (request.getRecipientId() != null) {
            // 单用户
            result = adminSysNoticeService.sendNoticeToUser(request.getRecipientId(), 
                request.getTitle(), request.getContent(), currentUser.getId(), "Single");
        } else {
            // 全部用户
            result = adminSysNoticeService.sendNoticeToAll(request.getTitle(), 
                request.getContent(), currentUser.getId());
        } 
        return ResultUtils.success(result);
    }

    // 管理员查看通知列表
    @GetMapping("/list")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<AdminSysNoticeVO>> getNotices(
            @RequestParam(required = false) Long recipientId,
            @RequestParam(required = false) Integer state,
            HttpServletRequest httpRequest) {
        
        User currentUser = userService.getLoginUser(httpRequest);
        List<AdminSysNotice> list;
        
        if (recipientId != null) {
            list = adminSysNoticeService.getNoticesByRecipientId(recipientId, state);
        } else {
            list = adminSysNoticeService.getNoticesByAdminId(currentUser.getId());
        }
        
        List<AdminSysNoticeVO> voList = list.stream().map(notice -> {
            AdminSysNoticeVO vo = new AdminSysNoticeVO();
            BeanUtils.copyProperties(notice, vo);
            return vo;
        }).collect(Collectors.toList());
        return ResultUtils.success(voList);
    }

    // 用户标记通知为已拉取
    @PostMapping("/pull")
    @LoginCheck
    public BaseResponse<Boolean> markAsPulled(@RequestParam Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "通知ID不能为空");
        boolean result = adminSysNoticeService.markAsPulled(id);
        return ResultUtils.success(result);
    }

    // 用户拉取题单相关通知
    @GetMapping("/listByProblemSet")
    @LoginCheck
    public BaseResponse<List<AdminSysNoticeVO>> listByProblemSet(@RequestParam Long problemSetId) {
        ThrowUtils.throwIf(problemSetId == null || problemSetId <= 0, ErrorCode.PARAMS_ERROR, "题单ID不能为空");
        
        List<AdminSysNotice> list = adminSysNoticeService.lambdaQuery()
            .eq(AdminSysNotice::getProblemSetId, problemSetId)
            .eq(AdminSysNotice::getIsDelete, 0)
            .orderByDesc(AdminSysNotice::getCreateTime)
            .list();
        List<AdminSysNoticeVO> voList = list.stream().map(notice -> {
            AdminSysNoticeVO vo = new AdminSysNoticeVO();
            BeanUtils.copyProperties(notice, vo);
            return vo;
        }).collect(Collectors.toList());
        return ResultUtils.success(voList);
    }
    
    // 管理员删除通知
    @PostMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteNotice(@RequestParam Long id, HttpServletRequest httpRequest) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "通知ID不能为空");
        
        User currentUser = userService.getLoginUser(httpRequest);
        boolean result = adminSysNoticeService.deleteNotice(id, currentUser.getId());
        return ResultUtils.success(result);
    }

    // 获取未读通知数量
    @GetMapping("/unreadCount")
    @LoginCheck
    public BaseResponse<Long> getUnreadCount(@RequestParam Long recipientId) {
        ThrowUtils.throwIf(recipientId == null || recipientId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        
        long count = adminSysNoticeService.lambdaQuery()
            .eq(AdminSysNotice::getRecipientId, recipientId)
            .eq(AdminSysNotice::getState, 0)
            .eq(AdminSysNotice::getIsDelete, 0)
            .count();
        return ResultUtils.success(count);
    }

}