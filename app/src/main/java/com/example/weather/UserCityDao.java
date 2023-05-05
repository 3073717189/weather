package com.example.weather;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserCityDao extends DbOpenHelper{

    //按用户名查询城市数据信息 R
    public List<Cityinfo> getCityByUserName(String userName){
        List<Cityinfo> cityList = new ArrayList<>();
        try {
            getConnection();//取得连接信息
            String sql="select * from usercity where user_name=?";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,userName);
            resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                Cityinfo item=new Cityinfo();
                item.setUser_name(resultSet.getString("user_name"));
                item.setCity_id(resultSet.getString("city_id"));
                item.setCity_name(resultSet.getString("city_name"));
                cityList.add(item);
            }
        }catch (SQLException e){
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
            cityList = null;
        } catch (Exception ex){
            System.err.println(ex.getMessage());
            cityList = null;}
        finally {
            closeAll();
        }
        return cityList;
    }

    //添加城市信息，item为要添加的用户，iRow影响的行数 C
    public int addCity(Cityinfo item){
        int iRow=0;
        try {
            getConnection();//取得连接信息
            String sql="insert into usercity(user_name,city_id,city_name) values(?,?,?)";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,item.getUser_name());
            preparedStatement.setString(2,item.getCity_id());
            preparedStatement.setString(3,item.getCity_name());
            iRow=preparedStatement.executeUpdate();
        }catch (Exception e){e.printStackTrace();}
        finally {
            closeAll();
        }
        return iRow;
    }

    //根据user_name删除城市信息
    public int delCity(String userName){
        int iRow=0;
        try {
            getConnection();//取得连接信息
            String sql="delete from usercity where user_name=?";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,userName);
            iRow=preparedStatement.executeUpdate();
        }catch (Exception e){e.printStackTrace();}
        finally {
            closeAll();
        }
        return iRow;
    }
}
