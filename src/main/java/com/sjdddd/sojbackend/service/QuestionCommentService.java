package com.sjdddd.sojbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjdddd.sojbackend.model.entity.QuestionComment;

/**
 * @Author: 沈佳栋
 * @Description: TODO
 * @DateTime: 2024/4/2 16:06
 **/
public interface QuestionCommentService extends IService<QuestionComment> {

    void validComment(QuestionComment questionComment, boolean b);
}
