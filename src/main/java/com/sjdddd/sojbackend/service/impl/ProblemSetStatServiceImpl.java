package com.sjdddd.sojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sjdddd.sojbackend.mapper.ProblemSetQuestionMapper;
import com.sjdddd.sojbackend.mapper.QuestionSolveMapper;
import com.sjdddd.sojbackend.model.entity.ProblemSetQuestion;
import com.sjdddd.sojbackend.service.ProblemSetStatService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 题单统计服务实现
 * @author sjdddd
 */
@Service
public class ProblemSetStatServiceImpl implements ProblemSetStatService {
    
    @Resource
    private ProblemSetQuestionMapper problemSetQuestionMapper;
    
    @Resource
    private QuestionSolveMapper questionSolveMapper;
    
    @Override
    public ProblemSetProgressStat getUserProgressStat(Long userId, Long problemSetId) {
        // 获取题单中的题目总数
        QueryWrapper<ProblemSetQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("problemSetId", problemSetId).eq("isDelete", 0);
        Long totalCount = problemSetQuestionMapper.selectCount(queryWrapper);
        Integer total = Math.toIntExact(totalCount);
        
        // 计算用户已完成的题目数量
        Integer completed = questionSolveMapper.countCompletedInProblemSet(userId, problemSetId);
        
        return new ProblemSetProgressStat(completed, total);
    }
}