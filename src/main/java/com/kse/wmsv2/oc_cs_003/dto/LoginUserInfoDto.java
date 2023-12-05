package com.kse.wmsv2.oc_cs_003.dto;

import lombok.Getter;
import lombok.Setter;

/**
 *ログインしたユーザーの情報
 * 及びトークンを管理するDto
 */
@Getter
@Setter
public class LoginUserInfoDto {

    private UserInfoDto userInfoDto = new UserInfoDto();
    private String accessToken;
    private boolean rst;
    private String msg;

}
