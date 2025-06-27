package com.sjdddd.sojbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题单
 * @TableName problem_set
 */
@TableName(value = "problem_set")
@Data
public class ProblemSet implements Serializable {
    /** 题单id */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 题单名称 */
    private String name;
    /** 题单描述 */
    private String description;
    /** 是否公开 0-私有 1-公开 */
    private Integer isPublic;
    /** 是否官方题单 0-用户题单 1-官方题单 */
    private Integer isOfficial;
    /** 创建者id */
    private Long userId;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
    /** 是否删除 */
    private Integer isDelete;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
