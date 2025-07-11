package com.sjdddd.sojbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 管理员通知表
 * @TableName admin_sys_notice
 */
@TableName(value ="admin_sys_notice")
@Data
public class AdminSysNotice {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 指定发送用户类型
     */
    private String type;

    /**
     * 是否已拉取给用户 0-未拉取 1-已拉取
     */
    private Integer state;

    /**
     * 接收通知的用户ID
     */
    private Long recipientId;

    /**
     * 发送通知的管理员ID
     */
    private Long adminId;

    /**
     * 关联题单id
     */
    private Long problemSetId;

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
