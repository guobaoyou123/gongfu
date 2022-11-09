package com.linzhi.gongfu.service;

import com.linzhi.gongfu.converter.WhetherConverter;
import com.linzhi.gongfu.dto.TNotification;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.NotificationType;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.mapper.NotificationMapper;
import com.linzhi.gongfu.repository.NotificationOperatorRepository;
import com.linzhi.gongfu.repository.NotificationRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private final NotificationOperatorRepository notificationOperatorRepository;
    private final JPAQueryFactory queryFactory;
    private final NotificationMapper notificationMapper;

    /**
     * 查询消息列表
     *
     * @param companyCode  本单位编码
     * @param readed       是否已读
     * @param operatorCode 操作员编码
     * @param scenes       场景列表
     * @return 返回消息列表
     */
    @Cacheable(value = "Notification_List;1800", key = "#companyCode+'-'+#operatorCode+'-'+#readed.state+''", unless = "#result == null")
    public List<TNotification> listNotification(String companyCode, Whether readed, String operatorCode, List<String> scenes) {
        QNotification qNotification = QNotification.notification;
        QNotificationOperator qNotificationOperator = QNotificationOperator.notificationOperator;
        JPAQuery<Notification> query = queryFactory.select(qNotification).from(qNotification)
            .leftJoin(qNotificationOperator).on(qNotificationOperator.notificationOperatorId.messageCode.eq(qNotification.code));
        query.where(qNotificationOperator.readed.eq(readed));
        query.where(qNotification.pushComp.eq(companyCode));
        query.where(qNotificationOperator.pushOperator.eq(operatorCode).or(qNotificationOperator.pushScene.in(scenes)));
        query.orderBy(qNotification.createdAt.desc());
        return query.fetch().stream()
            .map(notificationMapper::toTNotificationDo)
            .toList();
    }

    /**
     * 修改消息通知状态
     *
     * @param codes 消息通知编码
     * @return 返回 成功或者失败
     */
    @CacheEvict(value = "Notification_List;1800", key = "#companyCode+'-'+'*'")
    @Transactional
    public boolean modifyNotification(String companyCode, List<String> codes,String operator) {
        try {
            notificationOperatorRepository.updateNotificationState(Whether.YES, codes,companyCode,operator);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 生成消息信息
     *
     * @param companyCode      创建者单位编码
     * @param message          信息
     * @param operatorCode     创建者
     * @param type             类型
     * @param id               对应的消息主键
     * @param pushComp         推送单位
     * @param scene            查看消息场景列表
     * @param pushOperatorCode 推送操作员编码列表
     */
    @CacheEvict(value = "Notification_List;1800", key = "#pushComp+'-'+'*'")
    @Transactional
    public String saveNotification(String companyCode, String message, String operatorCode, NotificationType type, String id, String pushComp, List<String> scene, String[] pushOperatorCode) throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd");
        try{
            Notification notification = Notification.builder()
                .code("XXTZ-" + type.getType() + "-" + companyCode + "-" + operatorCode + "-" + dtf.format(LocalDateTime.now()) + "-" + UUID.randomUUID().toString().substring(0, 8))
                .createdAt(LocalDateTime.now())
                .id(id)
                .createdBy(operatorCode)
                .createdCompBy(companyCode)
                .pushComp(pushComp)
                .type(type)
                .message(message)
                .build();
            List<NotificationOperator> notifications = new ArrayList<>();
            int i = 1;
            if (scene != null && scene.size() > 0) {
                for (String s :
                    scene) {
                    var scence=NotificationOperator.builder()
                        .notificationOperatorId(NotificationOperatorId.builder()
                            .messageCode(notification.getCode())
                            .code(i)
                            .build())
                        .pushComp(pushComp)
                        .pushScene(s)
                        .pushOperator(null)
                        .readed(Whether.NO)
                        .build();
                    notifications.add(scence);
                    i++;
                }
            } else {
                for (String s : pushOperatorCode) {
                    var operator = NotificationOperator.builder()
                        .notificationOperatorId(NotificationOperatorId.builder()
                            .messageCode(notification.getCode())
                            .code(i)
                            .build())
                        .pushComp(pushComp)
                        .pushScene(null)
                        .pushOperator(s)
                        .readed(Whether.NO)
                        .build();
                    notifications.add(operator);
                }
            }
            notification.setOperatorList(notifications);
            notificationRepository.save(notification);
            return  notification.getCode();
        }catch (Exception e){
            throw  new Exception("保存消息失败");
        }

    }


    /**
     * 生成消息信息
     *
     * @param companyCode      创建者单位编码
     * @param message          信息
     * @param operatorCode     创建者
     * @param type             类型
     * @param id               对应的消息主键
     * @param pushComp         推送单位
     * @param scene            查看消息场景列表
     * @param pushOperatorCode 推送操作员编码列表
     */
    public Notification createdNotification(String companyCode, String message, String operatorCode, NotificationType type, String id, String pushComp, List<String> scene, String[] pushOperatorCode) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd");
            Notification notification = Notification.builder()
                .code("XXTZ-" + type.getType() + "-" + companyCode + "-" + operatorCode + "-" + dtf.format(LocalDateTime.now()) + "-" + UUID.randomUUID().toString().substring(0, 8))
                .createdAt(LocalDateTime.now())
                .id(id)
                .createdBy(operatorCode)
                .createdCompBy(companyCode)
                .pushComp(pushComp)
                .type(type)
                .message(message)
                .build();
            List<NotificationOperator> notifications = new ArrayList<>();
            int i = 1;
            if (scene != null && scene.size() > 0) {
                for (String s :
                    scene) {
                    var scence=NotificationOperator.builder()
                        .notificationOperatorId(NotificationOperatorId.builder()
                            .messageCode(notification.getCode())
                            .code(i)
                            .build())
                        .pushComp(pushComp)
                        .pushScene(s)
                        .pushOperator(null)
                        .readed(Whether.NO)
                        .build();
                    notifications.add(scence);
                    i++;
                }
            } else {
                for (String s : pushOperatorCode) {
                    var operator = NotificationOperator.builder()
                        .notificationOperatorId(NotificationOperatorId.builder()
                            .messageCode(notification.getCode())
                            .code(i)
                            .build())
                        .pushComp(pushComp)
                        .pushScene(null)
                        .pushOperator(s)
                        .readed(Whether.NO)
                        .build();
                    notifications.add(operator);
                }
            }
            notification.setOperatorList(notifications);
            return  notification;

    }

}
