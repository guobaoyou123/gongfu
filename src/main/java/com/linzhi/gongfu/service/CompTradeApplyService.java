package com.linzhi.gongfu.service;


import com.linzhi.gongfu.entity.CompTradeApply;
import com.linzhi.gongfu.entity.Notification;
import com.linzhi.gongfu.enumeration.NotificationType;
import com.linzhi.gongfu.enumeration.TradeApply;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.mapper.CompTradeApplyMapper;
import com.linzhi.gongfu.repository.CompInvitationCodeRepository;
import com.linzhi.gongfu.repository.CompTradeApplyRepository;
import com.linzhi.gongfu.repository.NotificationRepository;
import com.linzhi.gongfu.repository.SceneMenuRepository;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.VTradeApplyPageResponse;
import com.linzhi.gongfu.vo.VTradeApplyRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd");
                LocalDate data=LocalDate.now();
                Notification notification = Notification.builder()
                    .code("XXTZ-0-"+companyCode+"-"+operatorCode+"-"+dtf.format(data)+"-"+UUID.randomUUID().toString().substring(0,8))
                    .createdAt(LocalDateTime.now())
                    .id(compTradeApply1.getCode())
                    .createdBy(operatorCode)
                    .createdCompBy(companyCode)
                    .pushComp(vTradeApplyRequest.getApplyCompCode())
                    .type(NotificationType.ENROLLED_APPLY)
                    .message(companyName+"公司申请采购")
                    .pushScene(s)
                    .readed(Whether.NO)
                    .build();
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

}
