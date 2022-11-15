package com.linzhi.gongfu.service;

import com.linzhi.gongfu.converter.TaxModeConverter;
import com.linzhi.gongfu.dto.TNotification;
import com.linzhi.gongfu.dto.TNotificationInquiry;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.NotificationType;
import com.linzhi.gongfu.enumeration.OfferType;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.mapper.NotificationInquiryMapper;
import com.linzhi.gongfu.mapper.NotificationMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.vo.VOfferRequest;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final NotificationInquiryRecordRepository notificationInquiryRecordRepository;
    /**
     * 查询消息列表
     *
     * @param companyCode  本单位编码
     * @param readed       是否已读
     * @param operatorCode 操作员编码
     * @return 返回消息列表
     */
    public List<TNotification> listNotification(String companyCode, Whether readed, String operatorCode, NotificationType type) {
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
        return notification;

    }

    /**
     * 获取消息详情
     * @param code 消息编码
     * @param companyCode 公司编码
     * @param operator 操作员编码
     * @return 消息详情
     * @throws IOException 异常
     */
    @Transactional
    public TNotification getNotification(String code,String companyCode,String operator,String type) throws Exception {
        try {
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
        }catch (Exception e){
            throw new Exception("数据异常");
        }

    }

    /**
     * 查询该操作员未读的消息有多少条
     * @param companyCode 公司编码
     * @param operatorCode 操作员编码
     * @return 消息条数
     */
    public int getMessageCount(String companyCode,String operatorCode){
        return  notificationOperatorRepository.countNotificationOperatorByPushCompAndAndPushOperatorAndReaded(companyCode,operatorCode,Whether.NO);
    }

    /**
     * 查询该条消息是否正在报价或者已经完成报价
     * @param messCode 消息编码
     * @param operator 操作员编码
     * @return 返回查询消息
     */
    @Transactional
    public Map<String,Object> isOffered(String messCode,String operator) throws Exception {
       Map<String,Object> map = new HashMap<>();

        Notification notification = notificationRepository.findById(messCode).orElseThrow(()->new IOException("没有从数据库中查到"));

        NotificationInquiry inquiry = notificationInquiryRepository.findById(messCode).orElseThrow(()->new IOException("没有从数据库中查到"));
        if(!inquiry.getState().equals(OfferType.WAIT_OFFER)&&!inquiry.getOfferBy().equals(operator)){
              map.put("code",204);
              map.put("message","已完成报价，不可再次报价");
              return map;
        }

        if(inquiry.getState().equals(OfferType.WAIT_OFFER)&&notification.getOperatedBy()!=null && !notification.getOperatedBy().equals(operator)){
            map.put("code",204);
            map.put("message","正在报价中，不可再次报价");
            return map;
        }

        if(inquiry.getState().equals(OfferType.WAIT_OFFER)&&notification.getOperatedBy()==null){
            notification.setOperatedBy(operator);
            notificationRepository.save(notification);
        }
        map.put("code",200);
        map.put("message","可以进行报价");
        return map;
    }


    /**
     * 更新询价记录中的报价信息
     * @param offer 报价信息
     * @param messCode 消息编码
     * @throws Exception 异常
     */
    @Transactional
    public void updateOffer(VOfferRequest offer,String messCode) throws Exception {
        try {
             NotificationInquiry inquiry = notificationInquiryRepository.findById(messCode).orElseThrow(()->new IOException(""));
            if(Optional.ofNullable(offer.getTaxModel()).orElse("").length()!=0){
                inquiry.setOfferMode(new TaxModeConverter().convertToEntityAttribute(offer.getTaxModel().toCharArray()[0]));
            }
            if(offer.getProducts()!=null && offer.getProducts().size()>0){
                Map<Integer,VOfferRequest.VProduct> priceMap = offer.getProducts().stream().collect(Collectors.toMap(VOfferRequest.VProduct::getCode,vProduct -> vProduct));
                for(int i = 0;i<inquiry.getRecords().size();i++){
                    var price = priceMap.get(inquiry.getRecords().get(i).getNotificationInquiryRecordId().getCode());
                    if(price!=null){
                        inquiry.getRecords().get(i).setPrice(price.getPrice());
                        inquiry.getRecords().get(i).setIsOffer(price.isOffered()?Whether.YES:Whether.NO);
                    }
                }
               /* inquiry.setRecords(inquiry.getRecords().stream().peek(r->{
                    var price = priceMap.get(r.getNotificationInquiryRecordId().getCode());
                    if(price!=null){
                        r.setPrice(price.getPrice());
                        r.setIsOffer(price.isOffered()?Whether.YES:Whether.NO);
                    }
                }).collect(Collectors.toList()));*/
            }
            notificationInquiryRepository.save(inquiry);
        }catch (Exception e){
            e.printStackTrace();
            throw  new Exception("数据保存失败");
        }

    }

    /**
     * 导出询价记录列表
     *
     * @param code 消息主键
     * @return 产品列表
     */
    public List<LinkedHashMap<String, Object>> exportProduct(String code) {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        try {
            NotificationInquiry inquiry = notificationInquiryRepository.findById(code).orElseThrow(()->new IOException(""));

            inquiry.getRecords().forEach(record -> {
                LinkedHashMap<String, Object> m = new LinkedHashMap<>();
                m.put("产品代码", record.getProductCode());

                m.put("数量", record.getAmount());
                list.add(m);
            });
            if (list.size() == 0) {
                LinkedHashMap<String, Object> m = new LinkedHashMap<>();
                m.put("产品代码", "");
                m.put("数量", "");
                list.add(m);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 查询询价记录详情
     * @param messCode 消息编码
     * @return 询价记录详情
     * @throws IOException 异常
     */
    public Optional<TNotificationInquiry>  getOfferDetail(String messCode) throws IOException {
        var inquiry = notificationInquiryRepository.findById(messCode)
            .map(notificationInquiryMapper::toNotificationInquiry)
            .orElseThrow(()->new IOException("未查询到数据"));

        var records = notificationInquiryRecordRepository.findList(messCode).stream()
            .map(notificationInquiryMapper::toInquiryProduct)
            .collect(Collectors.toList());
        inquiry.setProducts(records);

        return Optional.of(inquiry);
    }

    /**
     * 取消报价
     * @param messCode 消息编码
     * @throws Exception 异常
     */
    @Transactional
    public void  cancelOffer(String messCode) throws Exception {
        try{
            var inquiry = notificationInquiryRepository.findById(messCode)
                .orElseThrow(()->new IOException("未查询到数据"));
            var message = notificationRepository.findById(messCode)
                .orElseThrow(()->new IOException("未查询到数据"));
            message.setOperatedBy(null);
            inquiry.setRecords(inquiry.getRecords().stream().map(r->{
                r.setPrice(null);
                r.setIsOffer(Whether.YES);
                return r;
            }).collect(Collectors.toList()));
            notificationRepository.save(message);
            notificationInquiryRepository.save(inquiry);
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("数据保存失败！");
        }

    }

    /**
     * 应答
     * @param messCode 消息编码
     * @param companyCode 单位编码
     * @param operatorCode 操作员编码
     * @param companyName 公司名称
     * @throws Exception 异常
     */
    @Transactional
    public void  offerResponse(String messCode,String companyCode,String operatorCode,String companyName) throws Exception {
        var inquiry = notificationInquiryRepository.findById(messCode)
            .orElseThrow(()->new IOException("未查询到数据"));
        if(!inquiry.getState().equals(OfferType.WAIT_OFFER))
            throw new Exception("该报价已经应答！");
        var notification = notificationRepository.findById(messCode)
            .orElseThrow(()->new IOException("未查询到数据"));
        try{

            var message = createdNotification(
                companyCode,
                companyName+"已对编号为"+notification.getId()+"的询价单作出报价，请及时查看",
                operatorCode,
                NotificationType.INQUIRY_RESPONSE,
                notification.getId(),
                notification.getCreatedCompBy(),
                List.of(notification.getCreatedBy())
            );
            inquiry.setOfferedMessCode(message.getCode());
            inquiry.setState(OfferType.FINISH_OFFER);
            inquiry.setOfferBy(operatorCode);
            inquiry.setOfferCompBy(companyCode);
            inquiry.setOfferedAt(LocalDateTime.now());
            notificationInquiryRepository.updateState(OfferType.ABANDONED_OFFER,notification.getId());
            notificationRepository.save(message);
            notificationInquiryRepository.save(inquiry);
        }catch (Exception e){
            e.printStackTrace();
            throw  new Exception("保存数据");
        }
    }


}
