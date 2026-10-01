package com.bookstore.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/** Tạo kết nối JDBC. Mỗi lần gọi = 1 connection mới; caller phải đóng bằng try-with-resources. */
public final class DBUtil_24162132 {
    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { throw new ExceptionInInitializerError(e); }
    }
    private DBUtil_24162132() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(AppConfig_24162132.get("db.url"), AppConfig_24162132.get("db.user"), AppConfig_24162132.get("db.password"));
    }

    public static void setInt(PreparedStatement ps, int idx, Integer v) throws SQLException {
        if (v == null) ps.setNull(idx, Types.INTEGER); else ps.setInt(idx, v);
    }
}
