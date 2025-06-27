package com.sjdddd.sojbackend.model.dto.problemset;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 编辑题单请求
 */
@Data
public class ProblemSetEditRequest implements Serializable {
    /** 题单id */
    private Long id;
    /** 题单名称 */
    private String name;
    /** 题单描述 */
    private String description;
    /** 是否公开 0-私有 1-公开 */
    private Integer isPublic;
    /** 是否官方题单 0-用户题单 1-官方题单 */
    private Integer isOfficial;
    /** 题目id列表（可选） */
    private List<Long> questionIdList;
    private static final long serialVersionUID = 1L;
}
