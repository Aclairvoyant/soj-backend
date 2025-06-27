package com.sjdddd.sojbackend.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题单VO
 */
@Data
public class ProblemSetVO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Integer isPublic;
    private Integer isOfficial;
    private Long userId;
    private Date createTime;
    private Date updateTime;
    /** 题目VO列表 */
    private List<QuestionVO> questionVOList;
    /** 创建者信息 */
    private UserVO userVO;
    private static final long serialVersionUID = 1L;
}
