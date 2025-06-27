package com.sjdddd.sojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.exception.BusinessException;
import com.sjdddd.sojbackend.mapper.*;
import com.sjdddd.sojbackend.model.dto.problemset.ProblemSetAddRequest;
import com.sjdddd.sojbackend.model.dto.problemset.ProblemSetQueryRequest;
import com.sjdddd.sojbackend.model.dto.problemset.ProblemSetUpdateRequest;
import com.sjdddd.sojbackend.model.entity.*;
import com.sjdddd.sojbackend.model.vo.ProblemSetVO;
import com.sjdddd.sojbackend.model.vo.QuestionVO;
import com.sjdddd.sojbackend.model.vo.UserVO;
import com.sjdddd.sojbackend.service.ProblemSetService;
import com.sjdddd.sojbackend.service.QuestionService;
import com.sjdddd.sojbackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProblemSetServiceImpl extends ServiceImpl<ProblemSetMapper, ProblemSet> implements ProblemSetService {
    @Resource
    private ProblemSetQuestionMapper problemSetQuestionMapper;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionService questionService;
    @Resource
    private UserService userService;

    private void checkPermission(ProblemSet problemSet, User user) {
        if (problemSet.getIsOfficial() == 1) {
            // 官方题单仅管理员可管理
            if (!userService.isAdmin(user)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限操作官方题单");
            }
        } else {
            // 用户题单仅创建者或管理员可管理
            if (!Objects.equals(problemSet.getUserId(), user.getId()) && !userService.isAdmin(user)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限操作该题单");
            }
        }
    }

    @Override
    @Transactional
    public Long addProblemSet(ProblemSetAddRequest addRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ProblemSet problemSet = new ProblemSet();
        BeanUtils.copyProperties(addRequest, problemSet);
        problemSet.setUserId(loginUser.getId());
        problemSet.setIsDelete(0);

        // 判断是否为管理员创建 1-管理员创建 0-用户创建
        String userRole = loginUser.getUserRole();
        if (userRole.equals("admin")) {
            problemSet.setIsOfficial(1);
        } else {
            problemSet.setIsOfficial(0);
        }
        // 是否为公开题单 0-私有 1-公开 默认私有
        problemSet.setIsPublic(request.getParameter("isPublic") == null ? 0 : 1);
        this.save(problemSet);

        // 题目关联
        if (addRequest.getQuestionIdList() != null && !addRequest.getQuestionIdList().isEmpty()) {
            int order = 0;
            for (Long qid : addRequest.getQuestionIdList()) {
                ProblemSetQuestion rel = new ProblemSetQuestion();
                rel.setProblemSetId(problemSet.getId());
                rel.setQuestionId(qid);
                rel.setSortOrder(order++);
                rel.setIsDelete(0);
                problemSetQuestionMapper.insert(rel);
            }
        }
        return problemSet.getId();
    }

    @Override
    @Transactional
    public Boolean deleteProblemSet(Long id, HttpServletRequest request) {
        ProblemSet problemSet = this.getById(id);
        if (problemSet == null || problemSet.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题单不存在");
        }
        User loginUser = userService.getLoginUser(request);
        checkPermission(problemSet, loginUser);
        problemSet.setIsDelete(1);
        this.updateById(problemSet);
        // 逻辑删除关联题目
        QueryWrapper<ProblemSetQuestion> qw = new QueryWrapper<>();
        qw.eq("questionSetId", id);
        List<ProblemSetQuestion> rels = problemSetQuestionMapper.selectList(qw);
        for (ProblemSetQuestion rel : rels) {
            rel.setIsDelete(1);
            problemSetQuestionMapper.updateById(rel);
        }
        return true;
    }

    @Override
    @Transactional
    public Boolean updateProblemSet(ProblemSetUpdateRequest updateRequest, HttpServletRequest request) {
        ProblemSet problemSet = this.getById(updateRequest.getId());
        if (problemSet == null || problemSet.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题单不存在");
        }
        User loginUser = userService.getLoginUser(request);
        checkPermission(problemSet, loginUser);
        BeanUtils.copyProperties(updateRequest, problemSet, "id", "userId", "createTime", "updateTime", "isDelete");
        this.updateById(problemSet);
        // 题目关联更新（全量覆盖）
        if (updateRequest.getQuestionIdList() != null) {
            QueryWrapper<ProblemSetQuestion> qw = new QueryWrapper<>();
            qw.eq("questionSetId", problemSet.getId());
            problemSetQuestionMapper.delete(qw);
            int order = 0;
            for (Long qid : updateRequest.getQuestionIdList()) {
                ProblemSetQuestion rel = new ProblemSetQuestion();
                rel.setProblemSetId(problemSet.getId());
                rel.setQuestionId(qid);
                rel.setSortOrder(order++);
                rel.setIsDelete(0);
                problemSetQuestionMapper.insert(rel);
            }
        }
        return true;
    }

    @Override
    public ProblemSetVO getProblemSetVO(Long id, HttpServletRequest request) {
        ProblemSet problemSet = this.getById(id);
        if (problemSet == null || problemSet.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题单不存在");
        }
        // 权限：私有题单仅创建者或管理员可见
        User loginUser = userService.getLoginUserPermitNull(request);
        if (problemSet.getIsPublic() == 0
                && (loginUser == null || (!Objects.equals(problemSet.getUserId(), loginUser.getId())
                && !userService.isAdmin(loginUser)))) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限查看该题单");
        }
        ProblemSetVO vo = new ProblemSetVO();
        BeanUtils.copyProperties(problemSet, vo);
        // 题目列表
        QueryWrapper<ProblemSetQuestion> qw = new QueryWrapper<>();
        qw.eq("questionSetId", id).eq("isDelete", 0).orderByAsc("sortOrder");
        List<ProblemSetQuestion> rels = problemSetQuestionMapper.selectList(qw);
        List<Long> qids = rels.stream().map(ProblemSetQuestion::getQuestionId).collect(Collectors.toList());
        List<QuestionVO> questionVOList = new ArrayList<>();
        if (!qids.isEmpty()) {
            List<Question> questions = questionMapper.selectBatchIds(qids);
            Map<Long, Question> qmap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));
            for (Long qid : qids) {
                Question q = qmap.get(qid);
                if (q != null && q.getIsDelete() == 0) {
                    questionVOList.add(questionService.getQuestionVO(q, request));
                }
            }
        }
        vo.setQuestionVOList(questionVOList);
        // 创建者信息
        User user = userService.getById(problemSet.getUserId());
        vo.setUserVO(userService.getUserVO(user));
        return vo;
    }

    @Override
    public Page<ProblemSetVO> listProblemSetVOByPage(ProblemSetQueryRequest queryRequest, HttpServletRequest request) {
        QueryWrapper<ProblemSet> qw = new QueryWrapper<>();
        if (queryRequest.getId() != null) qw.eq("id", queryRequest.getId());
        if (queryRequest.getName() != null) qw.like("name", queryRequest.getName());
        if (queryRequest.getIsPublic() != null) qw.eq("isPublic", queryRequest.getIsPublic());
        if (queryRequest.getIsOfficial() != null) qw.eq("isOfficial", queryRequest.getIsOfficial());
        if (queryRequest.getUserId() != null) qw.eq("userId", queryRequest.getUserId());
        qw.eq("isDelete", 0);
        Page<ProblemSet> page = this.page(new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize()), qw);
        Page<ProblemSetVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ProblemSetVO> voList = page.getRecords().stream().map(qs -> getProblemSetVO(qs.getId(), request)).collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<ProblemSetVO> listAllProblemSetVO(ProblemSetQueryRequest queryRequest) {
        QueryWrapper<ProblemSet> qw = new QueryWrapper<>();
        if (queryRequest.getId() != null) qw.eq("id", queryRequest.getId());
        if (queryRequest.getName() != null) qw.like("name", queryRequest.getName());
        if (queryRequest.getIsPublic() != null) qw.eq("isPublic", queryRequest.getIsPublic());
        if (queryRequest.getIsOfficial() != null) qw.eq("isOfficial", queryRequest.getIsOfficial());
        if (queryRequest.getUserId() != null) qw.eq("userId", queryRequest.getUserId());
        qw.eq("isDelete", 0);
        List<ProblemSet> list = this.list(qw);
        return list.stream().map(qs -> getProblemSetVO(qs.getId(), null)).collect(Collectors.toList());
    }

    // 题单题目管理接口（增删题目、调整顺序）
    @Override
    @Transactional
    public Boolean addQuestionToSet(Long setId, Long questionId, Integer sortOrder, HttpServletRequest request) {
        ProblemSet problemSet = this.getById(setId);
        if (problemSet == null || problemSet.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题单不存在");
        }
        User loginUser = userService.getLoginUser(request);
        checkPermission(problemSet, loginUser);
        // 检查题目是否已存在
        QueryWrapper<ProblemSetQuestion> qw = new QueryWrapper<>();
        qw.eq("questionSetId", setId).eq("questionId", questionId).eq("isDelete", 0);
        if (problemSetQuestionMapper.selectCount(qw) > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已在题单中");
        }
        ProblemSetQuestion rel = new ProblemSetQuestion();
        rel.setProblemSetId(setId);
        rel.setQuestionId(questionId);
        rel.setSortOrder(sortOrder);
        rel.setIsDelete(0);
        problemSetQuestionMapper.insert(rel);
        return true;
    }

    @Override
    @Transactional
    public Boolean removeQuestionFromSet(Long setId, Long questionId, HttpServletRequest request) {
        ProblemSet problemSet = this.getById(setId);
        if (problemSet == null || problemSet.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题单不存在");
        }
        User loginUser = userService.getLoginUser(request);
        checkPermission(problemSet, loginUser);
        QueryWrapper<ProblemSetQuestion> qw = new QueryWrapper<>();
        qw.eq("questionSetId", setId).eq("questionId", questionId).eq("isDelete", 0);
        ProblemSetQuestion rel = problemSetQuestionMapper.selectOne(qw);
        if (rel == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目未在题单中");
        }
        rel.setIsDelete(1);
        problemSetQuestionMapper.updateById(rel);
        return true;
    }

    @Override
    @Transactional
    public Boolean updateQuestionOrder(Long setId, Long questionId, Integer newOrder, HttpServletRequest request) {
        ProblemSet problemSet = this.getById(setId);
        if (problemSet == null || problemSet.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题单不存在");
        }
        User loginUser = userService.getLoginUser(request);
        checkPermission(problemSet, loginUser);
        QueryWrapper<ProblemSetQuestion> qw = new QueryWrapper<>();
        qw.eq("questionSetId", setId).eq("questionId", questionId).eq("isDelete", 0);
        ProblemSetQuestion rel = problemSetQuestionMapper.selectOne(qw);
        if (rel == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目未在题单中");
        }
        rel.setSortOrder(newOrder);
        problemSetQuestionMapper.updateById(rel);
        return true;
    }

    // 题单做题入口：返回题单题目VO列表（已排序）
    @Override
    public List<QuestionVO> getQuestionListForPractice(Long setId, HttpServletRequest request) {
        ProblemSet problemSet = this.getById(setId);
        if (problemSet == null || problemSet.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题单不存在");
        }
        // 权限：私有题单仅创建者或管理员可见
        User loginUser = userService.getLoginUserPermitNull(request);
        if (problemSet.getIsPublic() == 0 && (loginUser == null || (!Objects.equals(problemSet.getUserId(), loginUser.getId()) && !userService.isAdmin(loginUser)))) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限查看该题单");
        }
        QueryWrapper<ProblemSetQuestion> qw = new QueryWrapper<>();
        qw.eq("questionSetId", setId).eq("isDelete", 0).orderByAsc("sortOrder");
        List<ProblemSetQuestion> rels = problemSetQuestionMapper.selectList(qw);
        List<Long> qids = rels.stream().map(ProblemSetQuestion::getQuestionId).collect(Collectors.toList());
        List<QuestionVO> questionVOList = new ArrayList<>();
        if (!qids.isEmpty()) {
            List<Question> questions = questionMapper.selectBatchIds(qids);
            Map<Long, Question> qmap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));
            for (Long qid : qids) {
                Question q = qmap.get(qid);
                if (q != null && q.getIsDelete() == 0) {
                    questionVOList.add(questionService.getQuestionVO(q, request));
                }
            }
        }
        return questionVOList;
    }
}
