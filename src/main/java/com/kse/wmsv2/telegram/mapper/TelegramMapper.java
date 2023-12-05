package com.kse.wmsv2.telegram.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.kse.wmsv2.telegram.dto.*;

@Mapper
public interface TelegramMapper {
    TelegramCommonResultDto selectTelegramCommonHeadData(TelegramCommonParamDto param);
    TelegramOutResultDto selectTelegramOutData(TelegramOutParamDto param);
    String selectCharacterItem1(SelectCharacterItem1ParamDto param);
}
