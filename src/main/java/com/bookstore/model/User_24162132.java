package com.bookstore.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class User_24162132 implements Serializable {
    private int id;
    private String email, fullname, passwd;
    private Integer phone;
    private Timestamp signupDate, lastLogin;
    private boolean admin;

    public int getId() { return id; }
    public void setId(int v) { id = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public String getFullname() { return fullname; }
    public void setFullname(String v) { fullname = v; }
    public Integer getPhone() { return phone; }
    public void setPhone(Integer v) { phone = v; }
    public String getPasswd() { return passwd; }
    public void setPasswd(String v) { passwd = v; }
    public Timestamp getSignupDate() { return signupDate; }
    public void setSignupDate(Timestamp v) { signupDate = v; }
    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp v) { lastLogin = v; }
    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean v) { admin = v; }
}
