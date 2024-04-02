package com.sjdddd.sojbackend.model.vo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sjdddd.sojbackend.model.entity.Announcement;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 通知视图
 *

 */
@Data
public class AnnouncementVO implements Serializable {

    private final static Gson GSON = new Gson();

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;


    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 展示状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * 包装类转对象
     *
     * @param announcementVO
     * @return
     */
    public static Announcement voToObj(AnnouncementVO announcementVO) {
        if (announcementVO == null) {
            return null;
        }
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementVO, announcement);
        return announcement;
    }

    /**
     * 对象转包装类
     *
     * @param announcement
     * @return
     */
    public static AnnouncementVO objToVo(Announcement announcement) {
        if (announcement == null) {
            return null;
        }
        AnnouncementVO announcementVO = new AnnouncementVO();
        BeanUtils.copyProperties(announcement, announcementVO);

        return announcementVO;
    }
}
