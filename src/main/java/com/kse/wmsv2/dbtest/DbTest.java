package com.kse.wmsv2.dbtest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class DbTest {

    private static final String DB_DRIVER_CLASS = "org.mariadb.jdbc.Driver";

    private static final String DB_URL = "jdbc:mariadb://localhost:3306/KSE";

    private static final String DB_USERNAME = "root";

    private static final String DB_PASSWORD = "root";


    private static Connection conn;

    PreparedStatement pstmt = null;



    private static void connectDB() {



        try {

            Class.forName(DB_DRIVER_CLASS);

            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            System.out.println("성공");



        } catch (ClassNotFoundException e) {

            // TODO Auto-generated catch block

            System.out.println("드라이브 로딩 실패");

        } catch (SQLException e) {

            // TODO Auto-generated catch block

            System.out.println("DB 연결 실패");

        }

    }

    public static void main(String[] args) {

        connectDB();
        System.out.println("OK");

    }

}