package com.sjdddd.sojbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sjdddd.sojbackend.model.entity.ProblemSetQuestion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author
 * @description 针对表【problem_set_question(题单-题目关联)】的数据库操作Mapper
 */
public interface ProblemSetQuestionMapper extends BaseMapper<ProblemSetQuestion> {
    
    /**
     * 根据题目ID查询包含该题目的所有题单ID
     * @param questionId 题目ID
     * @return 题单ID列表
     */
    List<Long> getProblemSetIdsByQuestionId(@Param("questionId") Long questionId);
} 