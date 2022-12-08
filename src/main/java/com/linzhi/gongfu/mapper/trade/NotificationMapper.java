package com.linzhi.gongfu.mapper.trade;

import com.linzhi.gongfu.dto.TNotification;
import com.linzhi.gongfu.entity.Notification;
import com.linzhi.gongfu.mapper.trade.InquiryRecordMapper;
import com.linzhi.gongfu.vo.trade.VNotificationResponse;
import com.linzhi.gongfu.vo.trade.VNotificationsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换前端消息结构
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Mapper(componentModel = "spring", uses = {InquiryRecordMapper.class})
public interface NotificationMapper {

    /**
     * 将从数据库中查到的消息通知转换成可供使用的消息通知
     *
     * @param notification 消息通知
     * @return 返回可使用的消息通知信息
     */
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.dateFormat(notification.getCreatedAt()))")
    @Mapping(target = "offered",constant = "false")
    TNotification toTNotificationDo(Notification notification);

    /**
     * 消息列表中的消息转换成页面展示的消息
     * @param tNotification 可使用的消息
     * @return 页面展示的消息
     */
    @Mapping(target = "type", expression = "java(String.valueOf(tNotification.getType().getType()))")
    @Mapping(target = "content", source = "message")
    VNotificationsResponse.VNotification toVNotificationDo(TNotification tNotification);

    /**
     * 将获取的可使用的消息详情转换成前端展示消息详情
     * @param tNotification 可供更使用的消息详情
     * @return 前端展示消息详情
     */
    @Mapping(target = "content", source = "message")
    @Mapping(target = "products",source = "products")
    @Mapping(target = "type",expression = "java(String.valueOf(tNotification.getType().getType()))")
    VNotificationResponse.VNotification toVNotificationDetail(TNotification tNotification);

}
