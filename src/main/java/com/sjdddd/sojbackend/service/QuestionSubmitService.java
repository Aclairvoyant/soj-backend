package com.sjdddd.sojbackend.service;

import com.sjdddd.sojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.sjdddd.sojbackend.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sjdddd.sojbackend.model.entity.User;

/**
* @author K
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-03-03 15:41:23
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

}
