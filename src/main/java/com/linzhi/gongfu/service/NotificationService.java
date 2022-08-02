package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TNotification;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.mapper.NotificationMapper;
import com.linzhi.gongfu.repository.NotificationRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 前端菜单相关业务处理服务
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JPAQueryFactory queryFactory;
    private final NotificationMapper notificationMapper;

    public List<TNotification>  listNotification(String companyCode, Whether readed, String operatorCode, List<String> scenes){
        QNotification qNotification = QNotification.notification;
        JPAQuery<Notification> query = queryFactory.select(qNotification).from(qNotification);
        query.where(qNotification.readed.eq(readed));
        query.where(qNotification.pushComp.eq(companyCode));
        query.where(qNotification.pushOperator.eq(operatorCode).or(qNotification.pushScene.in(scenes)));
        query.orderBy(qNotification.createdAt.desc());
        return  query.fetch().stream()
            .map(notificationMapper::toTNotificationDo)
            .toList();
    }
}
