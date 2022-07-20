package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TCompTradeApply;
import com.linzhi.gongfu.entity.CompTradeApply;
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
    TCompTradeApply toTComTradeApply(CompTradeApply compTradeApply);


    VTradeApplyPageResponse.VTradeApply toVTradeApply(TCompTradeApply tradeApply);
}
