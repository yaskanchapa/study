package com.kse.wmsv2.oc_cs_003.common;

import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oc_cs_003.dto.AuthPermissionDto;
import com.kse.wmsv2.oc_cs_003.dto.UserInfoDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {
    /** トークンのsecretKey */
    @Value("${jwt.secret}")
    private String secretKey;

    /** トークン有効時間: 300分 */
    private final long tokenValidTime = 300 * 60 * 1000L;

    /**
     * init処理:secretKeyのBase64エンコーディング
     */
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /** Redis通信に利用する共通部品 */
    @Autowired
    private RedisUtil redisUtil;

    /**
     * トークン生成メソッド
     * @param userPk userCdの値
     * @param userInfo
     * @return トークン
     */
    public String createToken(String userPk, UserInfoDto userInfo) {

        //　トークンのpayload部分作成
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("userCd", userInfo.getUserCd());
        claims.put("userName", userInfo.getUserName());
        claims.put("userNameSyllabary", userInfo.getUserNameSyllabary());
        claims.put("userNameEng", userInfo.getUserNameEng());
        claims.put("userManagementAuthorityCd", userInfo.getUserManagementAuthorityCd());
        claims.put("userAuthorityCd", userInfo.getUserAuthorityCd());
        claims.put("userCompanyCd", userInfo.getUserCompanyCd());
        claims.put("departmentCd", userInfo.getDepartmentCd());
        claims.put("regUserCd", userInfo.getRegUserCd());
        claims.put("regDate", userInfo.getRegDate());
        claims.put("updateUserCd", userInfo.getUpdateUserCd());
        claims.put("updateDate", userInfo.getUpdateDate());
        claims.put("userAuthorityGroupCd", userInfo.getUserAuthorityGroupCd());
        claims.put("printUserCompanyNm", userInfo.getPrintUserCompanyNm());
        claims.put("printUserCompanyCd", userInfo.getPrintUserCompanyCd());
        claims.put("printUserName", userInfo.getPrintUserName());

        Date now = new Date();

        // builderを利用してトークン生成
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    /**
     * トークンパーシング処理によるUserCd取得メソッド
     * @param token
     * @return トークンのUserCd
     */
    // トークンからuser情報取得
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * ヘッダーのトークン情報を取得するメソッド
     * @param request
     * @return ヘッドのトークン情報
     */
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    /**
     * トークンの有効性チェックメソッド
     * @param jwtToken
     * @return 正常:true 異常:false
     */
    public boolean validateToken(String jwtToken) {

        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    /**
     * トークンをparsingするメソッド
     * @param token　トークン
     * @param claimName parsingして取得したい値の項目名
     * @return 該当claimの値
     */
    public String parsingToken(String token, String claimName){
        String parsingValue = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get(claimName, String.class);
        return parsingValue;
    }

    /**
     * トークンの認証処理
     * @document
     * ログインした際に保存されたRedisのユーザーの権限情報と
     * トークン内のユーザーの権限情報の一致を確認して
     * 一致した場合はそのユーザーの権限情報(Redis)をreturnする
     * @param accessToken　ヘッダより取得したトークン
     * @return Loginしたユーザーの権限情報
     */
    public AuthPermissionDto getAuthentication(String accessToken) {

        //　ユーザーコード取得
        String userCd = getUserPk(accessToken);

        //　Redisより権限情報取得
        // 管理権限コード
        String managementAuthorityCd = redisUtil.loadRedis(accessToken, "USERMANAGEMENTAUTHORITYCD");
        // 業務権限コード
        String authorityCd = redisUtil.loadRedis(accessToken, "USERAUTHORITYCD");
        // 業務グループコード
        String authorityGroupCd = redisUtil.loadRedis(accessToken, "USERAUTHORITYGROUPCD");

        //　トークンより権限情報取得
        // 管理権限コード
        String userManagementAuthorityCd = parsingToken(accessToken, "userManagementAuthorityCd");
        // 業務権限コード
        String userAuthorityCd = parsingToken(accessToken, "userAuthorityCd");
        // 業務グループコード
        String userAuthorityGroupCd = parsingToken(accessToken, "userAuthorityGroupCd");

        //　Redisとトークンの権限情報一致確認
        boolean managementAuthorityCdCheck = userManagementAuthorityCd.equals(managementAuthorityCd) || "*".equals(managementAuthorityCd);
        boolean authorityCdCheck = userAuthorityCd.equals(authorityCd) || "*".equals(authorityCd);
        boolean authorityGroupCdCheck = userAuthorityGroupCd.equals(authorityGroupCd) || "*".equals(authorityGroupCd);

        // 権限情報一致した場合、userCdと共に各権限情報をreturnする
        AuthPermissionDto authPermissionDto = new AuthPermissionDto();
        if(accessToken != null && !accessToken.isEmpty() && managementAuthorityCdCheck && authorityCdCheck && authorityGroupCdCheck){
            authPermissionDto.setAuthId(userCd);
            authPermissionDto.setUserManagementAuthorityCdInfo(managementAuthorityCd);
            authPermissionDto.setUserAuthorityCdInfo(authorityCd);
            authPermissionDto.setUserAuthorityGroupCd(authorityGroupCd);
            return authPermissionDto;
        }
        return authPermissionDto;
    }
}
