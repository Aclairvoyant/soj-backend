package com.sjdddd.sojbackend.judge.codesandbox;

import com.sjdddd.sojbackend.judge.codesandbox.Impl.ExampleSandBoxImpl;
import com.sjdddd.sojbackend.judge.codesandbox.Impl.RemoteSandBoxImpl;
import com.sjdddd.sojbackend.judge.codesandbox.Impl.ThirdPartySandBoxImpl;

/**
 * @Author: 沈佳栋
 * @Description: 代码沙箱工厂模式 用于创建不同的代码沙箱
 * @DateTime: 2024/3/7 19:14
 **/
public class CodeSandBoxFactory {

    /**
     * 获取代码沙箱
     * @param type
     * @return
     */
    public static CodeSandBox newInstance(String type) {
        switch (type) {
            case "remote":
                return new RemoteSandBoxImpl();
            case "thirdParty":
                return new ThirdPartySandBoxImpl();
            case "example":
            default:
                return new ExampleSandBoxImpl();
        }
    }
}
