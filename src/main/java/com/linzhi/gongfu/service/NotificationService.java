package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TNotification;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.NotificationType;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.mapper.NotificationInquiryMapper;
import com.linzhi.gongfu.mapper.NotificationMapper;
import com.linzhi.gongfu.repository.NotificationInquiryRepository;
import com.linzhi.gongfu.repository.NotificationOperatorRepository;
import com.linzhi.gongfu.repository.NotificationRepository;
import com.linzhi.gongfu.repository.OperatorBaseRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final NotificationInquiryRepository notificationInquiryRepository;
    private final NotificationInquiryMapper notificationInquiryMapper;
    private final OperatorBaseRepository operatorBaseRepository;

    /**
     * 查询消息列表
     *
     * @param companyCode  本单位编码
     * @param readed       是否已读
     * @param operatorCode 操作员编码
     * @param scenes       场景列表
     * @return 返回消息列表
     */
    public List<TNotification> listNotification(String companyCode, Whether readed, String operatorCode, List<String> scenes,NotificationType type) {
        QNotification qNotification = QNotification.notification;
        QNotificationOperator qNotificationOperator = QNotificationOperator.notificationOperator;
        JPAQuery<Notification> query = queryFactory.select(qNotification).from(qNotification)
            .leftJoin(qNotificationOperator).on(qNotificationOperator.notificationOperatorId.messageCode.eq(qNotification.code));
        query.where(qNotificationOperator.readed.eq(readed));
        query.where(qNotification.type.eq(type));
        query.where(qNotification.pushComp.eq(companyCode));
        query.where(qNotificationOperator.pushOperator.eq(operatorCode));
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
     * 保存消息信息(申请采购类)
     *  @param companyCode      创建者单位编码
     * @param message          信息
     * @param operatorCode     创建者
     * @param type             类型
     * @param id               对应的消息主键
     * @param pushComp         推送单位
     */
    @Transactional
    public void saveNotification(String companyCode, String message, String operatorCode, NotificationType type, String id, String pushComp) throws Exception {
        try{
            List<String> operators =    operatorBaseRepository.findByScene("格友综合管理",pushComp).stream()
                    .map(p->p.getIdentity().getOperatorCode())
                    .collect(Collectors.toList());
          var notification=  createdNotification(companyCode,message,operatorCode,type,id,pushComp,operators);
          notificationRepository.save(notification);
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
     * @param pushOperatorCode 推送操作员编码列表
     */
    public Notification createdNotification(String companyCode, String message, String operatorCode, NotificationType type, String id, String pushComp, List<String> pushOperatorCode) {
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
                for (String s : pushOperatorCode) {
                    var operator = NotificationOperator.builder()
                        .notificationOperatorId(NotificationOperatorId.builder()
                            .messageCode(notification.getCode())
                            .code(i)
                            .build())
                        .pushComp(pushComp)
                        .pushOperator(s)
                        .readed(Whether.NO)
                        .build();
                    notifications.add(operator);
                }

            notification.setOperatorList(notifications);
            return  notification;

    }

    /**
     * 获取消息详情
     * @param code 消息编码
     * @param companyCode 公司编码
     * @param operator 操作员编码
     * @return 消息详情
     * @throws IOException 异常
     */
    public TNotification getNotification(String code,String companyCode,String operator,String type) throws IOException {
        var notification = notificationRepository.findById(code)
            .map(notificationMapper::toTNotificationDo).orElseThrow(()->new IOException("未查询到数据"));
        NotificationInquiry inquiry = null;
        if(type.equals(NotificationType.INQUIRY_CALL.getType()+"")){
             inquiry = notificationInquiryRepository.findById(code).orElse(null);
        }else if(type.equals(NotificationType.INQUIRY_RESPONSE.getType()+"")){
             inquiry=notificationInquiryRepository.findByOfferedMessCode(code).orElse(null);
        }

        if(inquiry!=null){
            notification.setTaxModel(inquiry.getOfferMode().getTaxMode()+"");
            var products = inquiry.getRecords().stream()
                .map(notificationInquiryMapper::toInquiryProduct)
                .toList();
            notification.setProducts(products);
        }
        var readed = notificationOperatorRepository.findByNotificationOperatorId_MessageCodeAndPushCompAndPushOperator(code,companyCode,operator).orElseThrow(()->new IOException("未查询到数据"));
        if(readed.getReaded().equals(Whether.NO)) {
            readed.setReaded(Whether.YES);
            readed.setReadedAt(LocalDateTime.now());
            notificationOperatorRepository.save(readed);
        }
        return notification;
    }
}
