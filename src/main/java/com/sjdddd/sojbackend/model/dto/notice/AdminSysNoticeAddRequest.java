package com.sjdddd.sojbackend.model.dto.notice;

import lombok.Data;

@Data
public class AdminSysNoticeAddRequest {
    private String title;
    private String content;
    private String type;
    private Long recipientId;
    private Long adminId;
    private Long problemSetId;
    private java.util.List<Long> recipientIdList;
}