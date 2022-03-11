package com.linzhi.gongfu.util;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
@Data
public class DateConverter {

    public static Long getDateTime(LocalDateTime date){

        return date.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }
}
