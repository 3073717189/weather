package com.example.weather;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//用户数据数据库操作类，实现用户数据的crud操作
public class UserDataDao extends DbOpenHelper{

    //查询所有用户信息
    public List<UserDatainfo> getAllUserList(){
        List<UserDatainfo>list=new ArrayList<>();
        try {
            getConnection();//取得连接信息
            String sql="select * from userdata";
            preparedStatement=connection.prepareStatement(sql);
            resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                UserDatainfo item=new UserDatainfo();
                item.setLast_county(resultSet.getString("last_county"));
                item.setUser_name(resultSet.getString("user_name"));
                item.setStart_hour(resultSet.getInt("start_hour"));
                item.setStart_minute(resultSet.getInt("start_minute"));
                item.setEnd_hour(resultSet.getInt("end_hour"));
                item.setEnd_minute(resultSet.getInt("end_minute"));
                item.setSwitch_mode(resultSet.getBoolean("switch_mode"));
                item.setSwitch_time(resultSet.getBoolean("switch_time"));
                item.setAir_state(resultSet.getBoolean("air_state"));
                item.setForecast_state(resultSet.getBoolean("forecast_state"));
                item.setWind_state(resultSet.getBoolean("wind_state"));
                item.setLife_state(resultSet.getBoolean("life_state"));
                item.setOther_state(resultSet.getBoolean("other_state"));
                item.setHourly_state(resultSet.getBoolean("hourly_state"));
                item.setService_state(resultSet.getBoolean("service_state"));
                list.add(item);
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            closeAll();
        }
        return list;
    }

    //按用户名查询用户数据信息 R
    public UserDatainfo getUserByUserName(String userName){
        UserDatainfo item=new UserDatainfo();
        try {
            getConnection();//取得连接信息
            String sql="select * from userdata where user_name=?";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,userName);
            resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                item=new UserDatainfo();
                item.setLast_county(resultSet.getString("last_county"));
                item.setUser_name(resultSet.getString("user_name"));
                item.setStart_hour(resultSet.getInt("start_hour"));
                item.setStart_minute(resultSet.getInt("start_minute"));
                item.setEnd_hour(resultSet.getInt("end_hour"));
                item.setEnd_minute(resultSet.getInt("end_minute"));
                item.setSwitch_mode(resultSet.getBoolean("switch_mode"));
                item.setSwitch_time(resultSet.getBoolean("switch_time"));
                item.setAir_state(resultSet.getBoolean("air_state"));
                item.setForecast_state(resultSet.getBoolean("forecast_state"));
                item.setWind_state(resultSet.getBoolean("wind_state"));
                item.setLife_state(resultSet.getBoolean("life_state"));
                item.setOther_state(resultSet.getBoolean("other_state"));
                item.setHourly_state(resultSet.getBoolean("hourly_state"));
                item.setService_state(resultSet.getBoolean("service_state"));
            }
        }catch (SQLException e){
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
            item = null;
        } catch (Exception ex){
            System.err.println(ex.getMessage());
            item = null;}
        finally {
            closeAll();
        }
        return item;
    }

    //添加用户数据信息，item为要添加的用户，iRow影响的行数 C
    public int addUser(UserDatainfo item){
        int iRow=0;
        try {
            getConnection();//取得连接信息
            String sql="insert into userdata(user_name,switch_mode,switch_time,start_hour,start_minute,end_hour,end_minute" +
                    ",air_state,forecast_state,wind_state,life_state,other_state,hourly_state,service_state,last_county) " +
                    "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,item.getUser_name());
            preparedStatement.setBoolean(2,item.isSwitch_mode());
            preparedStatement.setBoolean(3,item.isSwitch_time());
            preparedStatement.setInt(4,item.getStart_hour());
            preparedStatement.setInt(5,item.getStart_minute());
            preparedStatement.setInt(6,item.getEnd_hour());
            preparedStatement.setInt(7,item.getEnd_minute());
            preparedStatement.setBoolean(8,item.isAir_state());
            preparedStatement.setBoolean(9,item.isForecast_state());
            preparedStatement.setBoolean(10,item.isWind_state());
            preparedStatement.setBoolean(11,item.isLife_state());
            preparedStatement.setBoolean(12,item.isOther_state());
            preparedStatement.setBoolean(13,item.isHourly_state());
            preparedStatement.setBoolean(14,item.isService_state());
            preparedStatement.setString(15,item.getLast_county());
            iRow=preparedStatement.executeUpdate();
        }catch (Exception e){e.printStackTrace();}
        finally {
            closeAll();
        }
        return iRow;
    }
    //修改用户信息，item为要添加的用户，iRow影响的行数 U
    public int editUser(UserDatainfo item){
        int iRow=0;
        try {
            getConnection();//取得连接信息
            String sql="update userdata set switch_mode=?,switch_time=?,start_hour=?,start_minute=?,end_hour=?,end_minute=?, " +
                    "air_state=?,forecast_state=?,wind_state=?,life_state=?,other_state=?,hourly_state=?" +
                    ",service_state=?,last_county=? where user_name=?";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(15,item.getUser_name());
            preparedStatement.setBoolean(1,item.isSwitch_mode());
            preparedStatement.setBoolean(2,item.isSwitch_time());
            preparedStatement.setInt(3,item.getStart_hour());
            preparedStatement.setInt(4,item.getStart_minute());
            preparedStatement.setInt(5,item.getEnd_hour());
            preparedStatement.setInt(6,item.getEnd_minute());
            preparedStatement.setBoolean(7,item.isAir_state());
            preparedStatement.setBoolean(8,item.isForecast_state());
            preparedStatement.setBoolean(9,item.isWind_state());
            preparedStatement.setBoolean(10,item.isLife_state());
            preparedStatement.setBoolean(11,item.isOther_state());
            preparedStatement.setBoolean(12,item.isHourly_state());
            preparedStatement.setBoolean(13,item.isService_state());
            preparedStatement.setString(14,item.getLast_county());
            iRow=preparedStatement.executeUpdate();
        }catch (Exception e){e.printStackTrace();}
        finally {
            closeAll();
        }
        return iRow;
    }
}
