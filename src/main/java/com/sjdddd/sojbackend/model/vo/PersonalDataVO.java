package com.sjdddd.sojbackend.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 个人数据视图
 *
 */
@Data
public class PersonalDataVO implements Serializable {
    //用户提交了多少次
    private Long commitCount;

    //用户已经提交通过的题目数
    private Long questionSolveCount;

    //获取总题量
    private Long questionCount;

    private static final long serialVersionUID = 1L;

}
