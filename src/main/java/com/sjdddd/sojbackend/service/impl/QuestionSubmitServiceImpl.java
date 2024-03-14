package com.sjdddd.sojbackend.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjdddd.sojbackend.common.BaseResponse;
import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.constant.CommonConstant;
import com.sjdddd.sojbackend.exception.BusinessException;
import com.sjdddd.sojbackend.judge.JudgeService;
import com.sjdddd.sojbackend.judge.codesandbox.CodeSandBox;
import com.sjdddd.sojbackend.judge.codesandbox.CodeSandBoxFactory;
import com.sjdddd.sojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import com.sjdddd.sojbackend.mapper.QuestionSubmitMapper;
import com.sjdddd.sojbackend.judge.codesandbox.model.JudgeInfo;
import com.sjdddd.sojbackend.model.dto.question.QuestionRunRequest;
import com.sjdddd.sojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.sjdddd.sojbackend.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.sjdddd.sojbackend.model.entity.Question;
import com.sjdddd.sojbackend.model.entity.QuestionSubmit;
import com.sjdddd.sojbackend.model.entity.User;
import com.sjdddd.sojbackend.model.enums.ExecuteCodeStatusEnum;
import com.sjdddd.sojbackend.model.enums.JudgeInfoMessageEnum;
import com.sjdddd.sojbackend.model.enums.QuestionSubmitLanguageEnum;
import com.sjdddd.sojbackend.model.enums.QuestionSubmitStatusEnum;
import com.sjdddd.sojbackend.model.vo.*;
import com.sjdddd.sojbackend.service.QuestionService;
import com.sjdddd.sojbackend.service.QuestionSubmitService;
import com.sjdddd.sojbackend.service.UserService;
import com.sjdddd.sojbackend.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author K
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2024-03-03 15:41:23
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService{

    @Value("${codesandbox.type:example}")
    private String type;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        long questionId = questionSubmitAddRequest.getQuestionId();

        // 判断编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言不合法");
        }

        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();

        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(questionSubmitAddRequest.getLanguage());
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");

        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }

        Long questionSubmitId = questionSubmit.getId();

        // 更新题目提交次数
        Integer submitNum = question.getSubmitNum();
        question.setSubmitNum(submitNum + 1);
        questionService.updateById(question);

        // 执行判题服务
        CompletableFuture.runAsync(() -> judgeService.doJudge(questionSubmitId));
        return questionSubmitId;
    }

    @Override
    public BaseResponse<PersonalDataVO> getPersonalData(User loginUser) {
        return null;
    }

    /**
     * 获取查询包装类
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();

        // 拼接查询语句
        queryWrapper.like(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.like(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.like(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);

        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);

        // 获取当前登录用户
        Long userId = loginUser.getId();

        // 非本人提交的代码 或者 也不是管理员
        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
//        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
//        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
//        if (CollectionUtils.isEmpty(questionSubmitList)) {
//            return questionSubmitVOPage;
//        }
//        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
//                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
//                .collect(Collectors.toList());
//        questionSubmitVOPage.setRecords(questionSubmitVOList);
//        return questionSubmitVOPage;
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
//        if (CollectionUtils.isEmpty(questionSubmitList)) {
//            return questionSubmitVOPage;
//        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(loginUser, userVO);
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(m -> {
                    QuestionSubmitVO questionSubmitVO = getQuestionSubmitVO(m, loginUser);
                    questionSubmitVO.setUserVO(userVO);
                    String judgeInfo = m.getJudgeInfo();
                    JudgeInfo bean = JSONUtil.toBean(judgeInfo, JudgeInfo.class);
                    String message = bean.getMessage();
//                    if (message != null) {
//                        questionSubmitVO.setJudgeInfo(Objects.requireNonNull(JudgeInfoMessageEnum.getEnumByValue(message)).getText());
//                        questionSubmitVO.setDetailsInfo(bean);
//                    } else {
//                        questionSubmitVO.setJudgeInfo("暂无判题信息");
//                        questionSubmitVO.setDetailsInfo(null);
//                    }
                    //获取判题信息，设置到对象中去
                    Long questionId = m.getQuestionId();
                    Question question = questionService.getById(questionId);
                    QuestionVO questionSubmitVO1 = new QuestionVO();
                    BeanUtils.copyProperties(question, questionSubmitVO1);
                    QuestionVO questionVO = questionSubmitVO1.objToVo(question);
                    questionSubmitVO.setQuestionVO(questionVO);
                    return questionSubmitVO;
                }).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    @Override
    public QuestionRunResultVO runQuestionSubmit(QuestionRunRequest questionRunRequest, User loginUser) {
        String code = questionRunRequest.getCode();
        String language = questionRunRequest.getLanguage();
        List<String> inputList = Collections.singletonList(questionRunRequest.getInput());
        List<String> outputList = Collections.singletonList(questionRunRequest.getOutput());

        CodeSandBox codeSandbox = CodeSandBoxFactory.newInstance(type);
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse response = codeSandbox.executeCode(executeCodeRequest);

        List<String> output = null;
        QuestionRunResultVO questionRunResultVO = new QuestionRunResultVO();
        if (Objects.equals(response.getMessage(), JudgeInfoMessageEnum.ACCEPTED.getText())) {
            output = Collections.singletonList(response.getOutputList().get(0));
            questionRunResultVO.setMessage(JudgeInfoMessageEnum.SUCCESS.getText());
            questionRunResultVO.setOutput(JSONUtil.toJsonStr(output));
        } else {
            questionRunResultVO.setMessage(response.getMessage());
        }

        return questionRunResultVO;
    }

}




