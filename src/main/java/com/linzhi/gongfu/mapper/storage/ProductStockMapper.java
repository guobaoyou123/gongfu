package com.linzhi.gongfu.mapper.storage;

import com.linzhi.gongfu.dto.TProductStock;
import com.linzhi.gongfu.dto.TProductStockSum;
import com.linzhi.gongfu.entity.ProductStock;
import com.linzhi.gongfu.entity.ProductStockSum;
import com.linzhi.gongfu.vo.storage.VSafetyStockListResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductStockMapper {


    @Mapping(target = "id",source = "product.id")
    @Mapping(target = "code",source = "product.code")
    @Mapping(target = "brandCode",source = "product.brandCode")
    @Mapping(target = "brand",source = "product.brand")
    @Mapping(target = "describe",source = "product.describe")
    @Mapping(target = "chargeUnit",source = "product.chargeUnit")
    @Mapping(target = "safetyStock",source = "safetyStock.safetyStock")
    @Mapping(target = "singlePurchaseQuantity",source = "safetyStock.singlePurchaseQuantity")
    @Mapping(target = "productStocks",source = "productStocks")
    TProductStockSum toTProductStockSum(ProductStockSum productStockSum);

    List<TProductStock> toTProductStocks(List<ProductStock> productStocks);

    @Mapping(target = "code",source = "productStockId.warehouseCode")
    @Mapping(target = "name",source = "wareHouse.name")
    @Mapping(target = "physicalStock",source = "physicalStock")
    @Mapping(target = "deliverStock",expression = "java(productStock.getPhysicalStock().subtract(productStock.getNotOutStock()))")
    TProductStock toTProductStock(ProductStock productStock);

   @Mapping(target = "warehousese",source = "productStocks")
   @Mapping(target = "brandName",source = "brand")
    VSafetyStockListResponse.VSafetyStock toVSafetyStock(TProductStockSum tProductStock);

}
