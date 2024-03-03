package com.sjdddd.sojbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author YukeSeko
 * @Since 2023/9/19 17:12
 */
@Data
public class UserLoginByMailRequest implements Serializable {

    private String email;

    private String emailCode;
}
