package com.kse.wmsv2.oa_it_001.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@EqualsAndHashCode(callSuper=false)
@Data
public class OAIT001IDADao extends OAIT001CommonDao{
    OAIT001IDAMainDao mainDao;
    List<OAIT001IDASubDao> subDao;
}
