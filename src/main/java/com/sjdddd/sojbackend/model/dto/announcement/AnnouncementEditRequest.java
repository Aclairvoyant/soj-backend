package com.sjdddd.sojbackend.model.dto.announcement;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑请求
 *

 */
@Data
public class AnnouncementEditRequest implements Serializable {

    /**
     * id
     */
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
     * 通知状态
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}
