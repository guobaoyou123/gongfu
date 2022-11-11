package com.linzhi.gongfu.service;


import com.linzhi.gongfu.dto.TBrand;
import com.linzhi.gongfu.dto.TCompTradeApply;
import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.NotificationType;
import com.linzhi.gongfu.enumeration.TaxMode;
import com.linzhi.gongfu.enumeration.TradeApply;
import com.linzhi.gongfu.mapper.BlacklistMapper;
import com.linzhi.gongfu.mapper.BrandMapper;
import com.linzhi.gongfu.mapper.CompTradeApplyMapper;
import com.linzhi.gongfu.mapper.OperatorMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final CompInvitationCodeRepository compInvitationCodeRepository;

    private final CompTradeApplyMapper compTradeApplyMapper;
    private final CompTradeBaseRepository compTradDetailRepository;
    private final CompTradeBrandRepository compTradBrandRepository;
    private final BlacklistRepository blacklistRepository;
    private final EnrolledCompanyRepository enrolledCompanyRepository;
    private final CompTradeRepository compTradeRepository;
    private final BrandMapper brandMapper;
    private final BlacklistMapper blacklistMapper;
    private final OperatorRepository operatorRepository;
    private final OperatorMapper operatorMapper;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    /**
     * 申请采购
     *
     * @param vTradeApplyRequest 申请信息
     * @param companyCode        单位编码
     * @param operatorCode       操作员编码
     * @return 返回是或否
     */
    @Caching(evict = {
        @CacheEvict(value = "trade_apply_history_List;1800", key = "#companyCode"),
        @CacheEvict(value = "trade_apply_List;1800", key = "#vTradeApplyRequest.applyCompCode+'-'+1"),
        @CacheEvict(value = "Notification_List;1800", key = "#vTradeApplyRequest.applyCompCode+ '-' + '*'")
    })
    @Transactional
    public Map<String, String> saveTradeApply(VTradeApplyRequest vTradeApplyRequest, String companyCode, String operatorCode) {
        Map<String, String> map = new HashMap<>();
        map.put("flag", "0");
        try {
            //查询是否有正在申请中的
            CompTradeApply compTradeApply = compTradeApplyRepository.findByCreatedCompByAndHandledCompByAndStateAndType(
                companyCode,
                vTradeApplyRequest.getApplyCompCode(),
                TradeApply.APPLYING,
                "1"
            ).orElse(null);
            if (compTradeApply != null)
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
            EnrolledCompany enrolledCompany = enrolledCompanyRepository.findById(companyCode).orElseThrow(() -> new IOException("未从数据库找到公司信息"));
            //申请记录编码 SQCG-申请单位-被申请单位-时间-随机数
            //生成申请采购记录
            CompTradeApply compTradeApply1 = CompTradeApply.builder()
                .code("SQCG-" + companyCode + "-" + vTradeApplyRequest.getApplyCompCode() + "-" + UUID.randomUUID().toString().substring(0, 8))
                .createdCompBy(companyCode)
                .createdBy(operatorCode)
                .createdAt(LocalDateTime.now())
                .createdRemark(vTradeApplyRequest.getRemark())
                .handledCompBy(vTradeApplyRequest.getApplyCompCode())
                .handledBy(blacklist == null ? null : blacklist.getCreatedBy())
                .type("1")
                .state(blacklist == null ? TradeApply.APPLYING : TradeApply.REFUSE)
                .shortNameInCN(enrolledCompany.getDetails().getShortNameInCN())
                .contactName(enrolledCompany.getCompVisible() != null && enrolledCompany.getCompVisible().getVisibleContent().contains("contactPhone") ? enrolledCompany.getDetails().getContactName() : null)
                .contactPhone(enrolledCompany.getCompVisible() != null && enrolledCompany.getCompVisible().getVisibleContent().contains("contactPhone") ? enrolledCompany.getDetails().getContactPhone() : null)
                .areaCode(enrolledCompany.getCompVisible() != null && enrolledCompany.getCompVisible().getVisibleContent().contains("address") ? enrolledCompany.getDetails().getAreaCode() : null)
                .areaName(enrolledCompany.getCompVisible() != null && enrolledCompany.getCompVisible().getVisibleContent().contains("address") ? enrolledCompany.getDetails().getAreaName() : null)
                .address(enrolledCompany.getCompVisible() != null && enrolledCompany.getCompVisible().getVisibleContent().contains("address") ? enrolledCompany.getDetails().getAddress() : null)
                .introduction(enrolledCompany.getCompVisible() != null && enrolledCompany.getCompVisible().getVisibleContent().contains("introduction") ? enrolledCompany.getIntroduction() : null)
                .build();
            compTradeApplyRepository.save(compTradeApply1);
            if (blacklist != null) {
                map.put("flag", "2");
                map.put("code", compTradeApply1.getCode());
                return map;
            }

            //销毁邀请码
            if (vTradeApplyRequest.getInvitationCode() != null)
                compInvitationCodeRepository.deleteByCompInvitationCodeId_InvitationCode(vTradeApplyRequest.getInvitationCode());
            map.put("flag", "1");
            map.put("code", compTradeApply1.getCode());
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return map;
        }
    }

    /**
     * 分页查询待处理列表
     *
     * @param companyCode 单位编码
     * @param pageable    分页
     * @param name        公司名称
     * @return 返回待处理列表
     */
    public Page<VTradeApplyPageResponse.VTradeApply> pageTradeApplies(String companyCode, Pageable pageable, String name) {
        List<VTradeApplyPageResponse.VTradeApply> compTradeApplies = compTradeApplyRepository.findByHandledCompByAndStateAndTypeOrderByCreatedAtDesc(companyCode, TradeApply.APPLYING, "1")
            .stream().filter(compTradeApply -> compTradeApply.getCreatedCompany().getNameInCN().contains(name))
            .map(compTradeApplyMapper::toTComTradeApply)
            .map(compTradeApplyMapper::toVTradeApply)
            .toList();

        return PageTools.listConvertToPage(compTradeApplies, pageable);
    }

    /**
     * 查找待处理申请数量
     *
     * @param name        格友公司名称
     * @param companyCode 本单位编码
     * @return 待处理申请数量
     */
    public int tradeApplyAmount(String name, String companyCode) {
        return compTradeApplyRepository.findByHandledCompByAndStateAndTypeOrderByCreatedAtDesc(companyCode, TradeApply.APPLYING, "1")
            .stream().filter(compTradeApply -> compTradeApply.getCreatedCompany().getNameInCN().contains(name))
            .toList().size();
    }


    /**
     * 同意申请采购
     *
     * @param compTradeApply            申请记录
     * @param companyCode               单位编码
     * @param operatorCode              操作员编码
     * @param vTradeApplyConsentRequest 交易信息
     * @return 返回是或者否
     */
    @Caching(evict = {
        @CacheEvict(value = "trade_apply_history_List;1800", key = "#companyCode"),
        @CacheEvict(value = "trade_apply_List;1800", key = "#companyCode+'-'+1"),
        @CacheEvict(value = "trade_apply_history_List;1800", key = "#compTradeApply.handledCompBy"),
        @CacheEvict(value = "trade_apply_detail;1800", key = "#compTradeApply.code"),
        @CacheEvict(value = "Supplier_List;1800", key = "#compTradeApply.handledCompBy+'*'"),
        @CacheEvict(value = "SupplierAndBrand;1800", key = "#compTradeApply.handledCompBy"),
        @CacheEvict(value = "brands_company;1800", key = "#compTradeApply.handledCompBy"),
        @CacheEvict(value = "Customer_List;1800", key = "#companyCode+'*'"),
        @CacheEvict(value = "Customer_List_All;1800", key = "#companyCode+'*'"),
    })
    public void consentApply(CompTradeApply compTradeApply, String companyCode, String operatorCode, VTradeApplyConsentRequest vTradeApplyConsentRequest,String companyName) throws Exception {
        try {
            compTradeApply.setHandledBy(operatorCode);
            compTradeApply.setHandledAt(LocalDateTime.now());
            compTradeApply.setState(TradeApply.AGREE);
            compTradeApplyRepository.save(compTradeApply);
            //生成交易信息
            CompTradeBase compTrad = CompTradeBase.builder()
                .compTradeId(
                    CompTradeId.builder()
                        .compSaler(companyCode)
                        .compBuyer(compTradeApply.getCreatedCompBy())
                        .build()
                )
                .taxModel(vTradeApplyConsentRequest.getTaxModel().equals("0") ? TaxMode.UNTAXED : TaxMode.INCLUDED)
                .state(Availability.ENABLED)
                .salerBelongTo(StringUtils.join(vTradeApplyConsentRequest.getAuthorizedOperator(), ","))
                .build();
            compTradDetailRepository.save(compTrad);
            List<CompTradeBrand> compTradBrands = new ArrayList<>();
            vTradeApplyConsentRequest.getBrandCodes().forEach(s -> {
                CompTradeBrand compTradBrand = CompTradeBrand.builder()
                    .compTradeBrandId(CompTradeBrandId.builder()
                        .compBuyer(compTradeApply.getCreatedCompBy())
                        .compSaler(companyCode)
                        .brandCode(s)
                        .build())
                    .sort(0)
                    .build();
                compTradBrands.add(compTradBrand);
            });
            compTradBrandRepository.saveAll(compTradBrands);
            notificationRepository.save(notificationService.createdNotification(companyCode,
                companyName+ "公司同意了您的申请采购的请求",
                operatorCode,
                NotificationType.ENROLLED_APPLY_HISTORY,
                compTradeApply.getCode(),
                compTradeApply.getCreatedCompBy(),
                Arrays.asList(new String[]{compTradeApply.getCreatedBy()})));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("数据保存失败");
        }
    }

    /**
     * 根据申请记录编码查找申请记录
     *
     * @param code 申请记录编码
     * @return 申请记录
     * @throws IOException 异常
     */
    public CompTradeApply getCompInvitationCode(String code) throws IOException {
        return compTradeApplyRepository.findById(code).orElseThrow(() -> new IOException("从数据中未找到该申请"));
    }


    /**
     * 拒绝申请和始终拒绝申请
     *
     * @param companyCode    单位编码
     * @param operatorCode   操作员编码
     * @param remark         拒绝原因
     * @param state          状态 1-拒绝 2-始终拒绝
     * @param compTradeApply 申请记录
     * @return 返回是或者否
     */
    @Caching(evict = {
        @CacheEvict(value = "trade_apply_history_List;1800", key = "#companyCode"),
        @CacheEvict(value = "trade_apply_List;1800", key = "#companyCode+'-'+1"),
        @CacheEvict(value = "trade_apply_history_List;1800", key = "#compTradeApply.handledCompBy"),
        @CacheEvict(value = "trade_apply_detail;1800", key = "#compTradeApply.code"),
        @CacheEvict(value = "Black_list;1800", key = "#companyCode")
    })
    @Transactional
    public boolean refuseApply(String companyCode,
                               String operatorCode, String remark,
                               String state, CompTradeApply compTradeApply,String companyName) throws Exception {
        try {
            //拒绝申请的
            if (!compTradeApply.getState().equals(TradeApply.APPLYING) && state.equals("1"))
                return false;
            //始终拒绝申请的
            if (state.equals("2") && (compTradeApply.getState().equals(TradeApply.ALWAYS_REFUSE) || compTradeApply.getState().equals(TradeApply.AGREE))) {
                return false;
            }
            compTradeApply.setHandledBy(operatorCode);
            compTradeApply.setRefuseRemark(remark);
            compTradeApply.setState(TradeApply.REFUSE);
            compTradeApply.setHandledAt(LocalDateTime.now());
            if (state.equals("2")) {
                compTradeApply.setState(TradeApply.ALWAYS_REFUSE);
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
            compTradeApplyRepository.save(compTradeApply);

            var notification = notificationService.createdNotification(
                companyCode,
                companyName+ "拒绝了您的申请采购的请求",
                operatorCode,
                NotificationType.ENROLLED_APPLY_HISTORY,
                compTradeApply.getCode(),
                compTradeApply.getCreatedCompBy(),
                Arrays.asList(new String[]{compTradeApply.getCreatedBy()}));
            notificationRepository.save(notification);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
           throw new Exception("操作失败");
        }
    }

    /**
     * 查询申请历史记录列表分页
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param name      公司名称
     * @param pageable  分页
     * @return 返回申请历史记录列表
     */
    public Page<VTradeApplyHistoryResponse.VApply> pageApplyHistories(
        String startTime,
        String endTime,
        String type,
        String name,
        Pageable pageable,
        String companyCode
    ) {
        List<TCompTradeApply> compTradeApplies = compTradeApplyRepository.listApplyHistories(companyCode).stream()
            .filter(compTradeApply -> {
                if (compTradeApply.getCreatedCompBy().equals(companyCode)) {

                    return compTradeApply.getHandledCompany().getNameInCN().contains(name);
                } else {
                    return compTradeApply.getCreatedCompany().getNameInCN().contains(name);
                }
            })
            .filter(compTradeApply -> {
                if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
                    DateTimeFormatter dateTimeFormatterDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter dateTimeFormatters = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime startTimes = LocalDate.parse(startTime, dateTimeFormatterDay).atStartOfDay();
                    LocalDateTime endTimes = LocalDateTime.parse(endTime + " 23:59:59", dateTimeFormatters);
                    return compTradeApply.getCreatedAt().isAfter(startTimes) && compTradeApply.getCreatedAt().isBefore(endTimes);
                }
                return true;
            })
            .filter(compTradeApply -> {
                if (type.equals("1")) {
                    return !compTradeApply.getCreatedCompBy().equals(companyCode);
                } else if (type.equals("2")) {
                    return compTradeApply.getCreatedCompBy().equals(companyCode);
                }
                return true;
            })
            .map(compTradeApplyMapper::toTComTradeApplyHistory)
            .toList();
        compTradeApplies.forEach(tradeApply -> {
            if (tradeApply.getCreatedCompBy().equals(companyCode) && tradeApply.getState().equals("3")) {
                tradeApply.setState("2");
            }
            tradeApply.setDcCompId(companyCode);
        });
        return PageTools.listConvertToPage(compTradeApplies.stream().map(compTradeApplyMapper::toVApplyHistory).toList(), pageable);
    }

    /**
     * 拒绝名单中格友公司可见详情
     *
     * @param enrolledCode 格友编码
     * @param companyCode  公司编码
     * @return 格友公司可见详情
     */
    @Cacheable(value = "Refused_Enrolled_Company", key = "#companyCode+'-'+#enrolledCode")
    public Optional<VEnrolledCompanyResponse.VCompany> getRefuseEnrolledCompanyDetail(
        String enrolledCode,
        String companyCode
    ) throws IOException {
        //最后一次申请记录
        CompTradeApply compTradeApply = compTradeApplyRepository.findTopByCreatedCompByAndHandledCompByAndTypeOrderByCreatedAtDesc(
            enrolledCode,
            companyCode,
            "1"
        ).orElseThrow(() -> new IOException("未从数据库找到"));
        TCompTradeApply tradeApply = compTradeApplyMapper.toEnrolledCompanyDetail(compTradeApply, companyCode);
        return Optional.of(tradeApply).map(compTradeApplyMapper::toTCompTradeApplyDetail);
    }

    /**
     * 申请记录详情（待处理和历史）
     *
     * @param code        申请记录编码
     * @param companyCode 本单位编码
     */
    @Cacheable(value = "trade_apply_detail;1800", key = "#code")
    public Optional<VTradeApplyDetailResponse.VApply> getTradeApplyDetail(String code, String companyCode) throws IOException {

        CompTradeApply compTradeApply = compTradeApplyRepository.findById(code).orElseThrow(() -> new IOException("未查询到数据"));
        TCompTradeApply tradeApply = compTradeApplyMapper.toEnrolledCompanyDetail(compTradeApply, companyCode);
        //判断该申请记录是否通过
        if (compTradeApply.getState().equals(TradeApply.AGREE)) {
            //查询交易品牌和税模式
            CompTrade compTrad = compTradeRepository.findById(CompTradeId.builder()
                .compBuyer(compTradeApply.getCreatedCompBy())
                .compSaler(compTradeApply.getHandledCompBy())
                .build()).orElseThrow(() -> new IOException("未从数据库中找到交易信息"));
            List<TBrand> tBrands = compTrad.getManageBrands().stream().map(brandMapper::toBrand).toList();
            tradeApply.setBrands(tBrands);
            tradeApply.setTaxModel(compTrad.getTaxModel().getTaxMode() + "");
            //查询本单位负责人
            List<Operator> operatorList = operatorRepository.findOperatorByIdentity_CompanyCodeAndIdentity_OperatorCodeIn(
                companyCode,

                compTradeApply.getState().equals(TradeApply.AGREE) && compTradeApply.getHandledCompBy().equals(companyCode) ?
                    compTrad.getSalerBelongTo() != null ? Arrays.asList(compTrad.getSalerBelongTo().split(",")) : new ArrayList<>() : compTrad.getBuyerBelongTo() != null ? Arrays.asList(compTrad.getBuyerBelongTo().split(",")) : new ArrayList<>()

            );
            tradeApply.setOperators(operatorList.stream().map(operatorMapper::toDTO)
                .toList());
        }
        return Optional.of(tradeApply).map(compTradeApplyMapper::toApplyDetail);
    }

    /**
     * 查询黑名单列表
     *
     * @param companyCode 公司编码
     * @return 返回黑名单列表
     */
    @Cacheable(value = "Black_list;1800", key = "#companyCode")
    public List<TCompanyBaseInformation> listRefused(String companyCode) {
        return blacklistRepository.findBlacklistsByBlacklistId_DcCompId(companyCode).stream()
            .map(blacklistMapper::toTCompanyDetail)
            .toList();
    }

    /**
     * 取消始终拒绝
     *
     * @param companyCode   本单位编码
     * @param beRefusedCode 被拒绝格友单位编码
     * @param type          类型1-申请采购 2-
     * @return 返回是或者否
     */
    @Caching(evict = {
        @CacheEvict(value = "Black_list;1800", key = "#companyCode"),
        @CacheEvict(value = "Refused_Enrolled_Company", key = "#companyCode+'-'+#beRefusedCode")
    })
    @Transactional
    public boolean removeRefused(String companyCode, String beRefusedCode, String type) {
        try {
            blacklistRepository.deleteById(BlacklistId.builder()
                .dcCompId(companyCode)
                .type(type)
                .beRefuseCompId(beRefusedCode)
                .build());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
