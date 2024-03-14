package com.sjdddd.sojbackend.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * 自测题目请求
 *
 */
@Data
public class QuestionRunRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 测试用例输入
     */
    private String input;

    /**
     * 测试用例输出
     */
    private String output;

    private static final long serialVersionUID = 1L;
}
