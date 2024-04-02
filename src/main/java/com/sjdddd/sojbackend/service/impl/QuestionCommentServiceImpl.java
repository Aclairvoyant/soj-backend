package com.sjdddd.sojbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.exception.BusinessException;
import com.sjdddd.sojbackend.exception.ThrowUtils;
import com.sjdddd.sojbackend.mapper.QuestionCommentMapper;
import com.sjdddd.sojbackend.model.entity.QuestionComment;
import com.sjdddd.sojbackend.service.QuestionCommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @Author: 沈佳栋
 * @Description: TODO
 * @DateTime: 2024/4/2 16:11
 **/
@Service
public class QuestionCommentServiceImpl extends ServiceImpl<QuestionCommentMapper, QuestionComment> implements QuestionCommentService {

    @Override
    public void validComment(QuestionComment questionComment, boolean add) {
        if (questionComment == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String content = questionComment.getContent();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(content), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }
}
