package com.sjdddd.sojbackend.mapper;

import com.sjdddd.sojbackend.model.entity.QuestionSolve;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author K
* @description 针对表【question_solve(用户已经解决题目数)】的数据库操作Mapper
* @createDate 2024-03-03 15:41:29
* @Entity com.sjdddd.sojbackend.model.entity.QuestionSolve
*/
public interface QuestionSolveMapper extends BaseMapper<QuestionSolve> {
    
    /**
     * 统计用户在指定题单中已完成的题目数量
     * @param userId 用户ID
     * @param problemSetId 题单ID
     * @return 完成数量
     */
    Integer countCompletedInProblemSet(@Param("userId") Long userId, @Param("problemSetId") Long problemSetId);
}




