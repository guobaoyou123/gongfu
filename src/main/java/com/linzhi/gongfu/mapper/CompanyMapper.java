package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.dto.TCompanyList;
import com.linzhi.gongfu.dto.TEnrolledTradeCompanies;
import com.linzhi.gongfu.dto.TEnrolledTradeCompany;
import com.linzhi.gongfu.entity.CompTrade;
import com.linzhi.gongfu.entity.Company;
import com.linzhi.gongfu.entity.EnrolledCompany;
import com.linzhi.gongfu.entity.EnrolledTrade;
import com.linzhi.gongfu.vo.*;
import com.querydsl.core.Tuple;
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
    @Mapping(target = "visible", expression = "java(company.getVisible()==null?null:String.valueOf(company.getVisible().getState()))")
    TCompanyBaseInformation toBaseInformation(EnrolledCompany company);

    /**
     * 将获取到的外部供应商或者客户公司信息，转换成可供使用的外部供应商或者客户公司基础信息
     *
     * @param compTrade 已经获取的外部供应商或者客户公司全部信息
     * @return 外部供应商或者客户公司简要基础信息
     */
    @Mapping(target = "code", source = "compTrade.salerCompanys.code")
    @Mapping(target = "encode", source = "compTrade.salerCompanys.encode")
    @Mapping(target = "shortName", source = "compTrade.salerCompanys.shortNameInCN")
    @Mapping(target = "state", expression = "java(compTrade.getState().getState()+\"\")")
    TCompanyBaseInformation toForeignCompany(CompTrade compTrade);

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
    @Mapping(target = "visible", expression = "java(company.getVisible()==null?\"0\":String.valueOf(company.getVisible().getState()))")
    @Mapping(target = "contactName", expression = "java(company.getCompVisible()!=null&&company.getCompVisible().getVisibleContent().contains(\"contactPhone\")?company.getDetails().getContactName():null)")
    @Mapping(target = "contactPhone", expression = "java(company.getCompVisible()!=null&&company.getCompVisible().getVisibleContent().contains(\"contactPhone\")?company.getDetails().getContactPhone():null)")
    @Mapping(target = "areaCode", expression = "java(company.getCompVisible()!=null&&company.getCompVisible().getVisibleContent().contains(\"address\")?company.getDetails().getAreaCode():null)")
    @Mapping(target = "areaName", expression = "java(company.getCompVisible()!=null&&company.getCompVisible().getVisibleContent().contains(\"address\")?company.getDetails().getAreaName():null)")
    @Mapping(target = "address", expression = "java(company.getCompVisible()!=null&&company.getCompVisible().getVisibleContent().contains(\"address\")?company.getDetails().getAddress():null)")
    @Mapping(target = "content", source = "compVisible.visibleContent")
    @Mapping(target = "introduction", expression = "java(company.getCompVisible()!=null&&company.getCompVisible().getVisibleContent().contains(\"introduction\")?company.getIntroduction():null)")
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
    @Mapping(target = "state", expression = "java(String.valueOf(company.getState().getState()))")
    TCompanyBaseInformation toBaseInformation(Company company);

    /**
     * 将获取到的本公司的入格信息以及设置的基本信息，转换成可供使用的公司基础信息
     * 用于查看本公司基本信息
     *
     * @param company 已经入格的公司全部信息
     * @return 可供使用的公司基础信息
     */
    @Mapping(target = "code", source = "id")
    @Mapping(target = "name", source = "nameInCN")
    @Mapping(target = "shortName", source = "details.shortNameInCN")
    @Mapping(target = "subdomain", source = "subdomainName")
    @Mapping(target = "visible", expression = "java(company.getVisible()==null?\"0\":String.valueOf(company.getVisible().getState()))")
    @Mapping(target = "contactName", source = "details.contactName")
    @Mapping(target = "contactPhone", source = "details.contactPhone")
    @Mapping(target = "areaCode", source = "details.areaCode")
    @Mapping(target = "areaName", source = "details.areaName")
    @Mapping(target = "address", source = "details.address")
    @Mapping(target = "content", source = "compVisible.visibleContent")
    TCompanyBaseInformation toCompDetail(EnrolledCompany company);

    /**
     * 明确可以成功获取到供应商公司基础信息时，向预获取响应转换
     *
     * @param info 公司基础信息
     * @return 供应商公司基本信息预获取响应
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "shortName")
    @Mapping(target = "sort", constant = "1")
    VSuppliersResponse.VSupplier toPreloadSuppliers(TCompanyBaseInformation info);

    /**
     * 将获取到的外供应商公司信息，转换成可供使用的公司基础信息
     *
     * @param company 外供应商的公司全部信息
     * @return 外供应商公司简要基础信息
     */
    @Mapping(target = "companyShortName", source = "shortName")
    @Mapping(target = "encode", source = "enCode")
    @Mapping(target = "state",source = "state")
    @Mapping(target = "brands",source = "brands")
    VForeignSuppliersResponse.VForeignSupplier toForeignSupplier(TCompanyList company);

    /**
     * 将获取到的外供应商公司信息，转换成可供使用的公司基础信息
     *
     * @param company 外供应商的公司全部信息
     * @return 外供应商公司简要基础信息
     */
    @Mapping(target = "companyName", source = "name")
    @Mapping(target = "companyShortName", source = "shortName")
    @Mapping(target = "usci", source = "USCI")
    VForeignSupplierResponse.VSupplier toSupplierDetail(TCompanyBaseInformation company);

    /**
     * 将获取到的本公司信息，转换成可供使用的公司基础信息
     *
     * @param company 本公司全部信息
     * @return 本公司简要基础信息
     */
    @Mapping(target = "companyName", source = "name")
    @Mapping(target = "companyShortName", source = "shortName")
    @Mapping(target = "usci", source = "USCI")
    @Mapping(target = "visible", expression = "java(company.getVisible().equals(\"1\")?true:false)")
    VCompanyResponse.VCompany toCompanyDetail(TCompanyBaseInformation company);

    /**
     * 明确可以成功获取到入格单位公司基础信息时，向预获取响应转换
     *
     * @param company 入格单位公司基础信息
     * @return 入格单位公司基本信息预获取响应
     */
    @Mapping(target = "companyName", source = "name")
    VEnrolledCompanyPageResponse.VCompany toEnrolledCompany(TCompanyBaseInformation company);

    /**
     * 明确可以成功获取到入格单位公司基础信息时，向预获取响应转换
     *
     * @param company 入格单位公司基础信息
     * @return 入格单位公司基本信息预获取响应
     */
    @Mapping(target = "companyName", source = "name")
    @Mapping(target = "companyShortName", source = "shortName")
    @Mapping(target = "usci", source = "USCI")
    @Mapping(target = "contactName", source = "contactName")
    @Mapping(target = "contactPhone", source = "contactPhone")
    @Mapping(target = "areaCode", source = "areaCode")
    @Mapping(target = "areaName", source = "areaName")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "isSupplier", constant = "false")
    @Mapping(target = "isCustomer", constant = "false")
    @Mapping(target = "state", constant = "0")
    VEnrolledCompanyResponse.VCompany toEnrolledCompanyDetail(TCompanyBaseInformation company);

    /**
     * 明确可以成功获取到入格供应商或者客户公司基础信息时，向预获取响应转换
     *
     * @param tCompanyList 入格供应商或者客户单位公司基础信息
     * @return 入格供应商或者客户公司基本信息预获取响应
     */
    @Mapping(target = "companyShortName", source = "shortName")
    @Mapping(target = "brands",source = "brands")
    @Mapping(target = "operators",source = "operators")
    VEnrolledTradeCompaniesResponse.VEnrolledTradeCompany toVEnrolledTradeCompanies(TCompanyList tCompanyList);


    /**
     * 将获取到的入格供应商详细信息，转换为可供使用的对我可见的入格供应商详细信息
     *
     * @param enrolledSupplier 入格供应商详细信息
     * @return 对我可见的入格供应商详细信息
     */
    @Mapping(target = "code", source = "company.details.code")
    @Mapping(target = "companyName", source = "company.nameInCN")
    @Mapping(target = "companyShortName", source = "company.details.shortNameInCN")
    @Mapping(target = "usci", source = "company.USCI")
    @Mapping(target = "taxMode", expression = "java(String.valueOf(enrolledSupplier.getTaxModel().getTaxMode()))")
    @Mapping(target = "brands", source = "brands")
    @Mapping(target = "contactName", expression = "java(enrolledSupplier.getCompany().getCompVisible()!=null&&enrolledSupplier.getCompany().getCompVisible().getVisibleContent()!=null&&enrolledSupplier.getCompany().getCompVisible().getVisibleContent().contains(\"contactPhone\")?enrolledSupplier.getCompany().getDetails().getContactName():null)")
    @Mapping(target = "contactPhone", expression = "java(enrolledSupplier.getCompany().getCompVisible()!=null&&enrolledSupplier.getCompany().getCompVisible().getVisibleContent()!=null&&enrolledSupplier.getCompany().getCompVisible().getVisibleContent().contains(\"contactPhone\")?enrolledSupplier.getCompany().getDetails().getContactPhone():null)")
    @Mapping(target = "areaCode", expression = "java(enrolledSupplier.getCompany().getCompVisible()!=null&&enrolledSupplier.getCompany().getCompVisible().getVisibleContent()!=null&&enrolledSupplier.getCompany().getCompVisible().getVisibleContent().contains(\"address\")?enrolledSupplier.getCompany().getDetails().getAreaCode():null)")
    @Mapping(target = "areaName", expression = "java(enrolledSupplier.getCompany().getCompVisible()!=null&&enrolledSupplier.getCompany().getCompVisible().getVisibleContent()!=null&&enrolledSupplier.getCompany().getCompVisible().getVisibleContent().contains(\"address\")?enrolledSupplier.getCompany().getDetails().getAreaName():null)")
    @Mapping(target = "address", expression = "java(enrolledSupplier.getCompany().getCompVisible()!=null&&enrolledSupplier.getCompany().getCompVisible().getVisibleContent()!=null&&enrolledSupplier.getCompany().getCompVisible().getVisibleContent().contains(\"address\")?enrolledSupplier.getCompany().getDetails().getAddress():null)")
    @Mapping(target = "introduction", expression = "java(enrolledSupplier.getCompany().getCompVisible()!=null&&enrolledSupplier.getCompany().getCompVisible().getVisibleContent()!=null&&enrolledSupplier.getCompany().getCompVisible().getVisibleContent().contains(\"introduction\")?enrolledSupplier.getCompany().getIntroduction():null)")
    TEnrolledTradeCompany toTEnrolledSupplierDetail(EnrolledTrade enrolledSupplier);

    /**
     * 明确可以成功获取入格供应商或者客户详细信息时，向预响应转换
     *
     * @param tEnrolledTradeCompany 入格供应商者客户公司详细信息
     * @return 入格供应商者客户公司详细信息预获取响应
     */
    VEnrolledTradeCompanyResponse.VCompany toTEnrolledTradeCompany(TEnrolledTradeCompany tEnrolledTradeCompany);

    /**
     * 将获取到的入格客户详细信息，转换为可供使用的对我可见的入格客户详细信息
     *
     * @param enrolledCustomer 入格客户详细信息
     * @return 对我可见的入格客户详细信息
     */
    @Mapping(target = "code", source = "buyerCompany.details.code")
    @Mapping(target = "companyName", source = "buyerCompany.nameInCN")
    @Mapping(target = "companyShortName", source = "buyerCompany.details.shortNameInCN")
    @Mapping(target = "usci", source = "buyerCompany.USCI")
    @Mapping(target = "taxMode", expression = "java(String.valueOf(enrolledCustomer.getTaxModel().getTaxMode()))")
    @Mapping(target = "brands", source = "brands")
    @Mapping(target = "contactName", expression = "java(enrolledCustomer.getBuyerCompany().getCompVisible()!=null&&enrolledCustomer.getBuyerCompany().getCompVisible().getVisibleContent()!=null&&enrolledCustomer.getBuyerCompany().getCompVisible().getVisibleContent().contains(\"contactPhone\")?enrolledCustomer.getBuyerCompany().getDetails().getContactName():null)")
    @Mapping(target = "contactPhone", expression = "java(enrolledCustomer.getBuyerCompany().getCompVisible()!=null&&enrolledCustomer.getBuyerCompany().getCompVisible().getVisibleContent()!=null&&enrolledCustomer.getBuyerCompany().getCompVisible().getVisibleContent().contains(\"contactPhone\")?enrolledCustomer.getBuyerCompany().getDetails().getContactPhone():null)")
    @Mapping(target = "areaCode", expression = "java(enrolledCustomer.getBuyerCompany().getCompVisible()!=null&&enrolledCustomer.getBuyerCompany().getCompVisible().getVisibleContent()!=null&&enrolledCustomer.getBuyerCompany().getCompVisible().getVisibleContent().contains(\"address\")?enrolledCustomer.getBuyerCompany().getDetails().getAreaCode():null)")
    @Mapping(target = "areaName", expression = "java(enrolledCustomer.getBuyerCompany().getCompVisible()!=null&&enrolledCustomer.getBuyerCompany().getCompVisible().getVisibleContent()!=null&&enrolledCustomer.getBuyerCompany().getCompVisible().getVisibleContent().contains(\"address\")?enrolledCustomer.getBuyerCompany().getDetails().getAreaName():null)")
    @Mapping(target = "address", expression = "java(enrolledCustomer.getBuyerCompany().getCompVisible()!=null&&enrolledCustomer.getBuyerCompany().getCompVisible().getVisibleContent()!=null&&enrolledCustomer.getBuyerCompany().getCompVisible().getVisibleContent().contains(\"address\")?enrolledCustomer.getBuyerCompany().getDetails().getAddress():null)")
    @Mapping(target = "introduction", expression = "java(enrolledCustomer.getBuyerCompany().getCompVisible()!=null&&enrolledCustomer.getBuyerCompany().getCompVisible().getVisibleContent()!=null&&enrolledCustomer.getBuyerCompany().getCompVisible().getVisibleContent().contains(\"introduction\")?enrolledCustomer.getBuyerCompany().getIntroduction():null)")
    TEnrolledTradeCompany toTEnrolledCustomerDetail(EnrolledTrade enrolledCustomer);


    /**
     * 将获取到的外客户公司信息，转换成可供使用的公司基础信息
     *
     * @param company 外客户的公司全部信息
     * @return 外客户公司简要基础信息
     */
    @Mapping(target = "companyShortName", source = "shortName")
    @Mapping(target = "encode", source = "enCode")
    @Mapping(target = "brands",source = "brands")
    @Mapping(target = "operators",source = "operators")
    VForeignCustomerPageResponse.VForeignCustomer toVForeignCustomer(TCompanyList company);
    /**
     * 将获取到的外供应商公司信息，转换成可供使用的公司基础信息
     *
     * @param company 外供应商的公司全部信息
     * @return 外供应商公司简要基础信息
     */
    @Mapping(target = "companyName", source = "name")
    @Mapping(target = "companyShortName", source = "shortName")
    @Mapping(target = "usci", source = "USCI")
    VForeignCustomerResponse.VCustomer toVCustomer(TCompanyBaseInformation company);

    /**
     * 明确可以成功获取到客户公司基础信息时，向预获取响应转换
     *
     * @param info 公司基础信息
     * @return 客户公司基本信息预获取响应
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "shortName")
    @Mapping(target = "sort", constant = "1")
    VCustomersResponse.VCustomer toPreloadCustomer(TCompanyBaseInformation info);
}
