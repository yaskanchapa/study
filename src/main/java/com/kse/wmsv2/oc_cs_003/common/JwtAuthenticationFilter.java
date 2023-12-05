package com.kse.wmsv2.oc_cs_003.common;

import com.kse.wmsv2.oc_cs_003.dto.AuthPermissionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * トークン検証フィルダークラス
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    /** トークン生成 */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * トークンの有効性チェック及びユーザーの設定等を行うメソッド
     * @document
     * SpringSecurityConfigにより、サーバー側の通信の際には
     * 必ずこのフィルターを通る
     * その際にトークンのチェックを行う
     * トークンのチェックが完了したら、
     * SpringSecurityConfigで利用されるユーザーの設定を行う
     * @param request  The request to process
     * @param response The response associated with the request
     * @param chain    Provides access to the next filter in the chain for this
     *                 filter to pass the request and response to for further
     *                 processing
     *
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // ヘッダよりトークン情報取得
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        // トークンの有効性チェック
        if (token != null && jwtTokenProvider.validateToken(token)) {
            //　トークンの認証処理
            AuthPermissionDto authPermissionDto = jwtTokenProvider.getAuthentication(token);
            // SpringSecurityConfigのhasRole(List形式の場合はhasAuthority)に関わる情報 権限名+権限値
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("USERMANAGEMENTAUTHORITYCD"+authPermissionDto.getUserManagementAuthorityCdInfo()));
            authorities.add(new SimpleGrantedAuthority("USERAUTHORITYCD"+authPermissionDto.getUserAuthorityCdInfo()));
            authorities.add(new SimpleGrantedAuthority("USERAUTHORITYGROUPCD"+authPermissionDto.getUserAuthorityGroupCd()));
            //　SpringSecurityConfigで利用されるユーザー情報設定
            if(!authPermissionDto.getAuthId().isEmpty()){
                // authoritiesがSpringSecurityConfigのhasRole(List形式の場合はhasAuthority)に関わる情報
                UserAuthentication authentication = new UserAuthentication(authPermissionDto.getAuthId(), null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));
                // securityContextにAuthentication登録
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
