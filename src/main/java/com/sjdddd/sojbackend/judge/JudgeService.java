package com.sjdddd.sojbackend.judge;

import com.sjdddd.sojbackend.model.entity.QuestionSubmit;
import com.sjdddd.sojbackend.model.vo.QuestionSubmitVO;

/**
 * @Author: 沈佳栋
 * @Description: 判题服务接口
 * @DateTime: 2024/3/7 19:52
 **/
public interface JudgeService {

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(Long questionSubmitId);
}
