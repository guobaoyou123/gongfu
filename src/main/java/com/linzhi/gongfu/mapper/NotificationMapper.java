package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TNotification;
import com.linzhi.gongfu.entity.Notification;
import com.linzhi.gongfu.vo.VNotificationsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换前端菜单结构
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    /**
     * 将从数据库中查到的消息通知转换成可供使用的消息通知
     * @param notification 消息通知
     * @return 返回可使用的消息通知信息
     */
    TNotification toTNotificationDo(Notification notification);

    @Mapping(target = "type",expression = "java(String.valueOf(tNotification.getType().getType()))")
    @Mapping(target = "content",source = "message")
    VNotificationsResponse.VNotification toVNotificationDo(TNotification tNotification);

}
