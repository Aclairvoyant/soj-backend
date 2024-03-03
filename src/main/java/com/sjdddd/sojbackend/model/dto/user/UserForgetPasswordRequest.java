package com.sjdddd.sojbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserForgetPasswordRequest implements Serializable {

    private String email;

    private String emailCode;

    private String userPassword;

    private String checkPassword;
}
