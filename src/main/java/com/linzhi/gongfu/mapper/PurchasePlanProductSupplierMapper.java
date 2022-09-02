package com.linzhi.gongfu.mapper;


import com.linzhi.gongfu.dto.TPurchasePlanProductSupplier;
import com.linzhi.gongfu.entity.PurchasePlanProductSupplier;
import com.linzhi.gongfu.vo.VPurchasePlanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PurchasePlanProductSupplierMapper {
    List<TPurchasePlanProductSupplier> toDTOs(List<TPurchasePlanProductSupplier> salers);

    @Mapping(target = "code", source = "purchasePlanProductSupplierId.salerCode")
    @Mapping(target = "name", source = "salerName")
    @Mapping(target = "deliverNum", source = "deliverNum")
    @Mapping(target = "tranNum", source = "tranNum")
    @Mapping(target = "demand", source = "demand")
    TPurchasePlanProductSupplier toDTO(PurchasePlanProductSupplier purchasePlan);


    List<VPurchasePlanResponse.VSupplier> toSuppliers(List<TPurchasePlanProductSupplier> tPurchasePlanProductSuppliers);

    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "deliverNum", source = "deliverNum")
    @Mapping(target = "tranNum", source = "tranNum")
    @Mapping(target = "demand", source = "demand")
    VPurchasePlanResponse.VSupplier toSupplier(TPurchasePlanProductSupplier tPurchasePlanProductSupplier);


}
