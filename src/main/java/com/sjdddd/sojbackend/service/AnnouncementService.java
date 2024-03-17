package com.sjdddd.sojbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sjdddd.sojbackend.model.dto.announcement.AnnouncementQueryRequest;
import com.sjdddd.sojbackend.model.entity.Announcement;
import com.sjdddd.sojbackend.model.entity.Announcement;
import com.sjdddd.sojbackend.model.vo.AnnouncementVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
* @author K
* @description 针对表【announcement(通知)】的数据库操作Service
* @createDate 2024-03-17 15:27:37
*/
public interface AnnouncementService extends IService<Announcement> {
    /**
     * 校验
     *
     * @param announcement
     * @param add
     */
    void validAnnouncement(Announcement announcement, boolean add);

    /**
     * 获取查询条件
     *
     * @param announcementQueryRequest
     * @return
     */
    QueryWrapper<Announcement> getQueryWrapper(AnnouncementQueryRequest announcementQueryRequest);

    /**
     * 获取通知封装
     *
     * @param announcement
     * @param request
     * @return
     */
    AnnouncementVO getAnnouncementVO(Announcement announcement, HttpServletRequest request);

    /**
     * 分页获取通知封装
     *
     * @param announcementPage
     * @param request
     * @return
     */
    Page<AnnouncementVO> getAnnouncementVOPage(Page<Announcement> announcementPage, HttpServletRequest request);

    List<Announcement> getByTitleOrContent(String titleOrContent);
}
