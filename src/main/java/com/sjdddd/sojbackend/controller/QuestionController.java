package com.sjdddd.sojbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.sjdddd.sojbackend.annotation.AuthCheck;
import com.sjdddd.sojbackend.common.BaseResponse;
import com.sjdddd.sojbackend.common.DeleteRequest;
import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.common.ResultUtils;
import com.sjdddd.sojbackend.constant.UserConstant;
import com.sjdddd.sojbackend.exception.BusinessException;
import com.sjdddd.sojbackend.exception.ThrowUtils;
import com.sjdddd.sojbackend.manager.RedisLimiterManager;
import com.sjdddd.sojbackend.model.dto.question.*;
import com.sjdddd.sojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.sjdddd.sojbackend.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.sjdddd.sojbackend.model.entity.*;
import com.sjdddd.sojbackend.model.vo.*;
import com.sjdddd.sojbackend.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目接口
 */
@RestController
@RequestMapping("/question")
@Api(tags = "题目接口")
@Slf4j
@CrossOrigin
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionSolveService questionSolveService;

    @Resource
    private QuestionCommentService questionCommentService;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private RedisTemplate<String, Object> redisObjTemplate;

    private final static Gson GSON = new Gson();

    // region 增删改查

    @ApiOperation("获取题目答案")
    @GetMapping("/getQuestionAnswer")
    public BaseResponse<String> getQuestionAnswer(Long questionId, HttpServletRequest request) {
        // 如果通过该题目后，才可获取题目答案
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 查询该用户是否已经解决过该题目
        long count = questionSolveService.count(new QueryWrapper<QuestionSolve>()
                .eq("questionId", questionId)
                .eq("userId", userService.getLoginUser(request).getId()));
        if (count <= 0) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无法查看答案，请先尝试解决该题目");
        }
        return ResultUtils.success(question.getAnswer());
    }

    /**
     * 创建（仅管理员）
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增题目")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }

        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }

        questionService.validQuestion(question, true);
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除（仅管理员）
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation("删除题目")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        // 删除提交记录
        boolean submitRemove = questionSubmitService.remove(new QueryWrapper<QuestionSubmit>().eq("questionId", id));
        // 删除评论
        boolean commentRemove = questionCommentService.remove(new QueryWrapper<QuestionComment>().eq("questionId", id));
        return ResultUtils.success(b && submitRemove && commentRemove);
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @ApiOperation("更新题目")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionUpdateRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }

        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取 脱敏
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    @ApiOperation("根据id获取题目详情(脱敏)")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @ApiOperation("根据id获取题目详情")
    public BaseResponse<Question> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 仅本人或管理员可查看
        if (!question.getUserId().equals(loginUser.getId()) && userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(question);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    @ApiOperation("分页获取题目列表")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<Question> questionPage = questionService.page(new Page<>(current, size),
//                questionService.getQueryWrapper(questionQueryRequest));
//        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));

        String cacheKey = "questionsPage:" + current + ":" + size;
        ValueOperations<String, Object> valueOperations = redisObjTemplate.opsForValue();

        // 尝试从缓存获取数据
        Page<QuestionVO> cachedPage = (Page<QuestionVO>) valueOperations.get(cacheKey);
        if (cachedPage != null) {
            return ResultUtils.success(cachedPage);
        }

        // 缓存未命中，查询数据库
        Page<Question> questionPage = questionService.page(
                new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest)
        );
        Page<QuestionVO> voPage = questionService.getQuestionVOPage(questionPage, request);

        // 将结果存入Redis
        valueOperations.set(cacheKey, voPage);

        return ResultUtils.success(voPage);
    }

    /**
     * 获取所有题目
     */
    @GetMapping("/getAll")
    @ApiOperation("获取所有题目")
    public BaseResponse<List<Question>> getAll() {
        return ResultUtils.success(questionService.list());
    }

    /**
     * 根据标题或者题目内容模糊查询
     */
    @GetMapping("/getByTitleOrContent")
    @ApiOperation("根据标题或者题目内容模糊查询")
    public BaseResponse<List<Question>> getByTitleOrContent(String titleOrContent) {
        return ResultUtils.success(questionService.getByTitleOrContent(titleOrContent));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    @ApiOperation("获取当前用户题目列表")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    @ApiOperation("分页获取题目列表（仅管理员）")
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                           HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @Deprecated
    @PostMapping("/edit")
    @ApiOperation("编辑题目（用户）")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }

        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }

        // 参数校验
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return 提交记录id
     */
    @PostMapping("/question_submit/do")
    @ApiOperation("提交题目")
    public BaseResponse doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                         HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交
        final User loginUser = userService.getLoginUser(request);
        // 限流
        boolean rateLimit = redisLimiterManager.doRateLimit(loginUser.getId().toString());
        if (!rateLimit) {
            return ResultUtils.error(ErrorCode.TOO_MANY_REQUEST, "提交过于频繁,请稍后重试");
        }
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }


    /**
     * 分页获取题目提交列表
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @PostMapping("/question_submit/list/page")
    @ApiOperation("分页获取题目提交列表")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();

        // 原始信息
        Page<QuestionSubmit> questionSum = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSum, loginUser));
    }

    /**
     * 新增用户已经解决的题目
     */
    @PostMapping("/createQuestionSolve")
    @ApiOperation("新增用户已经解决的题目")
    public BaseResponse<Boolean> createQuestionSolve(@RequestBody QuestionSolve questionSolve) {
        Long questionId = questionSolve.getQuestionId();
        Long userId = questionSolve.getUserId();
        long count = questionSolveService.count(new QueryWrapper<QuestionSolve>().eq("questionId", questionId).eq("userId", userId));
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "已经解决过该题目");
        }
        return ResultUtils.success(questionSolveService.save(questionSolve));
    }

    /**
     * 获取用户个人数据
     */
    @GetMapping("/get/id")
    @ApiOperation("获取用户个人数据")
    public BaseResponse<Question> getQuestionById(long questionId) {
        return ResultUtils.success(questionService.getById(questionId));
    }

    /**
     * 获取用户题目提交数据
     */
    @GetMapping("/question_submit/get/id")
    @ApiOperation("获取用户题目提交数据")
    public BaseResponse<QuestionSubmit> getQuestionSubmitById(long questionSubmitId) {
        return ResultUtils.success(questionSubmitService.getById(questionSubmitId));
    }


    /**
     * 更新题目通过率
     *
     * @param questionId
     * @return
     */
    @PostMapping("/question_submit/updateAccepted")
    @ApiOperation("更新题目通过率")
    public BaseResponse<Boolean> updateQuestionById(long questionId) {
        Question byId = questionService.getById(questionId);
        byId.setAcceptedNum(byId.getAcceptedNum() + 1);
        return ResultUtils.success(questionService.updateById(byId));
    }


    /**
     * 自测题目
     *
     * @param questionRunRequest
     * @param request
     * @return 提交记录id
     */
    @PostMapping("/question_submit/run")
    @ApiOperation("运行题目自测")
    public BaseResponse<QuestionRunResultVO> runQuestionSubmit(@RequestBody QuestionRunRequest questionRunRequest,
                                                               HttpServletRequest request) {
        if (questionRunRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交
        final User loginUser = userService.getLoginUser(request);
        QuestionRunResultVO questionRunResultVO = questionSubmitService.runQuestionSubmit(questionRunRequest, loginUser);
        return ResultUtils.success(questionRunResultVO);
    }


    /**
     * 获取题目评论
     */
    @GetMapping("/getQuestionComment")
    @ApiOperation("获取题目评论")
    public BaseResponse<List<QuestionCommentVO>> getQuestionComment(Long questionId) {
        return ResultUtils.success(questionService.getQuestionComment(questionId));
    }

    /**
     * 新增题目评论
     */
    @PostMapping("/addQuestionComment")
    @ApiOperation("新增题目评论")
    public BaseResponse<Long> addQuestionComment(@RequestBody QuestionCommentAddRequest questionCommentAddRequest, HttpServletRequest request) {
        if (questionCommentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionComment questionComment = new QuestionComment();
        BeanUtils.copyProperties(questionCommentAddRequest, questionComment);

        questionCommentService.validComment(questionComment, true);
        User loginUser = userService.getLoginUser(request);
        questionComment.setUserId(loginUser.getId());

        boolean result = questionCommentService.save(questionComment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newCommentId = questionComment.getId();
        return ResultUtils.success(newCommentId);
    }

}
