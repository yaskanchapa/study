package com.kse.wmsv2.common.service;

import com.kse.wmsv2.common.util.SharedDataBase;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;

public class AbstractService {

    @Autowired
    private SharedDataBase sharedDataBase;

    public Connection getConnection(String connectionId) throws  Exception{
        Connection conn = sharedDataBase.getConnection(connectionId);
        return conn;
    }


}
