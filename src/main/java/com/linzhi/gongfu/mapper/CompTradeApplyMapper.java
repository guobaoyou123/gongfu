package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TCompTradeApply;
import com.linzhi.gongfu.entity.CompTradeApply;
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

    @Mapping(target = "code",source = "code")
    @Mapping(target = "companyCode",source = "createdCompBy")
    @Mapping(target = "companyName",source = "createdCompany.nameInCN")
    @Mapping(target = "companyShortName",source = "createdCompany.details.shortNameInCN")
    @Mapping(target = "type",source = "type")
    @Mapping(target = "state",expression = "java(String.valueOf(compTradeApply.getState().getState()))")
    @Mapping(target = "createdAt",expression ="java(com.linzhi.gongfu.util.DateConverter.dateFormat(compTradeApply.getCreatedAt()))" )
    TCompTradeApply toTComTradeApply(CompTradeApply compTradeApply);

    @Mapping(target = "code",source = "code")
    @Mapping(target = "companyCode",source = "createdCompBy")
    @Mapping(target = "companyName",source = "createdCompany.nameInCN")
    @Mapping(target = "companyShortName",source = "createdCompany.details.shortNameInCN")
    @Mapping(target = "handledCompanyCode",source = "handledCompBy")
    @Mapping(target = "handledCompanyName",source = "handledCompany.nameInCN")
    @Mapping(target = "handledCompanyShortName",source = "handledCompany.details.shortNameInCN")
    @Mapping(target = "type",source = "type")
    @Mapping(target = "state",expression = "java(String.valueOf(compTradeApply.getState().getState()))")
    @Mapping(target = "createdAt",expression ="java(com.linzhi.gongfu.util.DateConverter.dateFormat(compTradeApply.getCreatedAt()))" )
    TCompTradeApply toTComTradeApplyHistory(CompTradeApply compTradeApply);

    VTradeApplyPageResponse.VTradeApply toVTradeApply(TCompTradeApply tradeApply);
    @Mapping(target = "companyCode",expression = "java(tradeApply.getDcCompId().equals(tradeApply.getCompanyCode())?tradeApply.getHandledCompanyCode():tradeApply.getCompanyCode())")
    @Mapping(target = "companyName",expression = "java(tradeApply.getDcCompId().equals(tradeApply.getCompanyCode())?tradeApply.getHandledCompanyName():tradeApply.getCompanyName())")
    @Mapping(target = "companyShortName",expression = "java(tradeApply.getDcCompId().equals(tradeApply.getCompanyCode())?tradeApply.getHandledCompanyShortName():tradeApply.getCompanyShortName())")
    @Mapping(target = "type",expression = "java(tradeApply.getDcCompId().equals(tradeApply.getCompanyCode())?\"2\":\"1\")")
    VTradeApplyHistoryResponse.VApply toVApplyHistory(TCompTradeApply tradeApply);
}
