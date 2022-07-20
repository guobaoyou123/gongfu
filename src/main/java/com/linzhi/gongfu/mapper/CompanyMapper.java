package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.entity.Company;
import com.linzhi.gongfu.entity.EnrolledCompany;
import com.linzhi.gongfu.vo.*;

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
    @Mapping(target = "visible",expression = "java(company.getVisible()==null?null:String.valueOf(company.getVisible().getState()))")
    TCompanyBaseInformation toBaseInformation(EnrolledCompany company);

    /**
     * 将获取到的入格公司可见信息，转换成可供使用的可见公司基础信息
     *
     * @param company 已经入格的公司可见信息
     * @return 公司简要可见基础信息
     */
    @Mapping(target = "code", source = "id")
    @Mapping(target = "name", source = "nameInCN")
    @Mapping(target = "shortName", source = "details.shortNameInCN")
    @Mapping(target = "subdomain", source = "subdomainName")
    @Mapping(target = "visible",expression = "java(company.getVisible()==null?\"0\":String.valueOf(company.getVisible().getState()))")
    @Mapping(target = "contactName",expression = "java(company.getCompVisible()!=null&&company.getCompVisible().getVisibleContent().contains(\"contactPhone\")?company.getDetails().getContactName():null)")
    @Mapping(target = "contactPhone",expression = "java(company.getCompVisible()!=null&&company.getCompVisible().getVisibleContent().contains(\"contactPhone\")?company.getDetails().getContactPhone():null)")
    @Mapping(target = "areaCode",expression = "java(company.getCompVisible()!=null&&company.getCompVisible().getVisibleContent().contains(\"address\")?company.getDetails().getAreaCode():null)")
    @Mapping(target = "areaName",expression = "java(company.getCompVisible()!=null&&company.getCompVisible().getVisibleContent().contains(\"address\")?company.getDetails().getAreaName():null)")
    @Mapping(target = "address",expression = "java(company.getCompVisible()!=null&&company.getCompVisible().getVisibleContent().contains(\"address\")?company.getDetails().getAddress():null)")
    @Mapping(target = "content",source = "compVisible.visibleContent")
    @Mapping(target = "introduction",expression = "java(company.getCompVisible()!=null&&company.getCompVisible().getVisibleContent().contains(\"introduction\")?company.getIntroduction():null)")

    TCompanyBaseInformation toEnrolledCompanyDetail(EnrolledCompany company);
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
     * 将获取到的本公司的入格信息以及设置的基本信息，转换成可供使用的公司基础信息
     * 用于查看本公司基本信息
     * @param company 已经入格的公司全部信息
     * @return 可供使用的公司基础信息
     */
    @Mapping(target = "code", source = "id")
    @Mapping(target = "name", source = "nameInCN")
    @Mapping(target = "shortName", source = "details.shortNameInCN")
    @Mapping(target = "subdomain", source = "subdomainName")
    @Mapping(target = "visible",expression = "java(company.getVisible()==null?\"0\":String.valueOf(company.getVisible().getState()))")
    @Mapping(target = "contactName",source = "details.contactName")
    @Mapping(target = "contactPhone",source = "details.contactPhone")
    @Mapping(target = "areaCode",source = "details.areaCode")
    @Mapping(target = "areaName",source = "details.areaName")
    @Mapping(target = "address",source = "details.address")
    @Mapping(target = "content",source = "compVisible.visibleContent")
    TCompanyBaseInformation toCompDetail(EnrolledCompany company);

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

    /**
     * 将获取到的本公司信息，转换成可供使用的公司基础信息
     *
     * @param company 本公司全部信息
     * @return 本公司简要基础信息
     */
    @Mapping(target = "companyName", source = "name")
    @Mapping(target = "companyShortName", source = "shortName")
    @Mapping(target = "usci", source = "USCI")
    @Mapping(target = "visible",expression = "java(company.getVisible().equals(\"1\")?true:false)")
    VCompanyDetailResponse.VCompany toCompanyDetail(TCompanyBaseInformation company);


    @Mapping(target = "companyName", source = "name")
    @Mapping(target = "companyShortName", source = "shortName")
    VEnrolledCompanyPageResponse.VCompany toEnrolledCompany(TCompanyBaseInformation company);

    @Mapping(target = "companyName", source = "name")
    @Mapping(target = "companyShortName", source = "shortName")
    @Mapping(target = "usci", source = "USCI")
    @Mapping(target = "contactName", source = "contactName")
    @Mapping(target = "contactPhone", source = "contactPhone")
    @Mapping(target = "areaCode", source = "areaCode")
    @Mapping(target = "areaName", source = "areaName")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "isSupplier",constant = "false")
    @Mapping(target = "isCustomer",constant = "false")
    @Mapping(target = "state",constant = "0")
    VEnrolledCompanyDetailResponse.VCompany toEnrolledCompanyDetail(TCompanyBaseInformation company);
}
