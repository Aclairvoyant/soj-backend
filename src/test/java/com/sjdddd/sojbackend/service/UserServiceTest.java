package com.sjdddd.sojbackend.service;

import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 用户服务测试
 *
  
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void userRegister() {
        String userAccount = "sjdddd";
        String userPassword = "";
        String checkPassword = "123456";
        String email = "295816492@qq.com";
        String emailCode = "123456";
        try {
            long result = userService.userRegister(userAccount, userPassword, checkPassword, email, emailCode);
            Assertions.assertEquals(-1, result);
            userAccount = "yu";
            result = userService.userRegister(userAccount, userPassword, checkPassword, email, emailCode);
            Assertions.assertEquals(-1, result);
        } catch (Exception e) {

        }
    }
}
