package com.linzhi.gongfu.service;


import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.*;
import com.linzhi.gongfu.mapper.CompTradeApplyMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.VTradeApplyConsentRequest;
import com.linzhi.gongfu.vo.VTradeApplyPageResponse;
import com.linzhi.gongfu.vo.VTradeApplyRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 公司申请采购信息及处理业务服务
 *
 * @author zhangguanghua
 * @create_at 2022-07-19
 */
@RequiredArgsConstructor
@Service
public class CompTradeApplyService {
    private final CompTradeApplyRepository compTradeApplyRepository;
    private final NotificationRepository notificationRepository;
    private final CompInvitationCodeRepository compInvitationCodeRepository;
    private final SceneMenuRepository sceneMenuRepository;
    private final CompTradeApplyMapper compTradeApplyMapper;
    private final CompTradDetailRepository compTradDetailRepository;
    private final CompTradBrandRepository compTradBrandRepository;
    /**
     * 申请采购
     * @param vTradeApplyRequest 申请信息
     * @param companyCode 单位编码
     * @param operatorCode 操作员编码
     * @param companyName 单位名称
     * @return 返回是或否
     */
    @Transactional
    public boolean tradeApply(VTradeApplyRequest vTradeApplyRequest,String companyCode,String operatorCode,String companyName){
        try{
           CompTradeApply compTradeApply =  compTradeApplyRepository.findByCreatedCompByAndHandledCompByAndStateAndType(
                companyCode,
                vTradeApplyRequest.getApplyCompCode(),
                TradeApply.APPLYING,
                "1"
           ).orElse(null);
           if(compTradeApply!=null)
                 return false;
           //申请记录编码 SQCG-申请单位-被申请单位-时间-随机数
            //生成申请采购记录
           CompTradeApply compTradeApply1 = CompTradeApply.builder()
              .code("SQCG-"+companyCode+"-"+vTradeApplyRequest.getApplyCompCode()+"-"+UUID.randomUUID().toString().substring(0,8))
              .createdCompBy(companyCode)
              .createdBy(operatorCode)
              .createdAt(LocalDateTime.now())
              .createdRemark(vTradeApplyRequest.getRemark())
              .handledCompBy(vTradeApplyRequest.getApplyCompCode())
              .type("1")
              .state(TradeApply.APPLYING)
              .build();
           compTradeApplyRepository.save(compTradeApply1);
            //查找有格友综合管理权限的场景
            List<String> sceneList= sceneMenuRepository.findList("格友管理").stream()
               .map(sceneMenu -> sceneMenu.getSceneMenuId().getSceneCode())
               .toList();
            //存入消息通知表
            List<Notification> notifications = new ArrayList<>();
            sceneList.forEach(s -> {
                //消息通知编码  XXTZ-类型-申请单位-操作员-时间戳-随机数
                Notification notification = createdNotification(companyCode,companyName+"公司申请采购",operatorCode,NotificationType.ENROLLED_APPLY+"",compTradeApply1.getCode(),
                    vTradeApplyRequest.getApplyCompCode(),s,null);
                notifications.add(notification);
            });
            notificationRepository.saveAll(notifications);
            //销毁邀请码
            if(vTradeApplyRequest.getInvitationCode()!=null)
                  compInvitationCodeRepository.deleteByCompInvitationCodeId_InvitationCode(vTradeApplyRequest.getInvitationCode());
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 分页查询待处理列表
     * @param companyCode
     * @param pageable
     * @param name
     * @return
     */
    public Page<VTradeApplyPageResponse.VTradeApply> findTradeApply(String companyCode, Pageable pageable, String name){
         List<VTradeApplyPageResponse.VTradeApply> compTradeApplies=  compTradeApplyRepository.findByHandledCompByAndStateAndTypeOrderByCreatedAtDesc(companyCode,TradeApply.APPLYING,"1")
             .stream().filter(compTradeApply -> compTradeApply.getCreatedCompany().getNameInCN().contains(name))
             .map(compTradeApplyMapper::toTComTradeApply)
             .map(compTradeApplyMapper::toVTradeApply)
             .toList();

        return PageTools.listConvertToPage(compTradeApplies,pageable);
    }

    /**
     * 同意申请采购
     * @param code
     * @param companyCode
     * @param operatorCode
     * @return 返回是或者否
     */
    public boolean consentApply(String code, String companyCode,String companyName, String operatorCode, VTradeApplyConsentRequest vTradeApplyConsentRequest){
        try{

           CompTradeApply compTradeApply=  compTradeApplyRepository.findById(code).orElseThrow(()->new IOException("从数据中未找到该申请"));
           if(!compTradeApply.getState().equals(TradeApply.APPLYING))
                return false;
           compTradeApply.setHandledBy(operatorCode);
           compTradeApply.setHandledAt(LocalDateTime.now());
           compTradeApply.setState(TradeApply.AGREE);
           compTradeApplyRepository.save(compTradeApply);
           //生成交易信息
            CompTradDetail compTrad = CompTradDetail.builder()
                .compTradId(CompTradId.builder()
                    .compSaler(companyCode)
                    .compBuyer(compTradeApply.getCreatedCompBy())
                    .build())
                .taxModel(vTradeApplyConsentRequest.getTaxModel().equals("0")? TaxMode.UNTAXED:TaxMode.INCLUDED)
                .state(Trade.TRANSACTION)
                .salerBelongTo(StringUtils.join(vTradeApplyConsentRequest.getAuthorizedOperator(),","))
                .build();
            compTradDetailRepository.save(compTrad);
            List<CompTradBrand> compTradBrands = new ArrayList<>();
            vTradeApplyConsentRequest.getBrandCodes().forEach(s -> {
                CompTradBrand compTradBrand = CompTradBrand.builder()
                    .compTradBrandId(CompTradBrandId.builder()
                        .compBuyer(compTradeApply.getCreatedCompBy())
                        .compSaler(companyCode)
                        .brandCode(s)
                        .build())
                    .sort(0)
                    .build();
                compTradBrands.add(compTradBrand);
            });
            compTradBrandRepository.saveAll(compTradBrands);
            //存入消息通知表
            //消息通知编码  XXTZ-类型-申请单位-操作员-时间戳-随机数
            Notification notification = createdNotification(
                companyCode,
                companyName+"公司同意了您的申请采购的请求",
                operatorCode,
                NotificationType.ENROLLED_APPLY_HISTORY.getType()+"",
                code,
                compTradeApply.getCreatedCompBy(),
                null,
                compTradeApply.getCreatedBy()
            );
            notificationRepository.save(notification);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建消息通知实体
     * @param companyCode 单位编码
     * @param message 消息内容
     * @param operatorCode 操作员
     * @param type 类型
     * @param id 关联表主键
     * @param pushComp 推送单位
     * @param scene 推送场景
     * @param pushOperatorCode 推送人
     * @return 返回消息实体
     */
    public Notification createdNotification(String companyCode,String message,String operatorCode,String type,String id,String pushComp,String scene,String pushOperatorCode){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate data=LocalDate.now();
        return  Notification.builder()
            .code("XXTZ-0-"+companyCode+"-"+operatorCode+"-"+dtf.format(data)+"-"+UUID.randomUUID().toString().substring(0,8))
            .createdAt(LocalDateTime.now())
            .id(id)
            .createdBy(operatorCode)
            .createdCompBy(companyCode)
            .pushComp(pushComp)
            .type(NotificationType.ENROLLED_APPLY)
            .message(message)
            .pushScene(scene)
            .pushOperator(pushOperatorCode)
            .readed(Whether.NO)
            .build();
    }
}
