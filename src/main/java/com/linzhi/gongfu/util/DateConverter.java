package com.linzhi.gongfu.util;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


public abstract class DateConverter {


    public static Long getDateTime(LocalDateTime date){

        return date.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }


    public static String dateFormat(LocalDateTime date){
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return   dtf2.format(date);
    }
}
