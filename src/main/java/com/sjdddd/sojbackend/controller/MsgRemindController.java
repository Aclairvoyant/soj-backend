package com.sjdddd.sojbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sjdddd.sojbackend.annotation.AuthCheck;
import com.sjdddd.sojbackend.common.BaseResponse;
import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.common.ResultUtils;
import com.sjdddd.sojbackend.constant.UserConstant;
import com.sjdddd.sojbackend.exception.BusinessException;
import com.sjdddd.sojbackend.model.entity.MsgRemind;
import com.sjdddd.sojbackend.model.entity.User;
import com.sjdddd.sojbackend.model.vo.MsgRemindVO;
import com.sjdddd.sojbackend.service.MsgRemindService;
import com.sjdddd.sojbackend.service.UserService;
import com.sjdddd.sojbackend.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/msgRemind")
@AuthCheck
public class MsgRemindController {

    @Resource
    private MsgRemindService msgRemindService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;

    // 用户获取自己的消息提醒列表
    @GetMapping("/list")
    public BaseResponse<List<MsgRemindVO>> list(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        List<MsgRemind> list = msgRemindService.listByRecipientId(loginUser.getId());
        // 封装VO，带发送者昵称头像
        Set<Long> senderIds = list.stream().map(MsgRemind::getSenderId).collect(Collectors.toSet());
        Map<Long, User> userMap = new java.util.HashMap<>();
        if (!senderIds.isEmpty()) {
            userMapper.selectBatchIds(senderIds).forEach(u -> userMap.put(u.getId(), u));
        }
        List<MsgRemindVO> voList = list.stream().map(remind -> {
            MsgRemindVO vo = new MsgRemindVO();
            BeanUtils.copyProperties(remind, vo);
            User sender = userMap.get(remind.getSenderId());
            if (sender != null) {
                vo.setSenderName(sender.getUserName());
                vo.setSenderAvatar(sender.getUserAvatar());
            }
            return vo;
        }).collect(Collectors.toList());
        return ResultUtils.success(voList);
    }

    // 分页获取消息提醒列表
    @GetMapping("/page")
    public BaseResponse<Page<MsgRemindVO>> page(@RequestParam(defaultValue = "1") int pageNum,
                                                @RequestParam(defaultValue = "10") int pageSize,
                                                HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Page<MsgRemind> page = msgRemindService.pageByRecipientId(loginUser.getId(), pageNum, pageSize);
        Page<MsgRemindVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        Set<Long> senderIds = page.getRecords().stream().map(MsgRemind::getSenderId).collect(Collectors.toSet());
        Map<Long, User> userMap = new java.util.HashMap<>();
        if (!senderIds.isEmpty()) {
            userMapper.selectBatchIds(senderIds).forEach(u -> userMap.put(u.getId(), u));
        }
        voPage.setRecords(page.getRecords().stream().map(remind -> {
            MsgRemindVO vo = new MsgRemindVO();
            BeanUtils.copyProperties(remind, vo);
            User sender = userMap.get(remind.getSenderId());
            if (sender != null) {
                vo.setSenderName(sender.getUserName());
                vo.setSenderAvatar(sender.getUserAvatar());
            }
            return vo;
        }).collect(Collectors.toList()));
        return ResultUtils.success(voPage);
    }

    // 获取未读消息数量
    @GetMapping("/unreadCount")
    public BaseResponse<Long> getUnreadCount(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        long count = msgRemindService.getUnreadCount(loginUser.getId());
        return ResultUtils.success(count);
    }

    // 获取消息总数
    @GetMapping("/totalCount")
    public BaseResponse<Long> getTotalCount(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        long count = msgRemindService.getTotalCount(loginUser.getId());
        return ResultUtils.success(count);
    }

    // 标记为已读
    @PostMapping("/read")
    public BaseResponse<Boolean> markAsRead(@RequestParam Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        boolean result = msgRemindService.markAsRead(id, loginUser.getId());
        return ResultUtils.success(result);
    }

    // 批量标记为已读
    @PostMapping("/batchRead")
    public BaseResponse<Boolean> markBatchAsRead(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        List<Long> ids = (List<Long>) req.get("ids");
        boolean result = msgRemindService.markBatchAsRead(ids, loginUser.getId());
        return ResultUtils.success(result);
    }

    // 标记全部为已读
    @PostMapping("/readAll")
    public BaseResponse<Boolean> markAllAsRead(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        boolean result = msgRemindService.markAllAsRead(loginUser.getId());
        return ResultUtils.success(result);
    }

    // 删除消息
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteMessage(@RequestParam Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        boolean result = msgRemindService.deleteMessage(id, loginUser.getId());
        return ResultUtils.success(result);
    }

    // 批量删除消息
    @PostMapping("/batchDelete")
    public BaseResponse<Boolean> batchDeleteMessage(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        List<Long> ids = (List<Long>) req.get("ids");
        boolean result = msgRemindService.batchDeleteMessage(ids, loginUser.getId());
        return ResultUtils.success(result);
    }

    // 未读筛选分页
    @GetMapping("/unreadPage")
    public BaseResponse<Page<MsgRemindVO>> unreadPage(@RequestParam(defaultValue = "1") int pageNum,
                                                      @RequestParam(defaultValue = "10") int pageSize,
                                                      HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Page<MsgRemind> page = msgRemindService.pageUnreadByRecipientId(loginUser.getId(), pageNum, pageSize);
        Page<MsgRemindVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        // 批量查用户
        Set<Long> senderIds = page.getRecords().stream().map(MsgRemind::getSenderId).collect(Collectors.toSet());
        Map<Long, User> userMap = new java.util.HashMap<>();
        if (!senderIds.isEmpty()) {
            userMapper.selectBatchIds(senderIds).forEach(u -> userMap.put(u.getId(), u));
        }
        voPage.setRecords(page.getRecords().stream().map(remind -> {
            MsgRemindVO vo = new MsgRemindVO();
            BeanUtils.copyProperties(remind, vo);
            User sender = userMap.get(remind.getSenderId());
            if (sender != null) {
                vo.setSenderName(sender.getUserName());
                vo.setSenderAvatar(sender.getUserAvatar());
            }
            return vo;
        }).collect(Collectors.toList()));
        return ResultUtils.success(voPage);
    }
} 