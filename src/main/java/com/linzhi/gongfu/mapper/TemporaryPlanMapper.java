package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TTemporaryPlan;
import com.linzhi.gongfu.entity.TemporaryPlan;
import com.linzhi.gongfu.vo.VTemporaryPlanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TemporaryPlanMapper {
    @Mapping(target = "dcCompId",source = "temporaryPlanId.dcCompId")
    @Mapping(target = "createdBy",source = "temporaryPlanId.createdBy")
    @Mapping(target = "productId",source = "temporaryPlanId.productId")
    @Mapping(target = "createdAt",expression = "java(com.linzhi.gongfu.util.DateConverter.getDateTime(temporaryPlan.getCreatedAt()))")
    TTemporaryPlan toTemporaryPlan(TemporaryPlan temporaryPlan);
    @Mapping(target = "code",source = "productCode")
    @Mapping(target = "brandName",source = "brand")
    @Mapping(target = "id",source = "productId")
    VTemporaryPlanResponse.VProduct toPreloadTemporaryPlan(TTemporaryPlan temporaryPlan);

}
