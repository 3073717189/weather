package com.example.weather;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

//MySQL数据库的连接辅助类
public class DbOpenHelper {
    private static final String CLS = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://192.168.137.1:3306/weatherdb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "weatherUser";
    private static final String PWD = "123456";

    public static Connection connection;//连接对象
    public static Statement statement;//命令集
    public static PreparedStatement preparedStatement;//预编译命令集
    public static ResultSet resultSet;//查询结果集

    //取得连接
    public static void getConnection() {
        try {
            Class.forName(CLS);
            connection = DriverManager.getConnection(URL, USER, PWD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭数据库
    public static void closeAll() {
        try {
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }
            if (statement != null) {
                statement.close();
                statement = null;
            }
            if (preparedStatement != null) {
                preparedStatement.close();
                preparedStatement = null;
            }
            if (connection != null) {
                connection.close();
                connection = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
