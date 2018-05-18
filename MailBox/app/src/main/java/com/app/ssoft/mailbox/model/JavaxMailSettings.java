package com.app.ssoft.mailbox.model;

import javax.mail.Store;

public class JavaxMailSettings {
    private static JavaxMailSettings javaxDetails;

    public JavaxMailSettings() {
    }

    public static JavaxMailSettings getInstance() {
        if (javaxDetails == null) {
            javaxDetails = new JavaxMailSettings();
        }
        return javaxDetails;
    }

    private Store store;

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
