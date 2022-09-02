package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TPurchasePlan;
import com.linzhi.gongfu.entity.PurchasePlan;
import com.linzhi.gongfu.vo.VPurchasePlanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PurchasePlanProductMapper.class})
public interface PurchasePlanMapper {

    @Mapping(target = "planCode", source = "purchasePlanId.planCode")
    @Mapping(target = "salesCode", source = "salesCode")
    @Mapping(target = "products", source = "product")
    TPurchasePlan toDTO(PurchasePlan purchasePlan);

    @Mapping(target = "code", constant = "200")
    @Mapping(target = "message", constant = "获取采购计划成功")
    @Mapping(target = "planCode", source = "planCode")
    @Mapping(target = "salesCode", source = "salesCode")
    @Mapping(target = "products", source = "products")
    VPurchasePlanResponse toPruchasePlan(TPurchasePlan tPurchasePlan);


}
