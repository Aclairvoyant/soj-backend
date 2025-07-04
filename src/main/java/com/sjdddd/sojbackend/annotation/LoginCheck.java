package com.sjdddd.sojbackend.annotation;

import java.lang.annotation.*;

/**
 * 登录校验注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginCheck {
    
}