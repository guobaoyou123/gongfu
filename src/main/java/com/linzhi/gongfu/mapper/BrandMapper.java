package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TBrand;
import com.linzhi.gongfu.dto.TCompanyIncludeBrand;
import com.linzhi.gongfu.entity.CompTrad;
import com.linzhi.gongfu.entity.DcBrand;
import com.linzhi.gongfu.entity.Brand;
import com.linzhi.gongfu.entity.ViewBrand;
import com.linzhi.gongfu.vo.VBrandResponse;
import com.linzhi.gongfu.vo.VDcBrandResponse;
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
    Set<TBrand>  toDTOs(Set<DcBrand> brands);

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
    VBrandResponse.VBrand toBrandPreload(TBrand brand);

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

    /*@Mapping(target = "code", source = "companys.code")
    @Mapping(target = "name", source = "companys.shortNameInCN")
    @Mapping(target = "selfSupportBrands", source = "selfSupportBrands")
    @Mapping(target = "authBrands", source = "authBrands")
    @Mapping(target = "manageBrands", source = "manageBrands")
    TBrand toDTOs(DcBrand brand);*/
}
