package com.sjdddd.sojbackend.judge.codesandbox;

import com.sjdddd.sojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojbackend.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * @Author: 沈佳栋
 * @Description: 代码沙箱接口定义
 * @DateTime: 2024/3/6 21:51
 **/
public interface CodeSandBox {
    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
