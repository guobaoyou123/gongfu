package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TProduct;
import com.linzhi.gongfu.dto.TProductStockSum;
import com.linzhi.gongfu.entity.Product;
import com.linzhi.gongfu.vo.VProductDetailResponse;
import com.linzhi.gongfu.vo.trade.VProductListResponse;
import com.linzhi.gongfu.vo.trade.VProductPageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    TProduct toProduct(Product product);

    @Mapping(target = "brandName", source = "brand")
    VProductPageResponse.VProduct toPreloadProduct(TProduct product);

    /**
     * 获取产品详情
     *
     * @param product 产品信息
     * @return 产品信息预获取响应
     */

    @Mapping(target = "brandName", source = "brand")
    VProductDetailResponse.VProduct tProductDetail(TProduct product);

    @Mapping(target = "brandName", source = "brand")
    VProductListResponse.VProduct tProductList(TProduct product);


    @Mapping(target = "brandName", source = "brand")
    VProductListResponse.VProduct toProductByCode(TProduct product);

}
