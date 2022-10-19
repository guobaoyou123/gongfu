package com.linzhi.gongfu.mapper;


import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.dto.TCompanyIncludeBrand;
import com.linzhi.gongfu.dto.TCompanyList;
import com.linzhi.gongfu.entity.CompTrade;
import com.linzhi.gongfu.vo.VSuppliersPageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


/**
 * 用于查询本单位的供应商和客户
 *
 * @author xutao
 * @create_at 2022-01-19
 */
@Mapper(componentModel = "spring", uses = {BrandMapper.class, BrandMapper.class})
public interface CompTradeMapper {
    /**
     * 将获取到的我的供应商信息信息，转换成可供使用的我的供应商信息基础信息
     *
     * @param tCompanyIncludeBrand 我的供应商信息
     * @return 我的供应商以及品牌信息
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "brands", source = "selfSupportBrands")
    @Mapping(target = "sort", constant = "1")
    VSuppliersPageResponse.VSupplier toPreloadSuppliersIncludeBrandDTOs(TCompanyIncludeBrand tCompanyIncludeBrand);

    /**
     * 将获取到的入格公司信息，转换成可供使用的公司基础信息
     *
     * @param compTrad 我的供应商信息
     * @return 我的供应商以及品牌信息
     */
    @Mapping(target = "code", source = "salerCompanys.code")
    @Mapping(target = "name", source = "salerCompanys.shortNameInCN")
    @Mapping(target = "selfSupportBrands", source = "selfSupportBrands")
    @Mapping(target = "authBrands", source = "authBrands")
    @Mapping(target = "manageBrands", source = "manageBrands")
    @Mapping(target = "state", expression = "java(String.valueOf(compTrad.getSalerCompanys().getState().getState()))")
    TCompanyIncludeBrand toSuppliersIncludeBrand(CompTrade compTrad);

    /**
     * 将获取到的交易信息内容转换成可供使用的供应商信息
     * @param compTrade 交易信息
     * @return 供应商公司信息
     */
    @Mapping(target = "code",source = "salerCompanys.code")
    @Mapping(target = "enCode",source = "salerCompanys.encode")
    @Mapping(target = "shortName",source = "salerCompanys.shortNameInCN")
    @Mapping(target = "state", expression = "java(String.valueOf(compTrade.getSalerCompanys().getState().getState()))")
    @Mapping(target = "brands", source = "manageBrands")
    TCompanyList toSupplier(CompTrade compTrade);

    /**
     * 将获取到的交易信息内容转换成可供使用的客户信息
     * @param compTrade 交易信息
     * @return 客户公司信息
     */
    @Mapping(target = "code",source = "buyerCompanys.code")
    @Mapping(target = "enCode",source = "buyerCompanys.encode")
    @Mapping(target = "shortName",source = "buyerCompanys.shortNameInCN")
    @Mapping(target = "state", expression = "java(String.valueOf(compTrade.getSalerCompanys().getState().getState()))")
    @Mapping(target = "brands", source = "manageBrands")
    TCompanyList toCustomer(CompTrade compTrade);
}
