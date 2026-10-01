package com.bookstore.util;

import java.util.Properties;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public final class MailUtil_24162132 {
    private static final Logger LOG = Logger.getLogger(MailUtil_24162132.class.getName());
    private MailUtil_24162132() {}

    public static void send(String to, String subject, String body) throws MessagingException {
        if (!Boolean.parseBoolean(AppConfig_24162132.get("mail.enabled", "false"))) {
            LOG.warning("[DEV - mail.enabled=false] To: " + to + " | " + subject + " | " + body);
            return;
        }
        final String user = AppConfig_24162132.get("mail.user"), pass = AppConfig_24162132.get("mail.password");
        Properties p = new Properties();
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.host", AppConfig_24162132.get("mail.host"));
        p.put("mail.smtp.port", AppConfig_24162132.get("mail.port"));
        p.put("mail.smtp.connectiontimeout", "8000");
        p.put("mail.smtp.timeout", "8000");
        Session s = Session.getInstance(p, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });
        MimeMessage m = new MimeMessage(s);
        m.setFrom(new InternetAddress(user));
        m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        m.setSubject(subject, "UTF-8");
        m.setText(body, "UTF-8");
        Transport.send(m);
    }
}
