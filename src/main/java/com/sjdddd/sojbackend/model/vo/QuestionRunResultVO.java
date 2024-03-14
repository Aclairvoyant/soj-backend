package com.sjdddd.sojbackend.model.vo;

import lombok.Data;

/**
 * @Author: 沈佳栋
 * @Description: TODO
 * @DateTime: 2024/3/13 21:36
 **/
@Data
public class QuestionRunResultVO {
    /**
     * 执行状态
     */
    private String message;


    /**
     * 执行结果
     */
    private String output;
}

