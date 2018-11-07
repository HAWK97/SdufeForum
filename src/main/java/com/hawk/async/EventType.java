package com.hawk.async;

public enum EventType {

    LIKE(1),

    COMMENT(2),

    NEWS(3);

    private int value;

    EventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
