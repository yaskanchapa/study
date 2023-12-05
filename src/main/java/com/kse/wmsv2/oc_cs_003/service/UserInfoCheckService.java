package com.kse.wmsv2.oc_cs_003.service;

import com.kse.wmsv2.oc_cs_003.dto.UserInfoDto;
import com.kse.wmsv2.oc_cs_003.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;


@Service
@Transactional
public class UserInfoCheckService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserInfoMapper userInfoMapper;

    public UserInfoDto userCheck(UserInfoDto userInfoDto){

        //初期値チェック
        if(userInfoDto.getPassword().isEmpty() || userInfoDto.getUserCd().isEmpty()){
            return new UserInfoDto();
        }

        //ユーザーID,パスワード一致確認
            // DBにユーザーコードが一致するユーザー情報取得
        UserInfoDto userInfoDtoDb = userInfoMapper.select(userInfoDto.getUserCd());
        if(userInfoDtoDb == null){
            return null;
        }
        boolean checkLoginInfo = !StringUtils.isEmpty(userInfoDtoDb.getUserCd());

        if(checkLoginInfo){

            // DBのパスワード
            String dbPassWord = userInfoDtoDb.getPassword();
            //　入力パスワード暗号化
            String inputPassword = passwordEncoder.encode(userInfoDto.getPassword());
            //　DBパスワードと暗号化した入力パスワード一致確認
            boolean checkPassWord = passwordEncoder.matches(dbPassWord, inputPassword);

            if(!checkPassWord){
                return null;
            }
            userInfoDtoDb.setPassword(inputPassword);
        } else {
            return null;
        }

        return userInfoDtoDb;
    }
}
