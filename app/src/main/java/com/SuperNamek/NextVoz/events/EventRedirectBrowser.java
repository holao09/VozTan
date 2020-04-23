package com.SuperNamek.NextVoz.events;

/**
 * @Author: SuperNamek
 */

public class EventRedirectBrowser {
    private final String url;

    public EventRedirectBrowser(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
}
