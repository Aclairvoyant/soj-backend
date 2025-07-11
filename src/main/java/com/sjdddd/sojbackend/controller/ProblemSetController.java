package com.sjdddd.sojbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sjdddd.sojbackend.annotation.AuthCheck;
import com.sjdddd.sojbackend.annotation.LoginCheck;
import com.sjdddd.sojbackend.common.BaseResponse;
import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.common.ResultUtils;
import com.sjdddd.sojbackend.constant.UserConstant;
import com.sjdddd.sojbackend.exception.BusinessException;

import com.sjdddd.sojbackend.model.dto.problemset.ProblemSetAddRequest;
import com.sjdddd.sojbackend.model.dto.problemset.ProblemSetQueryRequest;
import com.sjdddd.sojbackend.model.dto.problemset.ProblemSetUpdateRequest;
import com.sjdddd.sojbackend.model.vo.ProblemSetVO;
import com.sjdddd.sojbackend.model.vo.QuestionVO;
import com.sjdddd.sojbackend.service.ProblemSetService;
import com.sjdddd.sojbackend.service.ProblemSetStatService;
import com.sjdddd.sojbackend.service.UserService;
import com.sjdddd.sojbackend.model.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags = "题单管理")
@RequestMapping("/problemSet")
public class ProblemSetController {
    @Resource
    private ProblemSetService problemSetService;
    
    @Resource
    private ProblemSetStatService problemSetStatService;
    
    @Resource
    private UserService userService;

    @PostMapping("/add")
    @LoginCheck
    public BaseResponse<Long> addProblemSet(@RequestBody ProblemSetAddRequest addRequest, HttpServletRequest request) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = problemSetService.addProblemSet(addRequest, request);
        return ResultUtils.success(id);
    }

    @PostMapping("/delete")
    @LoginCheck
    public BaseResponse<Boolean> deleteProblemSet(@RequestParam Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = problemSetService.deleteProblemSet(id, request);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    @LoginCheck
    public BaseResponse<Boolean> updateProblemSet(@RequestBody ProblemSetUpdateRequest updateRequest, HttpServletRequest request) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = problemSetService.updateProblemSet(updateRequest, request);
        return ResultUtils.success(result);
    }

    @GetMapping("/get")
    public BaseResponse<ProblemSetVO> getProblemSetVO(@RequestParam Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProblemSetVO vo = problemSetService.getProblemSetVO(id, request);
        return ResultUtils.success(vo);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ProblemSetVO>> listProblemSetVOByPage(@RequestBody ProblemSetQueryRequest queryRequest, HttpServletRequest request) {
        Page<ProblemSetVO> voPage = problemSetService.listProblemSetVOByPage(queryRequest, request);
        return ResultUtils.success(voPage);
    }

    @PostMapping("/list/all/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<ProblemSetVO>> listAllQuestionSetVO(@RequestBody ProblemSetQueryRequest queryRequest) {
        List<ProblemSetVO> voList = problemSetService.listAllProblemSetVO(queryRequest);
        return ResultUtils.success(voList);
    }

    // 题单题目管理：添加题目
    @PostMapping("/addQuestion")
    public BaseResponse<Boolean> addQuestionToSet(@RequestParam Long setId, @RequestParam Long questionId, @RequestParam Integer sortOrder, HttpServletRequest request) {
        Boolean result = problemSetService.addQuestionToSet(setId, questionId, sortOrder, request);
        return ResultUtils.success(result);
    }

    // 题单题目管理：移除题目
    @PostMapping("/removeQuestion")
    public BaseResponse<Boolean> removeQuestionFromSet(@RequestParam Long setId, @RequestParam Long questionId, HttpServletRequest request) {
        Boolean result = problemSetService.removeQuestionFromSet(setId, questionId, request);
        return ResultUtils.success(result);
    }

    // 题单题目管理：调整题目顺序
    @PostMapping("/updateQuestionOrder")
    public BaseResponse<Boolean> updateQuestionOrder(@RequestParam Long setId, @RequestParam Long questionId, @RequestParam Integer newOrder, HttpServletRequest request) {
        Boolean result = problemSetService.updateQuestionOrder(setId, questionId, newOrder, request);
        return ResultUtils.success(result);
    }

    // 题单做题入口：获取题目列表
    @GetMapping("/practice")
    public BaseResponse<List<QuestionVO>> getQuestionListForPractice(@RequestParam Long setId, HttpServletRequest request) {
        List<QuestionVO> voList = problemSetService.getQuestionListForPractice(setId, request);
        return ResultUtils.success(voList);
    }
    
    // 题单进度：获取用户在题单中的完成统计（基于question_solve表）
    @GetMapping("/progress/stat")
    @LoginCheck
    public BaseResponse<ProblemSetStatService.ProblemSetProgressStat> getUserProgressStat(@RequestParam Long setId, HttpServletRequest request) {
        if (setId == null || setId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        ProblemSetStatService.ProblemSetProgressStat stat = problemSetStatService.getUserProgressStat(loginUser.getId(), setId);
        return ResultUtils.success(stat);
    }
}
