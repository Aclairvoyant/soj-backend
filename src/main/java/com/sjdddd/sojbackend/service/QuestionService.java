package com.sjdddd.sojbackend.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sjdddd.sojbackend.model.dto.question.QuestionQueryRequest;
import com.sjdddd.sojbackend.model.entity.Question;
import com.sjdddd.sojbackend.model.vo.QuestionCommentVO;
import com.sjdddd.sojbackend.model.vo.QuestionVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author K
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2024-03-03 15:40:37
*/
public interface QuestionService extends IService<Question> {
    /**
     * 校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    Page<QuestionVO> getQuestionVOPage(long current,
                                       long size,
                                       Wrapper<Question> wrapper,
                                       HttpServletRequest request);

    List<Question> getByTitleOrContent(String titleOrContent);

    String getQuestionAnswerById(Long questionId);

    List<QuestionCommentVO> getQuestionComment(Long questionId);
}
