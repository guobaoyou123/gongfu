package com.linzhi.gongfu.service;


import com.linzhi.gongfu.dto.TBrand;
import com.linzhi.gongfu.dto.TCompTradeApply;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.*;
import com.linzhi.gongfu.mapper.BrandMapper;
import com.linzhi.gongfu.mapper.CompTradeApplyMapper;
import com.linzhi.gongfu.mapper.CompTradeMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
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
    private final BlacklistRepository blacklistRepository;
    private final EnrolledCompanyRepository enrolledCompanyRepository;
    private final CompTradeRepository compTradeRepository;
    private final BrandMapper brandMapper;
    /**
     * 申请采购
     * @param vTradeApplyRequest 申请信息
     * @param companyCode 单位编码
     * @param operatorCode 操作员编码
     * @param companyName 单位名称
     * @return 返回是或否
     */
    @Caching(evict = {
        @CacheEvict(value="trade_apply_history_List;1800", key="#companyCode"),
        @CacheEvict(value="trade_apply_List;1800", key="#vTradeApplyRequest.applyCompCode+'-'+1")
    })
    @Transactional
    public Map<String,String> saveTradeApply(VTradeApplyRequest vTradeApplyRequest,String companyCode,String operatorCode,String companyName){
          Map<String,String> map = new HashMap<>();
          map.put("flag","0");
        try{

            //查询是否有正在申请中的
           CompTradeApply compTradeApply =  compTradeApplyRepository.findByCreatedCompByAndHandledCompByAndStateAndType(
                companyCode,
                vTradeApplyRequest.getApplyCompCode(),
                TradeApply.APPLYING,
                "1"
           ).orElse(null);
           if(compTradeApply!=null)
                 return map;
           //判断是否在对方黑名单的
           Blacklist blacklist = blacklistRepository.findById(
                BlacklistId.builder()
                    .type("1")
                    .beRefuseCompId(companyCode)
                    .dcCompId(vTradeApplyRequest.getApplyCompCode())
                .build()
           ).orElse(null);
           //查询本单位基础信息
           EnrolledCompany enrolledCompany =  enrolledCompanyRepository.findById(companyCode).orElseThrow(()->new IOException("未从数据库找到公司信息"));
           //申请记录编码 SQCG-申请单位-被申请单位-时间-随机数
           // 生成申请采购记录
           CompTradeApply compTradeApply1 = CompTradeApply.builder()
              .code("SQCG-"+companyCode+"-"+vTradeApplyRequest.getApplyCompCode()+"-"+UUID.randomUUID().toString().substring(0,8))
              .createdCompBy(companyCode)
              .createdBy(operatorCode)
              .createdAt(LocalDateTime.now())
              .createdRemark(vTradeApplyRequest.getRemark())
              .handledCompBy(vTradeApplyRequest.getApplyCompCode())
               .handledBy(blacklist==null?null:blacklist.getCreatedBy())
              .type("1")
              .state(blacklist==null?TradeApply.APPLYING:TradeApply.REFUSE)
               .shortNameInCN(enrolledCompany.getDetails().getShortNameInCN())
               .contactName(enrolledCompany.getCompVisible()!=null&&enrolledCompany.getCompVisible().getVisibleContent().contains("contactPhone")?enrolledCompany.getDetails().getContactName():null)
               .contactPhone(enrolledCompany.getCompVisible()!=null&&enrolledCompany.getCompVisible().getVisibleContent().contains("contactPhone")?enrolledCompany.getDetails().getContactPhone():null)
               .areaCode(enrolledCompany.getCompVisible()!=null&&enrolledCompany.getCompVisible().getVisibleContent().contains("address")?enrolledCompany.getDetails().getAreaCode():null)
               .areaName(enrolledCompany.getCompVisible()!=null&&enrolledCompany.getCompVisible().getVisibleContent().contains("address")?enrolledCompany.getDetails().getAreaName():null)
               .address(enrolledCompany.getCompVisible()!=null&&enrolledCompany.getCompVisible().getVisibleContent().contains("address")?enrolledCompany.getDetails().getAddress():null)
               .introduction(enrolledCompany.getCompVisible()!=null&&enrolledCompany.getCompVisible().getVisibleContent().contains("introduction")?enrolledCompany.getIntroduction():null)
               .build();
           compTradeApplyRepository.save(compTradeApply1);
           if(blacklist!=null){
               map.put("flag","2");
               map.put("code",compTradeApply1.getCode());
               return map;
           }
           //查找有格友综合管理权限的场景
           List<String> sceneList= sceneMenuRepository.findList("格友管理").stream()
                   .map(sceneMenu -> sceneMenu.getSceneMenuId().getSceneCode())
                   .toList();
           //存入消息通知表
           List<Notification> notifications = new ArrayList<>();
               sceneList.forEach(s -> {
                   //消息通知编码  XXTZ-类型-申请单位-操作员-时间戳-随机数
                   Notification notification = createdNotification(companyCode,companyName+"公司申请采购",operatorCode,NotificationType.ENROLLED_APPLY,compTradeApply1.getCode(),
                       vTradeApplyRequest.getApplyCompCode(),s,null);
                   notifications.add(notification);
               });
           notificationRepository.saveAll(notifications);

            //销毁邀请码
           if(vTradeApplyRequest.getInvitationCode()!=null)
                  compInvitationCodeRepository.deleteByCompInvitationCodeId_InvitationCode(vTradeApplyRequest.getInvitationCode());
           map.put("flag","1");
           map.put("code",compTradeApply1.getCode());
           return map ;
        }catch (Exception e){
            e.printStackTrace();
            return map;
        }
    }

    /**
     * 分页查询待处理列表
     * @param companyCode 单位编码
     * @param pageable 分页
     * @param name 公司名称
     * @return 返回待处理列表
     */
    public Page<VTradeApplyPageResponse.VTradeApply> pageTradeApplies(String companyCode, Pageable pageable, String name){
         List<VTradeApplyPageResponse.VTradeApply> compTradeApplies=  compTradeApplyRepository.findByHandledCompByAndStateAndTypeOrderByCreatedAtDesc(companyCode,TradeApply.APPLYING,"1")
             .stream().filter(compTradeApply -> compTradeApply.getCreatedCompany().getNameInCN().contains(name))
             .map(compTradeApplyMapper::toTComTradeApply)
             .map(compTradeApplyMapper::toVTradeApply)
             .toList();

        return PageTools.listConvertToPage(compTradeApplies,pageable);
    }

    /**
     * 同意申请采购
     * @param compTradeApply 申请记录
     * @param companyCode 单位编码
     * @param companyName 单位名称
     * @param operatorCode 操作员编码
     * @param vTradeApplyConsentRequest 交易信息
     * @return 返回是或者否
     */
    @Caching(evict = {
        @CacheEvict(value="trade_apply_history_List;1800", key="#companyCode"),
        @CacheEvict(value="trade_apply_List;1800", key="#companyCode+'-'+1"),
        @CacheEvict(value="trade_apply_history_List;1800", key="#compTradeApply.handledCompBy"),
        @CacheEvict(value="trade_apply_detail;1800", key="#compTradeApply.code")

    })
    public boolean consentApply(CompTradeApply compTradeApply, String companyCode,String companyName, String operatorCode, VTradeApplyConsentRequest vTradeApplyConsentRequest){
        try{


           compTradeApply.setHandledBy(operatorCode);
           compTradeApply.setHandledAt(LocalDateTime.now());
           compTradeApply.setState(TradeApply.AGREE);
           compTradeApplyRepository.save(compTradeApply);
           //生成交易信息
            CompTradDetail compTrad = CompTradDetail.builder()
                .compTradId(
                    CompTradId.builder()
                    .compSaler(companyCode)
                    .compBuyer(compTradeApply.getCreatedCompBy())
                    .build()
                )
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
                NotificationType.ENROLLED_APPLY_HISTORY,
                compTradeApply.getCode(),
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
     * 根据申请记录编码查找申请记录
     * @param code 申请记录编码
     * @return 申请记录
     * @throws IOException 异常
     */
    public CompTradeApply  getCompInvitationCode(String code) throws IOException {
        return  compTradeApplyRepository.findById(code).orElseThrow(()->new IOException("从数据中未找到该申请"));
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
    public Notification createdNotification(String companyCode,String message,String operatorCode,NotificationType type,String id,String pushComp,String scene,String pushOperatorCode){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate data=LocalDate.now();
        return  Notification.builder()
            .code("XXTZ-"+type.getType()+"-"+companyCode+"-"+operatorCode+"-"+dtf.format(data)+"-"+UUID.randomUUID().toString().substring(0,8))
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

    /**
     * 拒绝申请和始终拒绝申请
     * @param companyCode 单位编码
     * @param companyName 单位名称
     * @param operatorCode 操作员编码
     * @param remark 拒绝原因
     * @param state 状态 1-拒绝 2-始终拒绝
     * @param compTradeApply 申请记录
     * @return 返回是或者否
     */
    @Caching(evict = {
        @CacheEvict(value="trade_apply_history_List;1800", key="#companyCode"),
        @CacheEvict(value="trade_apply_List;1800", key="#companyCode+'-'+1"),
        @CacheEvict(value="trade_apply_history_List;1800", key="#compTradeApply.handledCompBy"),
        @CacheEvict(value="trade_apply_detail;1800", key="#compTradeApply.code")
    })
    @Transactional
    public boolean refuseApply(String companyCode,String companyName,
                               String operatorCode,String remark,
                               String state,CompTradeApply compTradeApply){
        try {
            compTradeApply.setHandledBy(operatorCode);
            compTradeApply.setRefuseRemark(remark);
            compTradeApply.setState(TradeApply.REFUSE);
            compTradeApply.setHandledAt(LocalDateTime.now());
            if(state.equals("2")){
                blacklistRepository.save(
                    Blacklist.builder()
                       .blacklistId(
                           BlacklistId.builder()
                             .dcCompId(companyCode)
                             .beRefuseCompId(compTradeApply.getCreatedCompBy())
                              .type("1")
                           .build()
                       )
                        .createdAt(LocalDateTime.now())
                        .createdBy(operatorCode)
                    .build()
                );
            }
            //存入消息通知表
            Notification notification = createdNotification(
                companyCode,
                companyName+"公司拒绝了您的申请采购的请求",
                operatorCode,
                NotificationType.ENROLLED_APPLY_HISTORY,
                compTradeApply.getCode(),
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
     * 查询申请历史记录列表分页
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param  name  公司名称
     * @param pageable 分页
     * @return 返回申请历史记录列表
     */
    public Page<VTradeApplyHistoryResponse.VApply> pageApplyHistories(
        String startTime,
        String endTime,
        String type,
        String name,
        Pageable pageable,
        String companyCode
    ){
        List<TCompTradeApply> compTradeApplies=compTradeApplyRepository.listApplyHistories(companyCode).stream()
            .filter(compTradeApply -> {
                if(compTradeApply.getCreatedCompBy().equals(companyCode)){

                    return compTradeApply.getHandledCompany().getNameInCN().contains(name);
                }else {
                    return compTradeApply.getCreatedCompany().getNameInCN().contains(name);
                }
            })
            .filter(compTradeApply -> {
                if(StringUtils.isNotBlank(startTime)&&StringUtils.isNotBlank(endTime)){
                    DateTimeFormatter dateTimeFormatterDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter dateTimeFormatters = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime startTimes = LocalDate.parse(startTime, dateTimeFormatterDay).atStartOfDay();
                    LocalDateTime endTimes = LocalDateTime.parse(endTime+" 23:59:59", dateTimeFormatters);
                    return compTradeApply.getCreatedAt().isAfter(startTimes) && compTradeApply.getCreatedAt().isBefore(endTimes);
                }
                return true;
            })
            .filter(compTradeApply -> {
                if(type.equals("1")){
                    return  !compTradeApply.getCreatedCompBy().equals(companyCode);
                }else if(type.equals("2")){
                    return  compTradeApply.getCreatedCompBy().equals(companyCode);
                }
                return true;
            })
            .map(compTradeApplyMapper::toTComTradeApplyHistory)
            .toList();
        compTradeApplies.forEach(tradeApply -> tradeApply.setDcCompId(companyCode));
        return PageTools.listConvertToPage(compTradeApplies.stream().map(compTradeApplyMapper::toVApplyHistory).toList(),pageable);
    }

    /**
     * 拒绝名单中格友公司可见详情
     * @param enrolledCode 格友编码
     *  @param companyCode 公司编码
     * @return 格友公司可见详情
     */
    public Optional<VEnrolledCompanyResponse.VCompany> getRefuseEnrolledCompanyDetail(
        String enrolledCode,
        String companyCode
    ) throws IOException {
        //最后一次申请记录
        CompTradeApply compTradeApply = compTradeApplyRepository.findTopByCreatedCompByAndHandledCompByAndTypeOrderByCreatedAtDesc(
            companyCode,
            enrolledCode,
            "1"
        ).orElseThrow(()->new IOException("未从数据库找到"));
        TCompTradeApply tradeApply=  compTradeApplyMapper.toEnrolledCompanyDetail(compTradeApply,companyCode);
        return  Optional.of(tradeApply).map(compTradeApplyMapper::toTCompTradeApplyDetail);
    }

    /**
     * 申请记录详情（待处理和历史）
     * @param code 申请记录编码
     * @param companyCode 本单位编码
     */
    public  Optional<VTradeApplyDetailResponse.VApply>  getTradeApplyDetail(String code,String companyCode) throws IOException {

         CompTradeApply compTradeApply = compTradeApplyRepository.findById(code).orElseThrow(()->new IOException("未查询到数据"));
         TCompTradeApply tradeApply = compTradeApplyMapper.toEnrolledCompanyDetail(compTradeApply,companyCode);
         //判断该申请记录是否通过
         if(compTradeApply.getState().equals(TradeApply.AGREE)){
             //查询交易品牌和税模式
             CompTrad compTrad = compTradeRepository.findById(CompTradId.builder()
                     .compBuyer(compTradeApply.getCreatedCompBy())
                     .compSaler(compTradeApply.getHandledCompBy())
                 .build()).orElseThrow(()->new IOException("未从数据库中找到交易信息"));
             List<TBrand> tBrands=compTrad.getManageBrands().stream().map(brandMapper::toBrand).toList();
             tradeApply.setBrands(tBrands);
             tradeApply.setTaxModel(compTrad.getTaxModel().getTaxMode()+"");
         }
         return  Optional.of(tradeApply).map(compTradeApplyMapper::toApplyDetail);
    }

    public void  listRefused(){

    }
}
