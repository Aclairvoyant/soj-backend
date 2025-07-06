package com.sjdddd.sojbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("user_checkin")
public class UserCheckin implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Date checkinDate;

    private Date createTime;
} 