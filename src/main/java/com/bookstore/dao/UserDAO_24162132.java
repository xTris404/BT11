package com.bookstore.dao;

import com.bookstore.model.User_24162132;
import com.bookstore.util.DBUtil_24162132;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO_24162132 {
    public User_24162132 findByEmail(String email) {
        try (Connection c = DBUtil_24162132.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, email, fullname, phone, passwd, signup_date, last_login, is_admin FROM users WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User_24162132 u = new User_24162132();
                u.setId(rs.getInt("id"));
                u.setEmail(rs.getString("email"));
                u.setFullname(rs.getString("fullname"));
                u.setPhone((Integer) rs.getObject("phone"));
                u.setPasswd(rs.getString("passwd"));
                u.setSignupDate(rs.getTimestamp("signup_date"));
                u.setLastLogin(rs.getTimestamp("last_login"));
                u.setAdmin(rs.getBoolean("is_admin"));
                return u;
            }
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }

    public void insert(User_24162132 u) {
        try (Connection c = DBUtil_24162132.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO users(email, fullname, phone, passwd, signup_date, is_admin) VALUES (?,?,?,?,NOW(),0)")) {
            ps.setString(1, u.getEmail());
            ps.setString(2, u.getFullname());
            DBUtil_24162132.setInt(ps, 3, u.getPhone());
            ps.setString(4, u.getPasswd());
            ps.executeUpdate();
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }

    public void updateLastLogin(int id) {
        try (Connection c = DBUtil_24162132.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE users SET last_login = NOW() WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new DataAccessException_24162132(e); }
    }
}
