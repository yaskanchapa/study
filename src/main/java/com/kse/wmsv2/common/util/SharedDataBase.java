package com.kse.wmsv2.common.util;


import ch.qos.logback.core.net.SyslogOutputStream;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class SharedDataBase {

    private  static SharedDataBase sharedDataBase;
    private static Map<String, HikariDataSource> dataSourceMap = new HashMap<String, HikariDataSource>();
    private static Connection conn;

//    private static final String KEY_DB_DRIVER_CLASS = ".DB_DRIVER_CLASS";
//    private static final String KEY_DB_URL = ".DB_URL";
//    private static final String KEY_DB_USERNAME = ".DB_USERNAME";
//    private static final String KEY_DB_PASSWORD = ".DB_PASSWORD";
//    private static final String MARIA_CONNECTION_ID = "maria";

    private static final String MARIA_DB_DRIVER_CLASS = "org.mariadb.jdbc.Driver";
    private static final String MARIA_DB_URL = "jdbc:mariadb://localhost:3306/KSE";
    private static final String MARIA_DB_USERNAME = "root";
    private static final String MARIA_DB_PASSWORD = "root";
    private static final String MARIA_CONNECTION_ID = "Maria";
    private static final int MARIA_MAXIMUM_POOL_SIZE = 1;
    private static final int MARIA_MAX_LIFE_TIME = 1000;



    PreparedStatement pstmt = null;

    public SharedDataBase(){

    }

    static {
        sharedDataBase = new SharedDataBase();
        sharedDataBase.init();
    }

    public static void init(){
        HikariDataSource dataSource = createHikariDataSource(MARIA_CONNECTION_ID);
        if(dataSource != null){
            dataSourceMap.put(MARIA_CONNECTION_ID,dataSource);
        } else {
            System.out.println("error");
        }
    }

    public static void main(String[] args){
        System.out.println("Start");
        init();

        for(int i = 0 ; i <100000; i++){

        }

        System.out.println("end");


    }




    public static HikariDataSource createHikariDataSource(String connectId){

        try {
            HikariConfig config = new HikariConfig();
            config.setPoolName(MARIA_CONNECTION_ID);
            config.setDriverClassName(MARIA_DB_DRIVER_CLASS);
            config.setJdbcUrl(MARIA_DB_URL);
            config.setUsername(MARIA_DB_USERNAME);
            config.setPassword(MARIA_DB_PASSWORD);
            config.setMaximumPoolSize(MARIA_MAXIMUM_POOL_SIZE);
            config.setAutoCommit(false);
            config.setMaxLifetime(MARIA_MAX_LIFE_TIME);

            return new HikariDataSource(config);
        } catch (Exception e){
            return null;
        }

    }


    public Connection getConnection(String connectionId) throws  Exception{
        try{
            HikariDataSource dataSource = getDataSourceByConnectionId(connectionId);
            Connection conn = dataSource.getConnection();
            return conn;

        } catch (SQLException e){
            throw e;
        }
    }

    private HikariDataSource getDataSourceByConnectionId(String connectionId){
        return dataSourceMap.get(connectionId);
    }


}
