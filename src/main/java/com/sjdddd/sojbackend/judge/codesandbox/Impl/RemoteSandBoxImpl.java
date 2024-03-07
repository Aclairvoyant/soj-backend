package com.sjdddd.sojbackend.judge.codesandbox.Impl;

import com.sjdddd.sojbackend.judge.codesandbox.CodeSandBox;
import com.sjdddd.sojbackend.judge.codesandbox.model.ExecuteCodeRequest;
import com.sjdddd.sojbackend.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * @Author: 沈佳栋
 * @Description: 远程代码沙箱
 * @DateTime: 2024/3/6 22:02
 **/
public class RemoteSandBoxImpl implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("执行远程代码沙箱");
        return null;
    }
}
