package com.sjdddd.sojbackend.model.vo;

import lombok.Data;
import java.util.Date;

@Data
public class MsgRemindVO {
    private Long id;
    private String action;
    private Long sourceId;
    private String sourceType;
    private String sourceContent;
    private Long quoteId;
    private String quoteType;
    private String url;
    private Integer state;
    private Long senderId;
    private Long recipientId;
    private Date createTime;
    private Date updateTime;
    private String senderName;
    private String senderAvatar;
    // 可扩展：如发送者昵称、头像等
} 