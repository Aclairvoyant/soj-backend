package com.sjdddd.sojbackend.model.vo;

import lombok.Data;

@Data
public class PersonalDataVO {
    //用户提交了多少次
    private Long commitCount;
    //用户已经提交通过的题目数
    private Long questionSolveCount;
    //获取总题量
    private Long questionCount;

}
