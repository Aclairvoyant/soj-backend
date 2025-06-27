package com.sjdddd.sojbackend.model.dto.questionsubmit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 提交题目请求
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSubmitAnalysisRequest implements Serializable {

    /**
     * 提交 id
     */
    private Long questionSubmitId;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 题目 id
     */
    private Long questionId;


    /**
     * 用户代码
     */
    private String code;


    /**
     * 编程语言
     */
    private String language;



    private static final long serialVersionUID = 1L;
}
