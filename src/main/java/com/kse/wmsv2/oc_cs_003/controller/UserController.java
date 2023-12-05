package com.kse.wmsv2.oc_cs_003.controller;

import com.kse.wmsv2.oc_cs_003.common.JwtTokenProvider;
import com.kse.wmsv2.oc_cs_003.dto.LoginUserInfoDto;
import com.kse.wmsv2.oc_cs_003.dto.UserInfoDto;
import com.kse.wmsv2.oc_cs_003.service.RedisService;
import com.kse.wmsv2.oc_cs_003.service.UserInfoCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Login,Logout関連のコントローラークラス
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserInfoCheckService userInfoCheckService;
    @Autowired
    RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;
    String accessToken = "";
    UserInfoDto userInfoDto = new UserInfoDto();

    /**
     * Login処理を行うメソッド
     * @param userInfo
     * @return Loginしたユーザー情報を渡す　ユーザー情報とトークンが入っている
     */
    @PostMapping("/login")
    public LoginUserInfoDto login(@RequestBody UserInfoDto userInfo) {
        LoginUserInfoDto loginUserInfoDto = null;
        loginUserInfoDto = new LoginUserInfoDto();
        try {
            userInfoDto = userInfoCheckService.userCheck(userInfo);

            if (userInfoDto == null) {
                String errMsg = "ユーザー情報を確認してください。";
                log.error(errMsg);
                loginUserInfoDto.setRst(false);
                loginUserInfoDto.setMsg(errMsg);
            } else {
                String msg = "ログインに成功しました。";
                log.info(msg);
                loginUserInfoDto.setRst(true);
                loginUserInfoDto.setMsg(msg);
                accessToken = jwtTokenProvider.createToken(userInfoDto.getUserCd(), userInfoDto);
                loginUserInfoDto.setUserInfoDto(userInfoDto);
                loginUserInfoDto.setAccessToken(accessToken);
                redisService.saveRedis(loginUserInfoDto);
            }
        } catch (Exception e) {
            String errMsg = "ログインに失敗しました。";
            log.error(errMsg + e.getMessage());
            loginUserInfoDto.setRst(false);
            loginUserInfoDto.setMsg(errMsg);

        } finally {

            return loginUserInfoDto;
        }
    }

    /**
     * Logout処理を行うメソッド
     * @return 正常:true 異常:false
     */
    @GetMapping("/logout")
    public boolean logout(HttpServletRequest request) {
        boolean rst = false;
        try{
            // Redis一括削除処理
            redisService.delete(request.getHeader("Authorization"));
            rst = true;
        }catch (Exception e) {
            String errMsg = "ログアウトに失敗しました。";
            log.error(errMsg + e.getMessage());
            rst = false;
        } finally {
            return rst;
        }
    }
}
