package com.sjdddd.sojbackend.model.dto.problemset;

import com.sjdddd.sojbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

/**
 * 查询题单请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProblemSetQueryRequest extends PageRequest implements Serializable {
    /** 题单id */
    private Long id;
    /** 题单名称 */
    private String name;
    /** 是否公开 0-私有 1-公开 */
    private Integer isPublic;
    /** 是否官方题单 0-用户题单 1-官方题单 */
    private Integer isOfficial;
    /** 创建者id */
    private Long userId;
    private static final long serialVersionUID = 1L;
}
