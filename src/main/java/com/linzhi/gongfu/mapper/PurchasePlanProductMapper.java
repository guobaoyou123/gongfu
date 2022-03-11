package com.linzhi.gongfu.mapper;


import com.linzhi.gongfu.dto.TPurchasePlanProduct;
import com.linzhi.gongfu.entity.PurchasePlanProduct;
import com.linzhi.gongfu.vo.VPurchasePlanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",uses = { PurchasePlanProductSupplierMapper.class })
public interface PurchasePlanProductMapper {
    List<TPurchasePlanProduct> toDTOs(List<PurchasePlanProduct> product);
    @Mapping(target = "id",source = "purchasePlanProductId.productId")
    @Mapping(target = "code",source = "productCode")
    @Mapping(target = "brandCode",source = "brandCode")
    @Mapping(target = "deliverNum",source = "deliverNum")
    @Mapping(target = "tranNum",source = "tranNum")
    @Mapping(target = "demand",source = "demand")
    @Mapping(target = "safetyStock",source = "safetyStock")
    @Mapping(target = "beforeSalesPrice",source = "beforeSalesPrice")
    @Mapping(target = "inquiryNum",source = "inquiryNum")
    @Mapping(target = "brandName",source = "brand")
    @Mapping(target = "describe",source = "describe")
    @Mapping(target = "chargeUnit",source = "chargeUnit")
    @Mapping(target = "facePrice",source = "facePrice")
    @Mapping(target = "suppliers",source = "salers")
    @Mapping(target = "createdAt",expression = "java(com.linzhi.gongfu.util.DateConverter.getDateTime(purchasePlan.getCreatedAt()))")
    TPurchasePlanProduct toDTO(PurchasePlanProduct purchasePlan);

    List<VPurchasePlanResponse.VProduct> toProducts(List<TPurchasePlanProduct> tPurchasePlanProduct);
    @Mapping(target = "id",source = "id")
    @Mapping(target = "code",source = "code")
    @Mapping(target = "brandCode",source = "brandCode")
    @Mapping(target = "deliverNum",source = "deliverNum")
    @Mapping(target = "tranNum",source = "tranNum")
    @Mapping(target = "demand",source = "demand")
    @Mapping(target = "safetyStock",source = "safetyStock")
    @Mapping(target = "beforeSalesPrice",source = "beforeSalesPrice")
    @Mapping(target = "inquiryNum",source = "inquiryNum")
    @Mapping(target = "brandName",source = "brandName")
    @Mapping(target = "describe",source = "describe")
    @Mapping(target = "chargeUnit",source = "chargeUnit")
    @Mapping(target = "facePrice",source = "facePrice")
    @Mapping(target = "suppliers",source = "suppliers")
    VPurchasePlanResponse.VProduct toProduct(TPurchasePlanProduct tPurchasePlanProduct);



}
