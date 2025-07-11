package com.sjdddd.sojbackend.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AdminSysNoticeVO {
    private Long id;
    private String title;
    private String content;
    private String type;
    private Integer state;
    private Long recipientId;
    private Long adminId;
    private Date createTime;
    private Date updateTime;
}