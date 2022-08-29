package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TNotification;
import com.linzhi.gongfu.entity.Notification;
import com.linzhi.gongfu.entity.QNotification;
import com.linzhi.gongfu.enumeration.NotificationType;
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

import java.time.LocalDate;
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
        JPAQuery<Notification> query = queryFactory.select(qNotification).from(qNotification);
        query.where(qNotification.readed.eq(readed));
        query.where(qNotification.pushComp.eq(companyCode));
        query.where(qNotification.pushOperator.eq(operatorCode).or(qNotification.pushScene.in(scenes)));
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
    public boolean modifyNotification(String companyCode, List<String> codes) {
        try {
            notificationRepository.updateNotificationState(Whether.YES, codes);
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
    public void saveNotification(String companyCode, String message, String operatorCode, NotificationType type, String id, String pushComp, List<String> scene, String[] pushOperatorCode) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd");
        List<Notification> notifications = new ArrayList<>();
        if (scene != null && scene.size() > 0) {
            for (String s :
                scene) {
                LocalDate data = LocalDate.now();
                Notification notification = Notification.builder()
                    .code("XXTZ-" + type.getType() + "-" + companyCode + "-" + operatorCode + "-" + dtf.format(data) + "-" + UUID.randomUUID().toString().substring(0, 8))
                    .createdAt(LocalDateTime.now())
                    .id(id)
                    .createdBy(operatorCode)
                    .createdCompBy(companyCode)
                    .pushComp(pushComp)
                    .type(type)
                    .message(message)
                    .pushScene(s)
                    .pushOperator(null)
                    .readed(Whether.NO)
                    .build();
                notifications.add(notification);
            }
        } else {

            for (String s : pushOperatorCode) {
                LocalDate data = LocalDate.now();
                Notification notification = Notification.builder()
                    .code("XXTZ-" + NotificationType.MODIFY_TRADE.getType() + "-" + companyCode + "-" + operatorCode + "-" + dtf.format(data) + "-" + UUID.randomUUID().toString().substring(0, 8))
                    .createdAt(LocalDateTime.now())
                    .id(id)
                    .createdBy(operatorCode)
                    .createdCompBy(companyCode)
                    .pushComp(pushComp)
                    .type(type)
                    .message(message)
                    .pushScene(null)
                    .pushOperator(s)
                    .readed(Whether.NO)
                    .build();
                notifications.add(notification);

            }
        }
        notificationRepository.saveAll(notifications);
    }

    /**
     * 创建消息通知实体
     *
     * @param companyCode      单位编码
     * @param message          消息内容
     * @param operatorCode     操作员
     * @param type             类型
     * @param id               关联表主键
     * @param pushComp         推送单位
     * @param scene            推送场景
     * @param pushOperatorCode 推送人
     * @return 返回消息实体
     */
    public Notification createdNotification(String companyCode, String message, String operatorCode, NotificationType type, String id, String pushComp, String scene, String pushOperatorCode) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate data = LocalDate.now();
        return Notification.builder()
            .code("XXTZ-" + type.getType() + "-" + companyCode + "-" + operatorCode + "-" + dtf.format(data) + "-" + UUID.randomUUID().toString().substring(0, 8))
            .createdAt(LocalDateTime.now())
            .id(id)
            .createdBy(operatorCode)
            .createdCompBy(companyCode)
            .pushComp(pushComp)
            .type(type)
            .message(message)
            .pushScene(scene)
            .pushOperator(pushOperatorCode)
            .readed(Whether.NO)
            .build();
    }
}
