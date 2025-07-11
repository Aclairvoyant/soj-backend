package com.sjdddd.sojbackend.model.vo;

import lombok.Data;
import java.util.Date;

@Data
public class UserSysNoticeVO {
    private Long id;
    private Long noticeId;
    private Long recipientId;
    private String type;
    private Integer state;
    private Date createTime;
    private Date updateTime;
    private String noticeTitle;
    private String noticeContent;
    private String noticeType;
    private Date noticeCreateTime;
} 