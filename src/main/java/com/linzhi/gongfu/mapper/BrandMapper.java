package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TBrand;
import com.linzhi.gongfu.entity.Brand;
import com.linzhi.gongfu.entity.CompAllowedBrand;
import com.linzhi.gongfu.entity.DcBrand;
import com.linzhi.gongfu.entity.ViewBrand;
import com.linzhi.gongfu.vo.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

/**
 * 用于转换品牌相关信息
 *
 * @author zgh
 * @create_at 2022-02-07
 */
@Mapper(componentModel = "spring")
public interface BrandMapper {


    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "chiShort")
    @Mapping(target = "sort", source = "sort")
    Set<TBrand> toDTOs(Set<DcBrand> brands);

    /**
     * 将获取到的品牌信息，转换成可供使用的品牌信息
     *
     * @param brand 品牌信息
     * @return 品牌简要基础信息
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "chiShort")
    @Mapping(target = "sort", source = "sort")
    @Mapping(target = "haveOwned", constant = "false")
    @Mapping(target = "owned", constant = "false")
    @Mapping(target = "vending", constant = "false")
    @Mapping(target = "compBrandOwner", source = "compBrandOwner")
    TBrand toBrand(Brand brand);

    /**
     * 将获取到的品牌信息，转换成可供使用的品牌信息
     *
     * @param brand 品牌信息
     * @return 品牌简要基础信息
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "chiShort")
    @Mapping(target = "sort", source = "sort")
    @Mapping(target = "haveOwned", constant = "false")
    @Mapping(target = "owned", constant = "false")
    @Mapping(target = "vending", constant = "false")
    TBrand toBrand(DcBrand brand);

    /**
     * 将获取到的品牌信息，转换成可供使用的品牌信息
     *
     * @param brand 品牌信息
     * @return 品牌简要基础信息
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "sort", source = "sort")
    @Mapping(target = "haveOwned", source = "haveOwned")
    @Mapping(target = "owned", source = "owned")
    @Mapping(target = "vending", source = "vending")
    VBrandPageResponse.VBrand toBrandPreload(TBrand brand);

    /**
     * 将获取到的品牌信息，转换成可供使用的品牌信息
     *
     * @param brand 品牌信息
     * @return 品牌简要基础信息
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "sort", source = "sort")
    VDcBrandResponse.VBrand toProductBrandPreload(TBrand brand);

    TBrand toViewBrand(ViewBrand viewBrand);

    /**
     * 将获取到的外供应商公司经营品牌转化成可供使用的外供应商公司经营品牌详细信息
     *
     * @param brand 外供应商公司经营品牌
     * @return 可供使用的外供应商公司经营品牌
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    VForeignSupplierResponse.VBrand toSupplierBrandPreload(TBrand brand);

    /**
     * 将获取到的公司经营品牌转化成可供使用的公司经营品牌详细信息
     *
     * @param allowedBrand 公司经营品牌
     * @return 可供使用的公司经营品牌
     */
    @Mapping(target = "code", source = "dcBrand.code")
    @Mapping(target = "name", source = "dcBrand.chiShort")
    @Mapping(target = "sort", source = "dcBrand.sort")
    TBrand toCompAllowedBrandDTO(CompAllowedBrand allowedBrand);

    /**
     * 将获取到的外客户公司经营品牌转化成可供使用的外客户公司经营品牌详细信息
     *
     * @param brand 外客户公司经营品牌
     * @return 可供使用的外客户公司经营品牌
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    VForeignCustomerResponse.VBrand toCustomerBrandPreload(TBrand brand);

}
