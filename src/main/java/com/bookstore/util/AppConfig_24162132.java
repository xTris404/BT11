package com.bookstore.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class AppConfig_24162132 {
    private static final Properties P = new Properties();
    static {
        try (InputStream in = AppConfig_24162132.class.getClassLoader().getResourceAsStream("app.properties")) {
            P.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    private AppConfig_24162132() {}
    public static String get(String key) { return P.getProperty(key); }
    public static String get(String key, String def) { return P.getProperty(key, def); }
    public static int getInt(String key, int def) {
        try { return Integer.parseInt(P.getProperty(key).trim()); } catch (Exception e) { return def; }
    }
}
