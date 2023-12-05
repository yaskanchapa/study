package com.kse.wmsv2.oc_cs_003.config;

import com.kse.wmsv2.oc_cs_003.common.JwtAuthenticationFilter;
import com.kse.wmsv2.oc_cs_003.common.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * SpringSecurityConfigクラス
 * @document
 * セキュリティ処理を行うクラス
 * 本プロジェクトではJWTを利用する
 * トークン認証や接続可能な権限設定等を行う
 */
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final JwtTokenProvider jwtTokenProvider;

    // authenticationManagerのBean登録
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS","PATCH", "DELETE"));


        http.csrf().disable();
        http.httpBasic().disable()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
//                .antMatchers("/api/**").authenticated()
//                .antMatchers("/api/**").hasAuthority("USERMANAGEMENTAUTHORITYCD01")
                .and().csrf().disable().cors().configurationSource(request -> corsConfiguration);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // トークンの認証を行うフィルター(カスタマイズしたフィルタ―であるJwtAuthenticationFilterを先行でセットする必要あり)
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * PasswordをBcrypt
     * @document
     *  8バイト以上のランダムに生成されたソルトと組み合わせた
     *  SHA-1 以上のハッシュを適用してエンコーディングを行うモジュール
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }





}
