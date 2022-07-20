package com.linzhi.gongfu.converter;

import com.linzhi.gongfu.enumeration.NotificationType;

import javax.persistence.Converter;

/**
 * 转换枚举型消息通知类型值到数据库的char类型字段
 * @author zhangguanghua
 * @create_at 2022-07-20
 */
@Converter(autoApply = true)
public class NotificationTypeConverter  extends CharacterEnumerationConverter<NotificationType>{
    public NotificationTypeConverter() throws NoSuchMethodException {
        super(NotificationType.class, "getType");
    }
}
