package com.bookstore.util;

import com.bookstore.model.User_24162132;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class WebUtil_24162132 {
    public static final String USER = "currentUser";
    private WebUtil_24162132() {}

    public static int toInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }
    public static User_24162132 currentUser(HttpServletRequest r) {
        HttpSession s = r.getSession(false);
        return s == null ? null : (User_24162132) s.getAttribute(USER);
    }
    /** Flash message: lưu ở session, hiển thị đúng 1 lần ở request kế tiếp (mẫu Post-Redirect-Get). */
    public static void flash(HttpServletRequest r, String msg) { put(r, msg, "success"); }
    public static void flashError(HttpServletRequest r, String msg) { put(r, msg, "error"); }
    private static void put(HttpServletRequest r, String msg, String type) {
        HttpSession s = r.getSession();
        s.setAttribute("flash", msg);
        s.setAttribute("flashType", type);
    }
    public static void moveFlash(HttpServletRequest r) {
        HttpSession s = r.getSession(false);
        if (s == null || s.getAttribute("flash") == null) return;
        r.setAttribute("flash", s.getAttribute("flash"));
        r.setAttribute("flashType", s.getAttribute("flashType"));
        s.removeAttribute("flash");
        s.removeAttribute("flashType");
    }
}
