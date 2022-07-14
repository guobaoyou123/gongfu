package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TBrand;
import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.dto.TCompanyIncludeBrand;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.*;
import com.linzhi.gongfu.mapper.BrandMapper;
import com.linzhi.gongfu.mapper.CompTradeMapper;
import com.linzhi.gongfu.mapper.CompanyMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private final AddressService addressService;
    private final CompTradBrandRepository compTradBrandRepository;
    private final CompVisibleRepository compVisibleRepository;
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
    public Page<VSuppliersIncludeBrandsResponse.VSupplier> CompanyIncludeBrandById(String id, Optional<String> pageNum,Optional<String> pageSize) {

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

    /**
     * 查询供应的税模式列表
     * @param compBuyer 买方编码
     * @param state 可用状态
     * @return 供应商税模式列表
     */
    @Cacheable(value = "SupplierAndBrand;1800", unless = "#result == null",key = "#compBuyer")
    public  List<CompTrad> findSuppliersByCompTradIdCompBuyerAndState(String compBuyer, Trade state){
        return  compTradeRepository.findSuppliersByCompTradId_CompBuyerAndState(compBuyer,state);
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
     * @param companyCode 单位id
     * @return 返回外供应商列表
     */
   public List<TCompanyBaseInformation> findForeignSuppliers(String companyCode){
       QCompany qCompany = QCompany.company;
       QCompTrad qCompTrad = QCompTrad.compTrad;
       JPAQuery<Company> query =  queryFactory.selectDistinct(qCompany).from(qCompany).leftJoin(qCompTrad)
           .on(qCompany.code.eq(qCompTrad.compTradId.compSaler));
           query.where
               (qCompTrad.compTradId.compBuyer.eq(companyCode)
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
   public VSupplierDetailResponse.VSupplier findForeignSupplierDetail(String code,String companyCode){

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
       vSupplier.setTaxMode(String.valueOf(trade.get().getTaxModel().getTaxMode()));
      return  vSupplier;
   }

    /**
     * 保存外供应商
     * @param foreignSupplier 供应商信息
     * @param companyCode 单位id
     * @return 返回成功或者是吧消息
     */
   @Caching(evict = {@CacheEvict(value = "suppliers_brands;1800",allEntries = true),
       @CacheEvict(value = "brands_company;1800",allEntries = true),
       @CacheEvict(value = "SupplierAndBrand;1800",key = "#companyCode"),
       @CacheEvict(value = "supplierDetail;1800",key = "#companyCode+'-'+#code" ,condition = "#code != null"),
   })
   @Transactional
   public Map<String,Object> saveForeignSupplier(VForeignSupplierRequest foreignSupplier, String companyCode, String code){
       Map<String,Object> map = new HashMap<>();
       String maxCode;
       Company company;
       try{
           if(code==null){
               //判重
               QCompany qCompany = QCompany.company;
               QCompTrad qCompTrad = QCompTrad.compTrad;
               QEnrolledCompany qEnrolledCompany = QEnrolledCompany.enrolledCompany;
               JPAQuery<Company> query =  queryFactory.selectDistinct(qCompany).from(qCompany,qEnrolledCompany).leftJoin(qCompTrad)
                   .on(qCompany.code.eq(qCompTrad.compTradId.compSaler).and(qCompTrad.compTradId.compBuyer.eq(companyCode)));
               query.where(qEnrolledCompany.USCI.eq(foreignSupplier.getUsci()));
               query.where(qEnrolledCompany.id.eq(qCompany.identityCode));
               query.where(qCompany.role.eq(CompanyRole.EXTERIOR_SUPPLIER.getSign()));
               List<Company> list=query.fetch();
               if(list.size()>0){
                   map.put("code",201);
                   map.put("message","供应商已存在于列表中");
                   return map;
               }
               //查询有没有系统唯一码
               Optional<EnrolledCompany> enrolledCompany= enrolledCompanyRepository.findByUSCI(foreignSupplier.getUsci());
               //判断系统单位表是否为空
               if(enrolledCompany.isEmpty()){
                   //空的话获取系统单位表的最大编码，生成新的单位
                   String maxId = enrolledCompanyRepository.findMaxCode();
                   if(maxId==null)
                       maxId="1002";
                   EnrolledCompany enrolledCompany1 = EnrolledCompany.builder()
                       .id(maxId)
                       .nameInCN(foreignSupplier.getCompanyName())
                       .USCI(foreignSupplier.getUsci())
                       .contactName(foreignSupplier.getContactName())
                       .contactPhone(foreignSupplier.getContactPhone())
                       .state(Enrollment.NOT_ENROLLED)
                       .build();
                   enrolledCompanyRepository.save(enrolledCompany1);
                   enrolledCompany=Optional.of(enrolledCompany1);
               }
               maxCode =   companyRepository.findMaxCode(CompanyRole.EXTERIOR_SUPPLIER.getSign(),companyCode);
               if(maxCode==null)
                   maxCode="101";
               code=companyCode+maxCode;
                company = Company.builder()
                   .code(code)
                   .encode(maxCode)
                  // .USCI(foreignSupplier.getUsci())
                   .role(CompanyRole.EXTERIOR_SUPPLIER.getSign())
                   .nameInCN(foreignSupplier.getCompanyName())
                    .identityCode(enrolledCompany.get().getId())
                    .state(Availability.ENABLED)
                   .build();
           }else{
               company =   companyRepository.findById(code).orElseThrow(()->new IOException("从数据库搜索不到该供应商"));
               compTradBrandRepository.deleteCompTradBrand(companyCode,code)  ;
          }
           if(foreignSupplier.getAreaCode()!=null&&!foreignSupplier.getAreaCode().equals("")){
               company.setAreaCode(foreignSupplier.getAreaCode());
               company.setAreaName(addressService.findByCode("",foreignSupplier.getAreaCode()));
           }else{
               company.setAreaCode(null);
               company.setAreaName(null);
           }
           company.setAddress(foreignSupplier.getAddress());
           company.setShortNameInCN(foreignSupplier.getCompanyShortName());
           company.setContactName(foreignSupplier.getContactName());
           company.setContactPhone(foreignSupplier.getContactPhone());
           company.setEmail(foreignSupplier.getEmail());
           company.setPhone(foreignSupplier.getPhone());
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
                   .taxModel(foreignSupplier.getTaxMode().equals("0")? TaxMode.UNTAXED: TaxMode.INCLUDED)
                   .state(Trade.TRANSACTION)
                   .build()
           );
           List<CompTradBrand> compTradBrands = new ArrayList<>();
           String finalCode = code;
           foreignSupplier.getBrands().forEach(s -> compTradBrands.add(
               CompTradBrand.builder()
                   .compTradBrandId(
                       CompTradBrandId.builder()
                       .brandCode(s)
                       .compBuyer(companyCode)
                       .compSaler(finalCode)
                       .build()
                   )
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

    /**
     * 修改供应商状态
     * @param code 供应商编码
     * @param state 状态
     * @return 返回成功或则失败
     */
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

    /**
     * 验证社会统一信息代码
     * @param usci  社会统一信用代码
     * @param companyCode 公司编码
     * @return 返回公司信息
     */
   public  Map<String,Object> supplierVerification(String usci,String companyCode){
       Map<String,Object> map = new HashMap<>();
       List<Company> list = companyRepository.findCompanyByUSCI(usci);
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
                 map.put("companyName","UNKNOWN");
                 map.put("message","该供应商已存在于外供应商列表，不可重新添加");
                 return map;
             }
         }
          map.put("code",200);
          map.put("companyName",list.stream()
              .map(companyMapper::toBaseInformation)
              .toList()
              .get(0).getName()
          );
          map.put("message","该社会统一信用代码正确");
      }else {
          //调取第三方验证接口
          map.put("code",404);
          map.put("companyName","UNKNOWN");
          map.put("message","该社会统一信用代码不正确");
      }
       return map;
   }

    /**
     * 查找公司详情
     * @param companyCode 单位编码
     * @return 返回详细信息
     */
    public VCompanyDetailResponse.VCompany findCompanyDetail(String companyCode) throws Exception {
        return  enrolledCompanyRepository.findById(companyCode).map(companyMapper::toCompDetail)
           .map(companyMapper::toCompanyDetail).orElseThrow(()->new IOException("未找到公司信息"));
    }

  /*  *//**
     * 查找公司详情
     * @param companyCode 单位编码
     * @return 返回详细信息
     *//*
   public Optional<EnrolledCompany>  findCompanyById(String companyCode)  {
        return  enrolledCompanyRepository.findById(companyCode);
    }*/


    /**
     * 判重
     * @param companyCode 公司编码
     * @param name 公司简称
     * @return 是否重复
     */
    public boolean shortNameRepeat(String companyCode,String name){
        int count = companyRepository.checkRepeat(name,companyCode);
        return  count>=1;
    }

    /**
     * 保存本公司详情
     * @param companyRequest 供应商信息
     * @param companyCode 单位id
     * @return 返回成功或者失败消息
     */
    @Caching(evict =  {
        @CacheEvict(value = "Company_Host;1800",key = "#result"),
        @CacheEvict(value = "companyDetail;1800",key = "#companyCode")
    }
    )
    @Transactional
    public String  saveCompanyDetail(VCompanyRequest companyRequest, String companyCode,String operator){
        try{
            EnrolledCompany company = enrolledCompanyRepository.findById(companyCode).orElseThrow(()->new IOException("未找到公司信息"));
           company.getDetails().setShortNameInCN(companyRequest.getCompanyShortName());
           company.getDetails().setContactName(companyRequest.getContactName());
           company.getDetails().setContactPhone(companyRequest.getContactPhone());
           company.getDetails().setAreaCode(companyRequest.getAreaCode());
           if(companyRequest.getAreaCode()!=null){
               company.getDetails().setAreaName(addressService.findByCode("",companyRequest.getAreaCode()));
           }else{
               company.getDetails().setAreaName(null);
           }
            company.getDetails().setAddress(companyRequest.getAddress());
            company.setIntroduction(companyRequest.getIntroduction());
            enrolledCompanyRepository.save(company);
            companyRepository.save(company.getDetails());
            return company.getSubdomainName();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 设置可见
     * @param companyCode 公司编码
     * @param visibleContent 可见内容
     * @return 返回操作是否成功信息
     */

    @CacheEvict(value = "companyDetail;1800",key = "#companyCode")
    @Transactional
    public Boolean  setVisible(String companyCode,VCompanyVisibleRequest visibleContent){
        try{
           EnrolledCompany company =  enrolledCompanyRepository.findById(companyCode).orElseThrow(()->new IOException("没有从数据库中找到该公司公司信息"));
           company.setVisible(visibleContent.getVisible()?Whether.YES:Whether.NO);
           Optional<CompVisible> compVisible =  compVisibleRepository.findById(companyCode);
           if(visibleContent.getContent()==null&&!compVisible.isEmpty()) {
               compVisibleRepository.findById(companyCode);
           }else if(visibleContent.getContent()!=null){
               CompVisible compVisible1 =  compVisible.orElse(new CompVisible(companyCode,null));
               compVisible1.setVisibleContent(visibleContent.getContent());
               compVisibleRepository.save(compVisible1);
           }
           enrolledCompanyRepository.save(company);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
