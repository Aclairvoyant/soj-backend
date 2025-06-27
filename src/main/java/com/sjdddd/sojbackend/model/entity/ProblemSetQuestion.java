package com.sjdddd.sojbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题单-题目关联
 * @TableName problem_set_question
 */
@TableName(value = "problem_set_question")
@Data
public class ProblemSetQuestion implements Serializable {
    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 题单id */
    private Long problemSetId;
    /** 题目id */
    private Long questionId;
    /** 排序 */
    private Integer sortOrder;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
    /** 是否删除 */
    private Integer isDelete;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
