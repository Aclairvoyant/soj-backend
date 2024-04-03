package com.sjdddd.sojbackend.controller;

import com.sjdddd.sojbackend.common.BaseResponse;
import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.common.ResultUtils;
import com.sjdddd.sojbackend.exception.BusinessException;
import com.sjdddd.sojbackend.model.dto.postthumb.PostThumbAddRequest;
import com.sjdddd.sojbackend.model.entity.User;
import com.sjdddd.sojbackend.service.PostThumbService;
import com.sjdddd.sojbackend.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 帖子点赞接口
 */
@RestController
@RequestMapping("/post_thumb")
@Api(tags = "帖子点赞接口")
@Slf4j
public class PostThumbController {

    @Resource
    private PostThumbService postThumbService;

    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param postThumbAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    @ApiOperation("点赞 / 取消点赞")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest,
            HttpServletRequest request) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 检查是否已点赞
     *
     * @param postId
     * @param request
     * @return
     */
    @PostMapping("/check/{postId}")
    @ApiOperation("检查是否已点赞")
    public BaseResponse<Boolean> checkThumb(@PathVariable Long postId, HttpServletRequest request) {
        if (postId == null || postId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final User loginUser = userService.getLoginUser(request);
        boolean result = postThumbService.checkThumb(postId, loginUser);
        return ResultUtils.success(result);
    }
}
