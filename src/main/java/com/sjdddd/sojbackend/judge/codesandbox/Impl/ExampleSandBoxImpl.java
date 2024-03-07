package com.sjdddd.sojbackend.judge.codesandbox.Impl;

import com.sjdddd.sojbackend.judge.codesandbox.CodeSandBox;
import com.sjdddd.sojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import com.sjdddd.sojbackend.model.dto.questionsubmit.JudgeInfo;
import com.sjdddd.sojbackend.model.enums.JudgeInfoMessageEnum;
import com.sjdddd.sojbackend.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * @Author: 沈佳栋
 * @Description: 示例代码沙箱
 * @DateTime: 2024/3/6 22:02
 **/
public class ExampleSandBoxImpl implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行示例代码沙箱");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);

        return executeCodeResponse;
    }
}
