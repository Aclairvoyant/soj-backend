package com.sjdddd.sojbackend.judge.strategy;

import com.sjdddd.sojbackend.model.dto.question.JudgeCase;
import com.sjdddd.sojbackend.judge.codesandbox.model.JudgeInfo;
import com.sjdddd.sojbackend.model.entity.Question;
import com.sjdddd.sojbackend.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * @Author: 沈佳栋
 * @Description: 上下文，传递的参数
 * @DateTime: 2024/3/7 20:31
 **/

@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
