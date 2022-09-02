package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TImportProductTemp;
import com.linzhi.gongfu.entity.ImportProductTemp;
import com.linzhi.gongfu.vo.VImportProductTempResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换导入产品的相关信息
 *
 * @author zgh
 * @create_at 2022-04-14
 */
@Mapper(componentModel = "spring")
public interface ImportProductTempMapper {
    @Mapping(target = "itemNo", source = "importProductTempId.itemNo")
    @Mapping(target = "dcCompId", source = "importProductTempId.dcCompId")
    @Mapping(target = "operator", source = "importProductTempId.operator")
    @Mapping(target = "confirmedBrand", source = "brandCode")
    @Mapping(target = "confirmedBrandName", source = "brandName")
    TImportProductTemp toTImportProductTemp(ImportProductTemp importProductTemp);

    VImportProductTempResponse.VProduct toVProduct(TImportProductTemp importProductTemp);
}
