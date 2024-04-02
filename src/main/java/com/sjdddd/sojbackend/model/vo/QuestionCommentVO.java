package com.sjdddd.sojbackend.model.vo;

import com.sjdddd.sojbackend.model.entity.QuestionComment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目评论
 *
 */
@Data
public class QuestionCommentVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 用户名字
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;


    /**
     * 包装类转对象
     *
     * @param questionCommentVO
     * @return
     */
    public static QuestionComment voToObj(QuestionVO questionCommentVO) {
        if (questionCommentVO == null) {
            return null;
        }
        QuestionComment questionComment = new QuestionComment();
        BeanUtils.copyProperties(questionCommentVO, questionComment);

        return questionComment;
    }

    /**
     * 对象转包装类
     *
     * @param questionComment
     * @return
     */
    public static QuestionCommentVO objToVo(QuestionComment questionComment) {
        if (questionComment == null) {
            return null;
        }
        QuestionCommentVO questionCommentVO = new QuestionCommentVO();
        BeanUtils.copyProperties(questionComment, questionCommentVO);
        return questionCommentVO;
    }
}
