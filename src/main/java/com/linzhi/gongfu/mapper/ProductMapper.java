package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TProduct;
import com.linzhi.gongfu.entity.Product;
import com.linzhi.gongfu.vo.VProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.persistence.Table;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    TProduct toProduct(Product product);
    @Mapping(target = "brandName",source = "brand")
    VProductResponse.VProduct toProldeProduct(TProduct product);
}
