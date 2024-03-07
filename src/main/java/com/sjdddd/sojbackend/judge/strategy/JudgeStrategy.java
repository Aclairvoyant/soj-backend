package com.sjdddd.sojbackend.judge.strategy;

import com.sjdddd.sojbackend.model.dto.questionsubmit.JudgeInfo;

/**
 * @Author: 沈佳栋
 * @Description: 判题策略（根据不同语言改变不同判题策略）
 * @DateTime: 2024/3/7 20:26
 **/
public interface JudgeStrategy {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
