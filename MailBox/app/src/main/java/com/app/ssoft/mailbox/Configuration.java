package com.app.ssoft.mailbox;

import java.util.Properties;

public class Configuration {
    public static Properties getImapProperties(String domain, String port, boolean isSslEnabled, String protocol) {
        Properties props = new Properties();
        switch (domain) {
            case "GMAIL":
                props.setProperty("mail.store.protocol", "imaps");
                props.setProperty("mail.host", "imap-mail.outlook.com");
                props.setProperty("mail.IMAP.port", port);
                props.setProperty("mail.imap.ssl.enable", "" + isSslEnabled);
                props.setProperty("mail.transport.protocol", protocol);
                props.setProperty("mail.imap.ssl.trust", "*");
        }
        return props;
    }


}



