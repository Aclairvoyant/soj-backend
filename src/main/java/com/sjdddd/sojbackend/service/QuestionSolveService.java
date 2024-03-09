package com.sjdddd.sojbackend.service;

import com.sjdddd.sojbackend.model.entity.QuestionSolve;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sjdddd.sojbackend.model.entity.User;
import com.sjdddd.sojbackend.model.vo.PersonalDataVO;

/**
* @author K
* @description 针对表【question_solve(用户已经解决题目数)】的数据库操作Service
* @createDate 2024-03-03 15:41:29
*/
public interface QuestionSolveService extends IService<QuestionSolve> {
    boolean createQuestionSolve(QuestionSolve questionSolve);
    void updateQuestionSolvedCount(Long questionId);

    PersonalDataVO getPersonalData(User loginUser);
}
