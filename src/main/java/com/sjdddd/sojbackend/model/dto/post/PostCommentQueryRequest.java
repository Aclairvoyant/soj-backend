package com.sjdddd.sojbackend.model.dto.post;

import com.sjdddd.sojbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *

 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostCommentQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 帖子 id
     */
    private Long postId;

    /**
     * 父级评论 id
     */
    private Long parentId;


    /**
     * 内容
     */
    private String content;


    /**
     * 创建用户 id
     */
    private Long userId;



    private static final long serialVersionUID = 1L;
}
