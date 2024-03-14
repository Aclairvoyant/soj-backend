package com.sjdddd.sojbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sjdddd.sojbackend.common.BaseResponse;
import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.common.ResultUtils;
import com.sjdddd.sojbackend.exception.BusinessException;
import com.sjdddd.sojbackend.model.entity.Question;
import com.sjdddd.sojbackend.model.entity.QuestionSolve;
import com.sjdddd.sojbackend.model.entity.QuestionSubmit;
import com.sjdddd.sojbackend.model.entity.User;
import com.sjdddd.sojbackend.model.vo.PersonalDataVO;
import com.sjdddd.sojbackend.service.QuestionService;
import com.sjdddd.sojbackend.service.QuestionSolveService;
import com.sjdddd.sojbackend.service.QuestionSubmitService;
import com.sjdddd.sojbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/question_solve")
@Slf4j
@Deprecated
public class QuestionSolveController {

    @Resource
    private QuestionSolveService questionSolveService;

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;


    /**
     * 新增用户已经解决的题目
     */
    @PostMapping("/createQuestionSolve")
    public BaseResponse<Boolean> createQuestionSolve(@RequestBody QuestionSolve questionSolve) {
        Long questionId = questionSolve.getQuestionId();
        Long userId = questionSolve.getUserId();
        long count = questionSolveService.count(new QueryWrapper<QuestionSolve>().eq("questionId", questionId).eq("userId", userId));
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "已经解决过该题目");
        }
        return ResultUtils.success(questionSolveService.save(questionSolve));
    }

    /**
     * 获取用户个人数据
     *
     */
    @GetMapping("/get/id")
    public BaseResponse<Question> getQuestionById(long questionId) {
        return ResultUtils.success(questionService.getById(questionId));
    }

    /**
     * 获取用户个人数据
     */
    @GetMapping("/question_submit/get/id")
    public BaseResponse<QuestionSubmit> getQuestionSubmitById(long questionSubmitId) {
        return ResultUtils.success(questionSubmitService.getById(questionSubmitId));
    }


    /**
     * 更新题目通过率
     * @param questionId
     * @return
     */
    @PostMapping("/question_submit/updateAccepted")
    public BaseResponse<Boolean> updateQuestionById(long questionId) {
        Question byId = questionService.getById(questionId);
        byId.setAcceptedNum(byId.getAcceptedNum() + 1);
        return ResultUtils.success(questionService.updateById(byId));
    }

    /**
     * 获取个人数据
     */
    @GetMapping("/getPersonalData")
    public BaseResponse<PersonalDataVO> getPersonalData(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionSolveService.getPersonalData(loginUser));
    }
}
