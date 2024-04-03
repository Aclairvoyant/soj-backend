package com.sjdddd.sojbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjdddd.sojbackend.model.entity.PostComment;
import com.sjdddd.sojbackend.model.vo.PostCommentVO;

import java.util.List;


/**
* @author K
* @description 针对表【post_comment(帖子评论)】的数据库操作Service
* @createDate 2024-03-17 15:56:40
*/
public interface PostCommentService extends IService<PostComment> {

    void validComment(PostComment postComment, boolean b);

    List<PostComment> getByPostId(long postId);

    boolean deleteCommentById(long id);

    List<PostCommentVO> getCommentById(long postId);
}
