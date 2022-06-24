package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.entity.Company;
import com.linzhi.gongfu.entity.EnrolledCompany;
import com.linzhi.gongfu.vo.VForeignSuppliersResponse;
import com.linzhi.gongfu.vo.VPreloadCompanyInfoResponse;

import com.linzhi.gongfu.vo.VSupplierDetailResponse;
import com.linzhi.gongfu.vo.VSuppliersResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换公司相关信息
 *
 * @author xutao
 * @create_at 2022-01-19
 */
@Mapper(componentModel = "spring")
public interface CompanyMapper {
    /**
     * 明确可以成功获取到公司基础信息时，向预获取响应转换
     *
     * @param info 公司基础信息
     * @return 公司基本信息预获取响应
     */
    @Mapping(target = "code", constant = "200")
    @Mapping(target = "message", constant = "成功找到对应的公司信息。")
    @Mapping(target = "companyName", source = "name")
    @Mapping(target = "companyShortName", source = "shortName")
    @Mapping(target = "companyScenes", source = "scenes")
    VPreloadCompanyInfoResponse toPreload(TCompanyBaseInformation info);

    /**
     * 将获取到的入格公司信息，转换成可供使用的公司基础信息
     *
     * @param company 已经入格的公司全部信息
     * @return 公司简要基础信息
     */
    @Mapping(target = "code", source = "id")
    @Mapping(target = "name", source = "nameInCN")
    @Mapping(target = "shortName", source = "details.shortNameInCN")
    @Mapping(target = "subdomain", source = "subdomainName")
    TCompanyBaseInformation toBaseInformation(EnrolledCompany company);

    /**
     * 将获取到的公司信息，转换成可供使用的公司基础信息
     *
     * @param company 已经入格的公司全部信息
     * @return 公司简要基础信息
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "nameInCN")
    @Mapping(target = "shortName", source = "shortNameInCN")
    @Mapping(target = "state",expression = "java(String.valueOf(company.getState().getState()))")
    TCompanyBaseInformation toBaseInformation(Company company);

    /**
     * 明确可以成功获取到公司基础信息时，向预获取响应转换
     *
     * @param info 公司基础信息
     * @return 公司基本信息预获取响应
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "shortName")
    @Mapping(target = "sort",constant = "1")
    VSuppliersResponse.VSupplier toPreloadSuppliers(TCompanyBaseInformation info);

    /**
     * 将获取到的外供应商公司信息，转换成可供使用的公司基础信息
     *
     * @param company 外供应商的公司全部信息
     * @return 外供应商公司简要基础信息
     */
    @Mapping(target = "companyName", source = "name")
    @Mapping(target = "companyShortName", source = "shortName")
    @Mapping(target = "usci", source = "USCI")
    VForeignSuppliersResponse.VForeignSupplier toForeignSupplier(TCompanyBaseInformation company);

    /**
     * 将获取到的外供应商公司信息，转换成可供使用的公司基础信息
     *
     * @param company 外供应商的公司全部信息
     * @return 外供应商公司简要基础信息
     */
    @Mapping(target = "companyName", source = "name")
    @Mapping(target = "companyShortName", source = "shortName")
    @Mapping(target = "usci", source = "USCI")
    VSupplierDetailResponse.VSupplier toSupplierDetail(TCompanyBaseInformation company);

}
