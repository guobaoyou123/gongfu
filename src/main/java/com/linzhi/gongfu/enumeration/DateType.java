package com.linzhi.gongfu.enumeration;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

public enum DateType {
    YYYYMMDD("yyyy-MM-dd"),
    YYYYMMDDHHMMSS("yyyy-MM-dd HH:mm:ss");

    private final String type;

    private DateType(String type) {
        this.type = type;
    }

    public DateTimeFormatter getType() {
        return DateTimeFormatter.ofPattern(type);
    }


}
