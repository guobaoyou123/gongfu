package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TNotification;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.mapper.NotificationMapper;
import com.linzhi.gongfu.repository.NotificationRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 查询消息列表
     * @param companyCode 本单位编码
     * @param readed  是否已读
     * @param operatorCode 操作员编码
     * @param scenes 场景列表
     * @return 返回消息列表
     */
    @Cacheable(value = "Notification_List;1800", key="#companyCode+'-'+#operatorCode+'-'+#readed.state+''",unless = "#result == null")
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

    /**
     * 修改消息通知状态
     * @param codes 消息通知编码
     * @return 返回 成功或者失败
     */
    @CacheEvict(value = "Notification_List;1800", key="#companyCode+'-'+'*'")
    @Transactional
    public boolean  modifyNotification(String companyCode, List<String> codes){
        try{
            notificationRepository.updateNotificationState(Whether.YES,codes);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
