package com.kse.wmsv2.common.interceptor;

import com.kse.wmsv2.common.Constants.CommonConstants;
import com.kse.wmsv2.common.dto.Auth;
import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oc_cs_003.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Permission;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("Interceptor Start");


        // 呼び出し元がMETHODではない場合
        if(!(handler instanceof HandlerMethod)) {
            return true;
        }

        // MethodのAnotationに設定なしの場合
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
        if (auth == null) {
            return true;
        }

        // 権限チェック
        String accessToken = request.getHeader("authorization");
        String userAuth = redisUtil.loadRedis(accessToken, "USERAUTHORITYCD");
        String necAuth = auth.role().toString();

        // ログインチェック
        if(necAuth.equals("USER")){
            if(userAuth == null){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            } else {
                return true;
            }
        }

        // 通関ユーザチェック
        if(necAuth.equals("CUSTOM_USER")){
            if(userAuth == null){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            } else {
                if(userAuth.equals(CommonConstants.AUTH_USER_CUSTOMS)){
                    return true;
                } else {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return false;
                }
            }
        }

        // 保税ユーザチェック
        if(necAuth.equals("WAREHOUSE_USER")){
            if(userAuth == null){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            } else {
                if(userAuth.equals(CommonConstants.AUTH_USER_WAREHOUSE_1) || userAuth.equals(CommonConstants.AUTH_USER_WAREHOUSE_2)){
                    return true;
                } else {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return false;
                }
            }
        }

        // ADMINチェック
        if(necAuth.equals("ADMIN")){
            if(userAuth == null){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            } else {
                String userGroup = redisUtil.loadRedis(accessToken, "USERMANAGEMENTAUTHORITYCD");
                if(userAuth.equals(CommonConstants.AUTH_MANAGEMENT_ADMIN)){
                    return true;
                } else {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return false;
                }
            }
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
    }



}
