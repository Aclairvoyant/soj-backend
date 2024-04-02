package com.sjdddd.sojbackend.mapper;

import com.sjdddd.sojbackend.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sjdddd.sojbackend.model.vo.QuestionCommentVO;

import java.util.List;

/**
* @author K
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2024-03-03 15:40:37
* @Entity com.sjdddd.sojbackend.model.entity.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {

    String getQuestionAnswerById(Long questionId);

    List<QuestionCommentVO> getQuestionComment(Long questionId);
}




