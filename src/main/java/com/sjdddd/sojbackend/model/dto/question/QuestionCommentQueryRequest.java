package com.sjdddd.sojbackend.model.dto.question;

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
public class QuestionCommentQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;


    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 搜索词
     */
    private String searchText;

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
