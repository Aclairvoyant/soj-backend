package com.sjdddd.sojbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sjdddd.sojbackend.model.entity.PostComment;
import com.sjdddd.sojbackend.model.vo.PostCommentVO;

import java.util.List;


/**
* @author shenjiadong
* @description 针对表【post_comment(帖子评论)】的数据库操作Mapper
* @createDate 2024-04-03 10:03:31
* @Entity generator.domain.PostComment
*/
public interface PostCommentMapper extends BaseMapper<PostComment> {

    List<PostCommentVO> getPostComment(long postId);
}




