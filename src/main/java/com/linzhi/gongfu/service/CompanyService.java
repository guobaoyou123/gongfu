package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TBrand;
import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.dto.TCompanyIncludeBrand;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.CompanyRole;
import com.linzhi.gongfu.enumeration.TaxModel;
import com.linzhi.gongfu.enumeration.Trade;
import com.linzhi.gongfu.mapper.BrandMapper;
import com.linzhi.gongfu.mapper.CompTradeMapper;
import com.linzhi.gongfu.mapper.CompanyMapper;
import com.linzhi.gongfu.repository.CompTradBrandRepository;
import com.linzhi.gongfu.repository.CompTradeRepository;
import com.linzhi.gongfu.repository.CompanyRepository;
import com.linzhi.gongfu.repository.EnrolledCompanyRepository;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.VOutsideSupplierRequest;
import com.linzhi.gongfu.vo.VSupplierDetailResponse;
import com.linzhi.gongfu.vo.VSuppliersIncludeBrandsResponse;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 公司信息及处理业务服务
 *
 * @author xutao
 * @create_at 2022-01-19
 */
@RequiredArgsConstructor
@Service
public class CompanyService {
    private final EnrolledCompanyRepository enrolledCompanyRepository;
    private final CompanyMapper companyMapper;
    private final CompanyRepository companyRepository;
    private final CompTradeRepository compTradeRepository;
    private final CompTradeMapper compTradeMapper;
    private final JPAQueryFactory queryFactory;
    private final BrandMapper brandMapper;
    private  final  AddressService addressService;
    private final CompTradBrandRepository compTradBrandRepository;
    /**
     * 根据给定的主机域名名称，获取对应的公司基本信息
     *
     * @param hostname 主机域名名称
     * @return 公司基本信息
     */
    @Cacheable(value = "Company_Host;1800", unless = "#result == null")
    public Optional<TCompanyBaseInformation> findCompanyInformationByHostname(String hostname) {
        return enrolledCompanyRepository.findBySubdomainName(hostname)
                .map(companyMapper::toBaseInformation);
    }

    /**
     * 根据本单位id,页码，页数，获取供应商信息
     *
     * @param id 本单位id，页码 pageNum,页数 pageSize
     * @return 供应商信息列表
     */

    public Page<VSuppliersIncludeBrandsResponse.VSupplier> CompanyIncludeBrandbyId(String id, Optional<String> pageNum,Optional<String> pageSize) {

        List<CompTrad> compTradList=findSuppliersByCompTradIdCompBuyerAndState(id,Trade.TRANSACTION);
        List<TCompanyIncludeBrand>  tCompanyIncludeBrandList=compTradList.stream()
            .map(compTradeMapper::toSuppliersIncludeBrand)
            .filter(t -> t.getState().equals("1"))
            .collect(Collectors.toList());
        Page<TCompanyIncludeBrand> tCompanyIncludeBrands =PageTools.listConvertToPage(
            tCompanyIncludeBrandList,
            PageRequest.of(
                pageNum.map(PageTools::verificationPageNum).orElse(0),
                pageSize.map(PageTools::verificationPageSize).orElse(10)
            )
        );

        tCompanyIncludeBrands.forEach(compTrad ->  {
            //将供应商中的经营品牌与授权品牌和自营品牌对比进行去重
            List<TBrand>   selfSupportBrands =  compTrad.getSelfSupportBrands().stream().filter(tBrand -> compTrad.getManageBrands().contains(tBrand)).collect(Collectors.toList());
            List<TBrand>      authBrands     = compTrad.getAuthBrands().stream().filter(tBrand -> compTrad.getManageBrands().contains(tBrand)).toList();
            List<TBrand>    managerBrands    =  compTrad.getManageBrands().stream().filter(tBrand -> !selfSupportBrands.contains(tBrand))
                                                    .collect(Collectors.toList());
                             managerBrands   =  managerBrands.stream().filter(tBrand -> !authBrands.contains(tBrand))
                                                   .collect(Collectors.toList());
            selfSupportBrands.forEach(dcBrand -> dcBrand.setOwned(true));
            authBrands.forEach(dcBrand -> dcBrand.setVending(true));
             //将供应商中的经营品牌、授权品牌、自营品牌合并在一个集合中
            if(selfSupportBrands.isEmpty())
                compTrad.setSelfSupportBrands(new ArrayList<>());
            else
                compTrad.setSelfSupportBrands(selfSupportBrands);
            if(!authBrands.isEmpty())
                compTrad.getSelfSupportBrands().addAll(authBrands);
            if(!managerBrands.isEmpty())
                compTrad.getSelfSupportBrands().addAll(managerBrands);
            if(compTrad.getSelfSupportBrands().size()>5)
                compTrad.setSelfSupportBrands(compTrad.getSelfSupportBrands().subList(0,5));
        });
        return   tCompanyIncludeBrands .map(compTradeMapper::toPreloadSuppliersIncludeBrandDTOs);
    }
    @Cacheable(value = "SupplierAndBrand;1800", unless = "#result == null",key = "#compBuyer")
    public  List<CompTrad> findSuppliersByCompTradIdCompBuyerAndState(String compBuyer, Trade state){
        return  compTradeRepository.findSuppliersByCompTradIdCompBuyerAndState(compBuyer,state);
    }
    /**
     * 根据品牌查询本单位的供应商
     * @param brands 品牌编码列表
     * @param id 单位id
     * @return 返回供应商列表
     */
    @Cacheable(value = "suppliers_brands;1800", unless = "#result == null",key = "T(String).valueOf(#brands)")
    public List<TCompanyBaseInformation> findSuppliersByBrands(List<String> brands,String id){
        QCompany qCompany = QCompany.company;
        QCompTradBrand qCompTradBrand = QCompTradBrand.compTradBrand;

        JPAQuery<Company> query =  queryFactory.selectDistinct(qCompany).from(qCompTradBrand).leftJoin(qCompany)
            .on(qCompany.code.eq(qCompTradBrand.compTradBrandId.compSaler));
        if(brands.size()>0){
            query.where(qCompTradBrand.compTradBrandId.brandCode.in(brands));
        }
        if(!id.isEmpty()){
            query.where(qCompTradBrand.compTradBrandId.compBuyer.eq(id));
        }
        query.where(qCompany.state.eq(Availability.ENABLED));
        List<Company> companies =query.orderBy(qCompany.code.desc())
                                     .fetch();
        return companies.stream()
            .map(companyMapper::toBaseInformation)
            .collect(Collectors.toList());
    }

    /**
     * 查找外供应商列表
     * @param compayCode 单位id
     * @return 返回外供应商列表
     */
   public List<TCompanyBaseInformation> findOutsideSuppliers(String compayCode){
       QCompany qCompany = QCompany.company;
       QCompTrad qCompTrad = QCompTrad.compTrad;
       JPAQuery<Company> query =  queryFactory.selectDistinct(qCompany).from(qCompany).leftJoin(qCompTrad)
           .on(qCompany.code.eq(qCompTrad.compTradId.compSaler));
           query.where
               (qCompTrad.compTradId.compBuyer.eq(compayCode)
                   .and(qCompany.role.eq(CompanyRole.EXTERIOR_SUPPLIER.getSign())));
       List<Company> companies =query.orderBy(qCompany.code.desc())
           .fetch();
       return companies.stream()
           .map(companyMapper::toBaseInformation)
           .toList();
   }

    /**
     * 查找外供应商详情
     * @param code 供应商编码
     * @param companyCode 单位编码
     * @return 返回详细信息
     */
    @Cacheable(value = "supplierDetail;1800", unless = "#result == null ",key = "#companyCode+'-'+#code")
   public VSupplierDetailResponse.VSupplier findOutsideSupplierDetail(String code,String companyCode){

       var trade =compTradeRepository.findById(CompTradId.builder()
              .compSaler(code)
              .compBuyer(companyCode)
          .build()
           );
       var brands = trade.get().getManageBrands().stream()
           .map(brandMapper::toBrand)
           .map(brandMapper::toSupplierBrandPreload)
           .toList();
       VSupplierDetailResponse.VSupplier vSupplier=trade.map(CompTrad::getCompanys)
           .map(companyMapper::toBaseInformation)
           .map(companyMapper::toSupplierDetail).get();
       vSupplier.setBrands(brands);
       vSupplier.setTaxMode(String.valueOf(trade.get().getTaxModel().getSign()));
      return  vSupplier;
   }

    /**
     * 保存外供应商
     * @param outsideSupplier 供应商信息
     * @param companyCode 单位id
     * @return 返回成功或者是吧消息
     */
   @Caching(evict = {@CacheEvict(value = "suppliers_brands;1800",allEntries = true),
       @CacheEvict(value = "brands_company;1800",allEntries = true),
       @CacheEvict(value = "SupplierAndBrand;1800",key = "#companyCode"),
       @CacheEvict(value = "supplierDetail;1800",key = "#companyCode+'-'+#code" ,condition = "#code != null"),
   })
   @Transactional
   public Map<String,Object> saveOutsideSupplier(VOutsideSupplierRequest outsideSupplier,String companyCode,String code){
       Map<String,Object> map = new HashMap<>();
       String maxCode;
       Company company;
       try{
           if(code==null){
               maxCode =   companyRepository.findMaxCode(CompanyRole.EXTERIOR_SUPPLIER.getSign(),companyCode);
               if(maxCode==null)
                   maxCode="101";
               code=companyCode+maxCode;
                company = Company.builder()
                   .code(code)
                   .encode(maxCode)
                   .USCI(outsideSupplier.getUsci())
                   .role(CompanyRole.EXTERIOR_SUPPLIER.getSign())
                   .nameInCN(outsideSupplier.getCompanyName())
                   .shortNameInCN(outsideSupplier.getCompanyShortName())
                   .areaCode(outsideSupplier.getAreaCode())
                   .areaName(addressService.findByCode("",outsideSupplier.getAreaCode()))
                   .contactName(outsideSupplier.getContactName())
                   .contactPhone(outsideSupplier.getContactPhone())
                   .email(outsideSupplier.getEmail())
                   .phone(outsideSupplier.getPhone())
                   .address(outsideSupplier.getAddress())
                    .state(Availability.ENABLED)
                   .build();
           }else{
               Company company1 =   companyRepository.findById(code).get();
                company = Company.builder()
                   .code(code)
                   .encode(company1.getEncode())
                   .USCI(company1.getUSCI())
                   .role(CompanyRole.EXTERIOR_SUPPLIER.getSign())
                   .nameInCN(company1.getNameInCN())
                   .shortNameInCN(outsideSupplier.getCompanyShortName())
                   .areaCode(outsideSupplier.getAreaCode())
                   .areaName(addressService.findByCode("",outsideSupplier.getAreaCode()))
                   .contactName(outsideSupplier.getContactName())
                   .contactPhone(outsideSupplier.getContactPhone())
                   .email(outsideSupplier.getEmail())
                   .phone(outsideSupplier.getPhone())
                   .address(outsideSupplier.getAddress())
                   .build();
          }
           companyRepository.save(company);
           //保存价税模式
           compTradeRepository.save(
               CompTrad.builder()
                   .compTradId(
                       CompTradId.builder()
                           .compBuyer(companyCode)
                           .compSaler(code)
                           .build()
                   )
                   .taxModel(outsideSupplier.getTaxMode().equals("0")? TaxModel.UNTAXED:TaxModel.INCLUDED)
                   .state(Trade.TRANSACTION)
                   .build()
           );
           List<CompTradBrand> compTradBrands = new ArrayList<>();
           String finalCode = code;
           outsideSupplier.getBrands().forEach(s -> compTradBrands.add(
               CompTradBrand.builder()
                   .compTradBrandId(CompTradBrandId.builder()
                       .brandCode(s)
                       .compBuyer(companyCode)
                       .compSaler(finalCode)
                       .build())
                   .sort(0)
                   .build()
               )
           );
           //保存经营品牌
           compTradBrandRepository.saveAll(compTradBrands);
           map.put("code",200);
           map.put("message","保存成功");
           return map;
       }catch (Exception e){
           e.printStackTrace();
           map.put("code",500);
           map.put("message","保存失败");
           return map;
       }
   }
    @Caching(evict = {@CacheEvict(value = "suppliers_brands;1800",allEntries = true),
        @CacheEvict(value = "brands_company;1800",allEntries = true),
        @CacheEvict(value = "SupplierAndBrand;1800",allEntries = true),
        @CacheEvict(value = "supplierDetail;1800",condition = "#code != null"),
    })
    @Transactional
   public Boolean modifySupplierState(List<String> code,Availability state){
       try {
           companyRepository.updateCompanyState(state,code);
           return  true;
       }catch (Exception e){
           e.printStackTrace();
           return  false;
       }

   }

   public  Map<String,Object> supplierVerification(String ucsi,String companyCode){
       Map<String,Object> map = new HashMap<>();
      List<Company>  list =   companyRepository.findCompanyByUSCI(ucsi);
      if (list.size()>0){
          //判断用户是否为外供
         List<String> outSuppliers =list.stream()
             .filter(company -> company.getRole().equals(CompanyRole.EXTERIOR_SUPPLIER.getSign()))
             .map(Company::getCode)
             .toList();
         if(outSuppliers.size()>0){
             List<CompTrad> compTradList=compTradeRepository.findCompTradsByCompTradId_CompBuyerAndCompTradId_CompSalerIn(companyCode,outSuppliers);
             if(compTradList.size()>0){
                 map.put("code",201);
                 map.put("company",new VSupplierDetailResponse.VSupplier());
                 map.put("message","该供应商已存在于外供应商列表，不可重新添加");
                 return map;
             }
         }
          map.put("code",200);
          map.put("company",list.stream()
              .map(companyMapper::toBaseInformation)
              .map(companyMapper::toSupplierDetail)
              .toList()
              .get(0)
          );
          map.put("message","该社会统一信用代码正确");
      }else {
          //调取第三方验证接口
          map.put("code",404);
          map.put("company",new VSupplierDetailResponse.VSupplier());
          map.put("message","该社会统一信用代码不正确");
      }
       return map;
   }
}
