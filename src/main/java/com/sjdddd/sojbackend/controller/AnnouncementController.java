package com.sjdddd.sojbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.sjdddd.sojbackend.annotation.AuthCheck;
import com.sjdddd.sojbackend.common.BaseResponse;
import com.sjdddd.sojbackend.common.DeleteRequest;
import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.common.ResultUtils;
import com.sjdddd.sojbackend.constant.UserConstant;
import com.sjdddd.sojbackend.exception.BusinessException;
import com.sjdddd.sojbackend.exception.ThrowUtils;
import com.sjdddd.sojbackend.model.dto.announcement.AnnouncementAddRequest;
import com.sjdddd.sojbackend.model.dto.announcement.AnnouncementEditRequest;
import com.sjdddd.sojbackend.model.dto.announcement.AnnouncementQueryRequest;
import com.sjdddd.sojbackend.model.dto.announcement.AnnouncementUpdateRequest;
import com.sjdddd.sojbackend.model.entity.Announcement;
import com.sjdddd.sojbackend.model.entity.User;
import com.sjdddd.sojbackend.model.vo.AnnouncementVO;
import com.sjdddd.sojbackend.service.AnnouncementService;
import com.sjdddd.sojbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 通知接口
 */
@RestController
@RequestMapping("/announcement")
@Slf4j
@CrossOrigin
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param announcementAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addAnnouncement(@RequestBody AnnouncementAddRequest announcementAddRequest, HttpServletRequest request) {
        if (announcementAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementAddRequest, announcement);
        announcementService.validAnnouncement(announcement, true);
        User loginUser = userService.getLoginUser(request);
        announcement.setUserId(loginUser.getId());
        boolean result = announcementService.save(announcement);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newAnnouncementId = announcement.getId();
        return ResultUtils.success(newAnnouncementId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteAnnouncement(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Announcement oldAnnouncement = announcementService.getById(id);
        ThrowUtils.throwIf(oldAnnouncement == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldAnnouncement.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = announcementService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param announcementUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAnnouncement(@RequestBody AnnouncementUpdateRequest announcementUpdateRequest) {
        if (announcementUpdateRequest == null || announcementUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementUpdateRequest, announcement);

        // 参数校验
        announcementService.validAnnouncement(announcement, false);
        long id = announcementUpdateRequest.getId();
        // 判断是否存在
        Announcement oldAnnouncement = announcementService.getById(id);
        ThrowUtils.throwIf(oldAnnouncement == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = announcementService.updateById(announcement);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<AnnouncementVO> getAnnouncementVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(announcementService.getAnnouncementVO(announcement, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param announcementQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<AnnouncementVO>> listAnnouncementVOByPage(@RequestBody AnnouncementQueryRequest announcementQueryRequest,
            HttpServletRequest request) {
        long current = announcementQueryRequest.getCurrent();
        long size = announcementQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Announcement> announcementPage = announcementService.page(new Page<>(current, size),
                announcementService.getQueryWrapper(announcementQueryRequest));
        return ResultUtils.success(announcementService.getAnnouncementVOPage(announcementPage, request));
    }

    /**
     * 获取所有可见的通知
     */
    @GetMapping("/getAll")
    public BaseResponse<List<Announcement>> getAllVisible() {
        List<Announcement> announcements = announcementService.getAllVisibleAnnouncements();
        return ResultUtils.success(announcements);
    }

    /**
     * 根据标题或者通知内容模糊查询
     */
    @GetMapping("/getByTitleOrContent")
    public BaseResponse<List<Announcement>> getByTitleOrContent(String titleOrContent) {
        return ResultUtils.success(announcementService.getByTitleOrContent(titleOrContent));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param announcementQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<AnnouncementVO>> listMyAnnouncementVOByPage(@RequestBody AnnouncementQueryRequest announcementQueryRequest,
            HttpServletRequest request) {
        if (announcementQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        announcementQueryRequest.setUserId(loginUser.getId());
        long current = announcementQueryRequest.getCurrent();
        long size = announcementQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Announcement> announcementPage = announcementService.page(new Page<>(current, size),
                announcementService.getQueryWrapper(announcementQueryRequest));
        return ResultUtils.success(announcementService.getAnnouncementVOPage(announcementPage, request));
    }



}
