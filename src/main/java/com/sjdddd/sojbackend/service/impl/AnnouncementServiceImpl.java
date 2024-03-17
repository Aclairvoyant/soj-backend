package com.sjdddd.sojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.constant.CommonConstant;
import com.sjdddd.sojbackend.exception.BusinessException;
import com.sjdddd.sojbackend.exception.ThrowUtils;
import com.sjdddd.sojbackend.mapper.AnnouncementMapper;
import com.sjdddd.sojbackend.model.dto.announcement.AnnouncementQueryRequest;
import com.sjdddd.sojbackend.model.entity.Announcement;
import com.sjdddd.sojbackend.model.entity.User;
import com.sjdddd.sojbackend.model.vo.AnnouncementVO;
import com.sjdddd.sojbackend.model.vo.UserVO;
import com.sjdddd.sojbackend.service.AnnouncementService;
import com.sjdddd.sojbackend.service.UserService;
import com.sjdddd.sojbackend.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author K
* @description 针对表【announcement(通知)】的数据库操作Service实现
* @createDate 2024-03-17 15:27:37
*/
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement>
    implements AnnouncementService {

    @Resource
    private UserService userService;


    @Override
    public void validAnnouncement(Announcement announcement, boolean add) {
        if (announcement == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = announcement.getTitle();
        String content = announcement.getContent();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param announcementQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Announcement> getQueryWrapper(AnnouncementQueryRequest announcementQueryRequest) {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        if (announcementQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = announcementQueryRequest.getSearchText();
        String sortField = announcementQueryRequest.getSortField();
        String sortOrder = announcementQueryRequest.getSortOrder();
        Long id = announcementQueryRequest.getId();
        String title = announcementQueryRequest.getTitle();
        String content = announcementQueryRequest.getContent();

        Long userId = announcementQueryRequest.getUserId();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("content", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);

        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public AnnouncementVO getAnnouncementVO(Announcement announcement, HttpServletRequest request) {
        AnnouncementVO announcementVO = AnnouncementVO.objToVo(announcement);
        // 1. 关联查询用户信息
        Long userId = announcement.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        announcementVO.setUser(userVO);

        return announcementVO;
    }

    @Override
    public Page<AnnouncementVO> getAnnouncementVOPage(Page<Announcement> announcementPage, HttpServletRequest request) {
        List<Announcement> announcementList = announcementPage.getRecords();
        Page<AnnouncementVO> announcementVOPage = new Page<>(announcementPage.getCurrent(), announcementPage.getSize(), announcementPage.getTotal());
        if (CollectionUtils.isEmpty(announcementList)) {
            return announcementVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = announcementList.stream().map(Announcement::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        List<AnnouncementVO> announcementVOList = announcementList.stream().map(announcement -> {
            AnnouncementVO announcementVO = AnnouncementVO.objToVo(announcement);
            Long userId = announcement.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            announcementVO.setUser(userService.getUserVO(user));

            return announcementVO;
        }).collect(Collectors.toList());
        announcementVOPage.setRecords(announcementVOList);
        return announcementVOPage;
    }

    @Override
    public List<Announcement> getByTitleOrContent(String titleOrContent) {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title", titleOrContent).or().like("content", titleOrContent);
        return baseMapper.selectList(queryWrapper);
    }

}




