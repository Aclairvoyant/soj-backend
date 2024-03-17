package com.sjdddd.sojbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sjdddd.sojbackend.mapper.PostCommentMapper;
import com.sjdddd.sojbackend.model.entity.PostComment;
import com.sjdddd.sojbackend.service.PostCommentService;
import org.springframework.stereotype.Service;

/**
* @author K
* @description 针对表【post_comment(帖子评论)】的数据库操作Service实现
* @createDate 2024-03-17 15:56:40
*/
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
    implements PostCommentService {

}




