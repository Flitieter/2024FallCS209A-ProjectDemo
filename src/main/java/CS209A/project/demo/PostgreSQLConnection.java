package CS209A.project.demo;

import CS209A.project.demo.controller.JavatopicsImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLConnection {
    static JavatopicsImpl javatopics=new JavatopicsImpl();
    public static void main(String[] args) {
        // 数据库连接 URL，包含数据库类型、主机地址、端口号、数据库名称
        String url = "jdbc:postgresql://localhost:5432/stkovflw";  // 替换为你的数据库连接信息

        // 数据库用户名和密码
        String username = "postgres";  // 替换为你的数据库用户名
        String password = "200516";  // 替换为你的数据库密码

        Connection connection = null;

        try {
            // 获取数据库连接
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("连接 PostgreSQL 数据库成功！");

            javatopics.Query(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("连接 PostgreSQL 数据库失败！");
        } finally {
            try {
                if (connection != null) {
                    connection.close();  // 关闭连接
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

