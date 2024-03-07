package com.sjdddd.sojbackend.judge;

import cn.hutool.json.JSONUtil;
import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.exception.BusinessException;
import com.sjdddd.sojbackend.judge.codesandbox.CodeSandBox;
import com.sjdddd.sojbackend.judge.codesandbox.CodeSandBoxFactory;
import com.sjdddd.sojbackend.judge.codesandbox.CodeSandBoxProxy;
import com.sjdddd.sojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import com.sjdddd.sojbackend.judge.strategy.DefaultJudgeStrategy;
import com.sjdddd.sojbackend.judge.strategy.JavaJudgeStrategy;
import com.sjdddd.sojbackend.judge.strategy.JudgeContext;
import com.sjdddd.sojbackend.judge.strategy.JudgeStrategy;
import com.sjdddd.sojbackend.model.dto.question.JudgeCase;
import com.sjdddd.sojbackend.model.dto.question.JudgeConfig;
import com.sjdddd.sojbackend.model.dto.questionsubmit.JudgeInfo;
import com.sjdddd.sojbackend.model.entity.Question;
import com.sjdddd.sojbackend.model.entity.QuestionSubmit;
import com.sjdddd.sojbackend.model.enums.JudgeInfoMessageEnum;
import com.sjdddd.sojbackend.model.enums.QuestionSubmitLanguageEnum;
import com.sjdddd.sojbackend.model.enums.QuestionSubmitStatusEnum;
import com.sjdddd.sojbackend.model.vo.QuestionSubmitVO;
import com.sjdddd.sojbackend.service.QuestionService;
import com.sjdddd.sojbackend.service.QuestionSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: 沈佳栋
 * @Description: 判题服务
 * @DateTime: 2024/3/7 19:57
 **/
@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService{

    @Value("${codesandbox.type:example}")
    private String type;

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Override
    public QuestionSubmit doJudge(Long questionSubmitId) {
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }

        Long id = questionSubmit.getQuestionId();
        Question question = questionService.getById(id);
        if (question == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目信息不存在");
        }

        // 如果不为等待状态，不进行判题
        if (!Objects.equals(questionSubmit.getStatus(), QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }

        // 更新题目状态信息
        QuestionSubmit submitUpdate = new QuestionSubmit();
        submitUpdate.setId(questionSubmitId);
        submitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(submitUpdate);

        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题目状态信息失败");
        }

        // 判题
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();

        // 获取题目的测试用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();

        // 根据沙箱执行结果，设置判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);

        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // 修改数据库中的判题结果
        submitUpdate = new QuestionSubmit();
        submitUpdate.setId(questionSubmitId);
        submitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        submitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(submitUpdate);

        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题目状态信息失败");
        }

        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionSubmitId);

        log.info(String.valueOf(questionSubmitResult));
        return questionSubmitResult;
    }
}
