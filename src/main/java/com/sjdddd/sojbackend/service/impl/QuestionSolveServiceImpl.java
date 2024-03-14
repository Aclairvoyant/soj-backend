package com.sjdddd.sojbackend.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.common.ResultUtils;
import com.sjdddd.sojbackend.exception.BusinessException;
import com.sjdddd.sojbackend.mapper.QuestionMapper;
import com.sjdddd.sojbackend.model.entity.Question;
import com.sjdddd.sojbackend.model.entity.QuestionSolve;
import com.sjdddd.sojbackend.model.entity.QuestionSubmit;
import com.sjdddd.sojbackend.model.entity.User;
import com.sjdddd.sojbackend.model.vo.PersonalDataVO;
import com.sjdddd.sojbackend.service.QuestionService;
import com.sjdddd.sojbackend.service.QuestionSolveService;
import com.sjdddd.sojbackend.mapper.QuestionSolveMapper;
import com.sjdddd.sojbackend.service.QuestionSubmitService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

/**
* @author K
* @description 针对表【question_solve(用户已经解决题目数)】的数据库操作Service实现
* @createDate 2024-03-03 15:41:29
*/
@Service
public class QuestionSolveServiceImpl extends ServiceImpl<QuestionSolveMapper, QuestionSolve>
    implements QuestionSolveService{
    @Resource
    @Lazy
    private QuestionSolveService questionSolveService;

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    public boolean createQuestionSolve(QuestionSolve questionSolve) {
        Long questionId = questionSolve.getQuestionId();
        Long userId = questionSolve.getUserId();

        // 检查是否已经解决过该题目
        int count = (int) this.count(new QueryWrapper<QuestionSolve>()
                .eq("questionId", questionId)
                .eq("userId", userId));
        if (count > 0) {
//            throw new BusinessException(ErrorCode.OPERATION_ERROR, "已经解决过该题目");
            return false;
        }

        return this.save(questionSolve);
    }

    @Override
    public void updateQuestionSolvedCount(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目信息不存在");
        }
        question.setAcceptedNum(question.getAcceptedNum() + 1);
        questionMapper.updateById(question);
    }

    @Override
    public PersonalDataVO getPersonalData(User loginUser) {
        Long id = loginUser.getId();
        //1、获取用户提交了多少次
        long commitCount = questionSubmitService.count(new QueryWrapper<QuestionSubmit>().eq("userId", id));
        //2、获取该用户已经提交通过的题目数
        long questionSolveCount = questionSolveService.count(new QueryWrapper<QuestionSolve>().eq("userId", id));
        //3、获取总题量
        long questionCount = questionService.count();
//        HashMap<String, Long> hashMap = new HashMap<>();
//        hashMap.put("commitCount", commitCount);
//        hashMap.put("questionSolveCount", questionSolveCount);
//        hashMap.put("questionCount", questionCount);
//        String jsonStr = JSONUtil.toJsonStr(hashMap);
//        PersonalDataVO personalDataVO = JSONUtil.toBean(jsonStr, PersonalDataVO.class);
        PersonalDataVO personalDataVO = new PersonalDataVO();
        personalDataVO.setCommitCount(commitCount);
        personalDataVO.setQuestionSolveCount(questionSolveCount);
        personalDataVO.setQuestionCount(questionCount);
        return personalDataVO;
    }

}




