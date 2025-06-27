package com.sjdddd.sojbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sjdddd.sojbackend.model.dto.problemset.ProblemSetAddRequest;
import com.sjdddd.sojbackend.model.dto.problemset.ProblemSetQueryRequest;
import com.sjdddd.sojbackend.model.dto.problemset.ProblemSetUpdateRequest;
import com.sjdddd.sojbackend.model.entity.ProblemSet;
import com.sjdddd.sojbackend.model.vo.ProblemSetVO;
import com.sjdddd.sojbackend.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ProblemSetService extends IService<ProblemSet> {
    Long addProblemSet(ProblemSetAddRequest addRequest, HttpServletRequest request);
    Boolean deleteProblemSet(Long id, HttpServletRequest request);
    Boolean updateProblemSet(ProblemSetUpdateRequest updateRequest, HttpServletRequest request);
    ProblemSetVO getProblemSetVO(Long id, HttpServletRequest request);
    Page<ProblemSetVO> listProblemSetVOByPage(ProblemSetQueryRequest queryRequest, HttpServletRequest request);
    List<ProblemSetVO> listAllProblemSetVO(ProblemSetQueryRequest queryRequest);

    // 题单题目管理相关
    Boolean addQuestionToSet(Long setId, Long questionId, Integer sortOrder, HttpServletRequest request);
    Boolean removeQuestionFromSet(Long setId, Long questionId, HttpServletRequest request);
    Boolean updateQuestionOrder(Long setId, Long questionId, Integer newOrder, HttpServletRequest request);
    List<QuestionVO> getQuestionListForPractice(Long setId, HttpServletRequest request);
}
