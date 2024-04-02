package com.sjdddd.sojbackend.model.dto.question;

import com.sjdddd.sojbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 创建请求
 *

 */
@Data
public class QuestionCommentAddRequest implements Serializable {


    /**
     * 题目 id
     */
    private Long questionId;


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
