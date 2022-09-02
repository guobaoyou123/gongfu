package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TCompTradeApply;
import com.linzhi.gongfu.entity.CompTradeApply;
import com.linzhi.gongfu.vo.VEnrolledCompanyResponse;
import com.linzhi.gongfu.vo.VTradeApplyDetailResponse;
import com.linzhi.gongfu.vo.VTradeApplyHistoryResponse;
import com.linzhi.gongfu.vo.VTradeApplyPageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换申请采购相关信息
 *
 * @author zgh
 * @create_at 2022-07-20
 */
@Mapper(componentModel = "spring")
public interface CompTradeApplyMapper {
    /**
     * 待处理的申请信息
     *
     * @param compTradeApply 待处理申请
     * @return 待处理申请信息
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "createdCompBy", source = "createdCompBy")
    @Mapping(target = "createdCompanyName", source = "createdCompany.nameInCN")
    @Mapping(target = "createdCompanyShortName", source = "createdCompany.details.shortNameInCN")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "state", expression = "java(String.valueOf(compTradeApply.getState().getState()))")
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.dateFormat(compTradeApply.getCreatedAt()))")
    TCompTradeApply toTComTradeApply(CompTradeApply compTradeApply);

    /**
     * 申请历史记录
     *
     * @param compTradeApply 历史记录
     * @return 申请历史记录详情
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "createdCompBy", source = "createdCompBy")
    @Mapping(target = "createdCompanyName", source = "createdCompany.nameInCN")
    @Mapping(target = "createdCompanyShortName", source = "createdCompany.details.shortNameInCN")
    @Mapping(target = "handledCompanyCode", source = "handledCompBy")
    @Mapping(target = "handledCompanyName", source = "handledCompany.nameInCN")
    @Mapping(target = "handledCompanyShortName", source = "handledCompany.details.shortNameInCN")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "remark", source = "createdRemark")
    @Mapping(target = "state", expression = "java(String.valueOf(compTradeApply.getState().getState()))")
    @Mapping(target = "createdAt", expression = "java(com.linzhi.gongfu.util.DateConverter.dateFormat(compTradeApply.getCreatedAt()))")
    TCompTradeApply toTComTradeApplyHistory(CompTradeApply compTradeApply);

    /**
     * 待处理的申请信息
     *
     * @param tradeApply 待处理申请
     * @return 待处理申请信息
     */
    @Mapping(target = "companyShortName", source = "createdCompanyShortName")
    @Mapping(target = "companyCode", source = "createdCompBy")
    @Mapping(target = "companyName", source = "createdCompanyName")
    VTradeApplyPageResponse.VTradeApply toVTradeApply(TCompTradeApply tradeApply);

    /**
     * 申请历史记录
     *
     * @param tradeApply 历史记录
     * @return 申请历史记录详情
     */
    @Mapping(target = "companyCode", expression = "java(tradeApply.getDcCompId().equals(tradeApply.getCreatedCompBy())?tradeApply.getHandledCompanyCode():tradeApply.getCreatedCompBy())")
    @Mapping(target = "companyName", expression = "java(tradeApply.getDcCompId().equals(tradeApply.getCreatedCompBy())?tradeApply.getHandledCompanyName():tradeApply.getCreatedCompanyName())")
    @Mapping(target = "companyShortName", expression = "java(tradeApply.getDcCompId().equals(tradeApply.getCreatedCompBy())?tradeApply.getHandledCompanyShortName():tradeApply.getCreatedCompanyShortName())")
    VTradeApplyHistoryResponse.VApply toVApplyHistory(TCompTradeApply tradeApply);

    /**
     * 申请记录详情
     *
     * @param tradeApply 申请记录
     * @param dcCompId   公司编码
     * @return 申请记录详情
     */
    @Mapping(target = "createdAt", expression = "java(tradeApply.getCreatedAt()==null?null:com.linzhi.gongfu.util.DateConverter.dateFormat(tradeApply.getCreatedAt()))")
    @Mapping(target = "state", expression = "java(String.valueOf(tradeApply.getState().getState()))")
    @Mapping(target = "remark", source = "tradeApply.createdRemark")
    @Mapping(target = "handledAt", expression = "java(tradeApply.getHandledAt()==null?null:com.linzhi.gongfu.util.DateConverter.dateFormat(tradeApply.getHandledAt()))")
    @Mapping(target = "companyCode", expression = "java(dcCompId.equals(tradeApply.getCreatedCompBy())?tradeApply.getHandledCompBy():tradeApply.getCreatedCompBy())")
    @Mapping(target = "companyName", expression = "java(dcCompId.equals(tradeApply.getCreatedCompBy())?tradeApply.getHandledCompany().getNameInCN():tradeApply.getCreatedCompany().getNameInCN())")
    @Mapping(target = "companyShortName", expression = "java(dcCompId.equals(tradeApply.getCreatedCompBy())?tradeApply.getHandledCompany().getDetails().getShortNameInCN():tradeApply.getShortNameInCN())")
    @Mapping(target = "type", expression = "java(dcCompId.equals(tradeApply.getCreatedCompBy())?\"2\":\"1\")")
    @Mapping(target = "usci", expression = "java(dcCompId.equals(tradeApply.getCreatedCompBy())?tradeApply.getHandledCompany().getUSCI():tradeApply.getCreatedCompany().getUSCI() )")
    @Mapping(target = "contactName", expression = "java(dcCompId.equals(tradeApply.getCreatedCompBy())?tradeApply.getHandledCompany().getCompVisible()!=null&&tradeApply.getHandledCompany().getCompVisible().getVisibleContent().contains(\"contactPhone\")?tradeApply.getHandledCompany().getDetails().getContactName():null:tradeApply.getContactName())")
    @Mapping(target = "contactPhone", expression = "java(dcCompId.equals(tradeApply.getCreatedCompBy())?tradeApply.getHandledCompany().getCompVisible()!=null&&tradeApply.getHandledCompany().getCompVisible().getVisibleContent().contains(\"contactPhone\")?tradeApply.getHandledCompany().getDetails().getContactPhone():null:tradeApply.getContactPhone())")
    @Mapping(target = "areaCode", expression = "java(dcCompId.equals(tradeApply.getCreatedCompBy())?tradeApply.getHandledCompany().getCompVisible()!=null&&tradeApply.getHandledCompany().getCompVisible().getVisibleContent().contains(\"address\")?tradeApply.getHandledCompany().getDetails().getAreaCode():null:tradeApply.getAreaCode())")
    @Mapping(target = "areaName", expression = "java(dcCompId.equals(tradeApply.getCreatedCompBy())?tradeApply.getHandledCompany().getCompVisible()!=null&&tradeApply.getHandledCompany().getCompVisible().getVisibleContent().contains(\"address\")?tradeApply.getHandledCompany().getDetails().getAreaName():null:tradeApply.getAreaName())")
    @Mapping(target = "address", expression = "java(dcCompId.equals(tradeApply.getCreatedCompBy())?tradeApply.getHandledCompany().getCompVisible()!=null&&tradeApply.getHandledCompany().getCompVisible().getVisibleContent().contains(\"address\")?tradeApply.getHandledCompany().getDetails().getAddress():null:tradeApply.getAddress())")
    @Mapping(target = "introduction", expression = "java(dcCompId.equals(tradeApply.getCreatedCompBy())?tradeApply.getHandledCompany().getCompVisible()!=null&&tradeApply.getHandledCompany().getCompVisible().getVisibleContent().contains(\"introduction\")?tradeApply.getHandledCompany().getIntroduction():null:tradeApply.getIntroduction())")
    TCompTradeApply toEnrolledCompanyDetail(CompTradeApply tradeApply, String dcCompId);

    /**
     * 拒绝名单中的公司详情
     *
     * @param compTradeApply 申请记录
     * @return 拒绝名单中的公司详情
     */
    @Mapping(target = "companyName", source = "companyName")
    @Mapping(target = "companyShortName", source = "companyShortName")
    @Mapping(target = "usci", source = "usci")
    @Mapping(target = "contactPhone", source = "contactPhone")
    @Mapping(target = "contactName", source = "contactName")
    @Mapping(target = "areaCode", source = "areaCode")
    @Mapping(target = "areaName", source = "areaName")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "introduction", source = "introduction")
    @Mapping(target = "isSupplier", constant = "false")
    @Mapping(target = "isCustomer", constant = "false")
    VEnrolledCompanyResponse.VCompany toTCompTradeApplyDetail(TCompTradeApply compTradeApply);


    VTradeApplyDetailResponse.VApply toApplyDetail(TCompTradeApply compTradeApply);
}
