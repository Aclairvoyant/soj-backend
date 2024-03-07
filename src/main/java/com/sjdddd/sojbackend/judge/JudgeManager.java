package com.sjdddd.sojbackend.judge;

import com.sjdddd.sojbackend.judge.strategy.DefaultJudgeStrategy;
import com.sjdddd.sojbackend.judge.strategy.JavaJudgeStrategy;
import com.sjdddd.sojbackend.judge.strategy.JudgeContext;
import com.sjdddd.sojbackend.judge.strategy.JudgeStrategy;
import com.sjdddd.sojbackend.model.dto.questionsubmit.JudgeInfo;
import com.sjdddd.sojbackend.model.entity.Question;
import com.sjdddd.sojbackend.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * @Author: 沈佳栋
 * @Description: 判题策略管理
 * @DateTime: 2024/3/7 20:55
 **/

@Service
public class JudgeManager {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
