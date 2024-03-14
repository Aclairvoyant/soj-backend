package com.sjdddd.sojbackend.judge.codesandbox.Impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.sjdddd.sojbackend.common.ErrorCode;
import com.sjdddd.sojbackend.exception.BusinessException;
import com.sjdddd.sojbackend.judge.codesandbox.CodeSandBox;
import com.sjdddd.sojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojbackend.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: 沈佳栋
 * @Description: 远程代码沙箱
 * @DateTime: 2024/3/6 22:02
 **/
public class RemoteSandBoxImpl implements CodeSandBox {
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "soj";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("执行远程代码沙箱");
        String url = "http://localhost:8090/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR,"远程代码沙箱返回结果为空" + responseStr);
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
