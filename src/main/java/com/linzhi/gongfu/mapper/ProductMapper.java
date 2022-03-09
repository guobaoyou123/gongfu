package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TProduct;
import com.linzhi.gongfu.entity.Product;
import com.linzhi.gongfu.vo.VProductListResponse;
import com.linzhi.gongfu.vo.VProductPageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    TProduct toProduct(Product product);
    @Mapping(target = "brandName",source = "brand")
    VProductPageResponse.VProduct toProldeProduct(TProduct product);

    @Mapping(target = "brandName",source = "brand")
    VProductListResponse.VProduct toProductByCode(TProduct product);

}
