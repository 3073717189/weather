package com.example.weather;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//用户数据库操作类，实现用户的crud操作
public class UserDao extends DbOpenHelper{

    //查询所有用户信息
    public List<Userinfo>getAllUserList(){
        List<Userinfo>list=new ArrayList<>();
        try {
            getConnection();//取得连接信息
            String sql="select * from user";
            preparedStatement=connection.prepareStatement(sql);

            resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                Userinfo item=new Userinfo();
                item.setId(resultSet.getInt("id"));
                item.setUserName(resultSet.getString("name"));
                item.setUserPass(resultSet.getString("password"));
                item.setCreateDate(resultSet.getString("createDate"));
                list.add(item);
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            closeAll();
        }
        return list;
    }

    //按用户名和密码查询用户信息 R
    public Userinfo getUserByUserNameAndUserPass(String userName,String userPass){
        Userinfo item=new Userinfo();
        try {
            getConnection();//取得连接信息
            String sql="select * from user where name=? and password=?";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,userName);
            preparedStatement.setString(2,userPass);
            resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                item=new Userinfo();
                item.setId(resultSet.getInt("id"));
                item.setUserName(userName);
                item.setUserPass(userPass);
                item.setCreateDate(resultSet.getString("createDate"));
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
    //添加用户信息，item为要添加的用户，iRow影响的行数 C
    public int addUser(Userinfo item){
        int iRow=0;
        try {
            getConnection();//取得连接信息
            String sql="insert into user(name,password,createDate) values(?,?,?)";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,item.getUserName());
            preparedStatement.setString(2,item.getUserPass());
            preparedStatement.setString(3,item.getCreateDate());
            iRow=preparedStatement.executeUpdate();
        }catch (Exception e){e.printStackTrace();}
        finally {
            closeAll();
        }
        return iRow;
    }
    //修改用户信息，item为要添加的用户，iRow影响的行数 U
    public int editUser(Userinfo item){
        int iRow=0;
        try {
            getConnection();//取得连接信息
            String sql="update user set name=?,password=? where id=?";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,item.getUserName());
            preparedStatement.setString(2,item.getUserPass());
            preparedStatement.setInt(3,item.getId());
            iRow=preparedStatement.executeUpdate();
        }catch (Exception e){e.printStackTrace();}
        finally {
            closeAll();
        }
        return iRow;
    }
    //根据id删除用户信息
    public int delUser(int id){

        int iRow=0;
        try {
            getConnection();//取得连接信息
            String sql="delete from user where id=?";
            preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            iRow=preparedStatement.executeUpdate();
        }catch (Exception e){e.printStackTrace();}
        finally {
            closeAll();
        }
        return iRow;
    }
}
