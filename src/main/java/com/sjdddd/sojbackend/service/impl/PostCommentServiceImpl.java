package com.sjdddd.sojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.exception.BusinessException;
import com.sjdddd.sojbackend.exception.ThrowUtils;
import com.sjdddd.sojbackend.mapper.PostCommentMapper;
import com.sjdddd.sojbackend.model.entity.PostComment;
import com.sjdddd.sojbackend.model.entity.QuestionComment;
import com.sjdddd.sojbackend.model.vo.PostCommentVO;
import com.sjdddd.sojbackend.model.vo.QuestionCommentVO;
import com.sjdddd.sojbackend.service.PostCommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author K
* @description 针对表【post_comment(帖子评论)】的数据库操作Service实现
* @createDate 2024-03-17 15:56:40
*/
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
    implements PostCommentService {

    @Resource
    private PostCommentMapper postCommentMapper;

    @Override
    public void validComment(PostComment postComment, boolean add) {
        if (postComment == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String content = postComment.getContent();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(content), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    @Override
    public List<PostComment> getByPostId(long postId) {
        LambdaQueryWrapper<PostComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostComment::getPostId, postId);
        return list(queryWrapper);
    }

    @Override
    public boolean deleteCommentById(long id) {
        LambdaUpdateWrapper<PostComment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PostComment::getId, id)
                .or().eq(PostComment::getParentId, id)
                .set(PostComment::getIsDelete, true);
        return update(updateWrapper);
    }

    @Override
    public List<PostCommentVO> getCommentById(long postId) {
        PostCommentVO postCommentVO = new PostCommentVO();
        postCommentVO.setPostId(postId);
        QueryWrapper<PostCommentVO> queryWrapper = new QueryWrapper<>(postCommentVO);
        queryWrapper.eq("postId", postId);
        return postCommentMapper.getPostComment(postId);
    }
}




