package com.sjdddd.sojbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 消息提醒表
 * @TableName msg_remind
 */
@TableName(value ="msg_remind")
@Data
public class MsgRemind {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 动作类型，如Like_Post、Like_Discuss、Discuss、Reply等
     */
    private String action;

    /**
     * 消息来源ID，讨论ID或比赛ID
     */
    private Long sourceId;

    /**
     * 事件源类型：Discussion、Contest等
     */
    private String sourceType;

    /**
     * 事件源内容，如回复内容、评论标题等
     */
    private String sourceContent;

    /**
     * 引用上一级评论或回复ID
     */
    private Long quoteId;

    /**
     * 引用上一级类型：Comment、Reply
     */
    private String quoteType;

    /**
     * 事件发生地点链接
     */
    private String url;

    /**
     * 是否已读 0-未读 1-已读
     */
    private Integer state;

    /**
     * 操作者ID
     */
    private Long senderId;

    /**
     * 接收用户ID
     */
    private Long recipientId;

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
