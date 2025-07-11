package com.sjdddd.sojbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户系统通知表
 * @TableName user_sys_notice
 */
@TableName(value ="user_sys_notice")
@Data
public class UserSysNotice {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 系统通知ID
     */
    private Long noticeId;

    /**
     * 接收通知的用户ID
     */
    private Long recipientId;

    /**
     * 消息类型，sys-系统通知，mine-我的信息
     */
    private String type;

    /**
     * 是否已读 0-未读 1-已读
     */
    private Integer state;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除 0-否 1-是
     */
    private Integer isDelete;
}
