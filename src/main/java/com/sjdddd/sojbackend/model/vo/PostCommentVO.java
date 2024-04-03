package com.sjdddd.sojbackend.model.vo;

import com.sjdddd.sojbackend.model.entity.PostComment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子评论
 *
 */
@Data
public class PostCommentVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 帖子 id
     */
    private Long postId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论 id
     */
    private Long parentId;

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
     * @param postCommentVO
     * @return
     */
    public static PostComment voToObj(PostCommentVO postCommentVO) {
        if (postCommentVO == null) {
            return null;
        }
        PostComment postComment = new PostComment();
        BeanUtils.copyProperties(postCommentVO, postComment);

        return postComment;
    }

    /**
     * 对象转包装类
     *
     * @param postComment
     * @return
     */
    public static PostCommentVO objToVo(PostComment postComment) {
        if (postComment == null) {
            return null;
        }
        PostCommentVO postCommentVO = new PostCommentVO();
        BeanUtils.copyProperties(postComment, postCommentVO);
        return postCommentVO;
    }
}
