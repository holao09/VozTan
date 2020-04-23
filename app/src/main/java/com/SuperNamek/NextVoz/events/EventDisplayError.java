package com.SuperNamek.NextVoz.events;

/**
 * @Author: SuperNamek
 */

public class EventDisplayError {

    private final String errorDescription;

    public EventDisplayError(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getErrorDescription() {
        return this.errorDescription;
    }
}
