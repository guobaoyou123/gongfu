package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TPreferenceSupplier;
import com.linzhi.gongfu.entity.PreferenceSupplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 用于转换优选供应的相关信息
 *
 * @author zgh
 * @create_at 2022-02-07
 */
@Mapper(componentModel = "spring")
public interface PreferenceSupplierMapper {
    /**
     * 将获取优选供应商列表转换成可供使用的优选供应商列表
     * @param suppliers 优选供应商列表
     * @return 优选供应商列表
     */
    List<TPreferenceSupplier> toPreferenceSuppliers(List<PreferenceSupplier> suppliers);

    /**
     * 将获取优选供应商转换成可供使用的优选供应商信息
     * @param preferenceSupplier 优选供应商
     * @return 优选供应商信息
     */
    @Mapping(target = "code", source= "company.code")
    @Mapping(target = "encode", expression = "java(preferenceSupplier.getCompany().getEncode()==null?preferenceSupplier.getCompany().getCode():preferenceSupplier.getCompany().getEncode())")
    @Mapping(target = "name", source= "company.shortNameInCN")
    @Mapping(target = "sort", source= "sort")
    TPreferenceSupplier toPreferenceSupplier(PreferenceSupplier preferenceSupplier);
}
