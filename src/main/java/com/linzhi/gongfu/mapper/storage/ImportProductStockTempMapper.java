package com.linzhi.gongfu.mapper.storage;

import com.linzhi.gongfu.dto.TImportProductTemp;
import com.linzhi.gongfu.entity.ImportProductStockTemp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImportProductStockTempMapper {

    @Mapping(target = "itemNo", source = "importProductTempId.itemNo")
    @Mapping(target = "dcCompId", source = "importProductTempId.dcCompId")
    @Mapping(target = "operator", source = "importProductTempId.operator")
    @Mapping(target = "confirmedBrand", source = "brandCode")
    @Mapping(target = "confirmedBrandName", source = "brandName")
    TImportProductTemp toTImportProductTemp(ImportProductStockTemp importProductTemp);
}
