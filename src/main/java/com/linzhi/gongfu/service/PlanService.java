package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TTemporaryPlan;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.DemandSource;
import com.linzhi.gongfu.mapper.PurchasePlanMapper;
import com.linzhi.gongfu.mapper.TemporaryPlanMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.vo.VPlanDemandRequest;
import com.linzhi.gongfu.vo.VPurchasePlanResponse;
import com.linzhi.gongfu.vo.VTemporaryPlanRequest;
import com.linzhi.gongfu.vo.VVerificationPlanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 采购计划信息处理及业务服务
 *
 * @author zgh
 * @create_at 2022-02-10
 */
@RequiredArgsConstructor
@Service
public class PlanService {
    private final TemporaryPlanRepository temporaryPlanRepository;
    private final TemporaryPlanMapper temporaryPlanMapper;
    private final ProductRepository productRepository;
    private final PurchasePlanMapper purchasePlanMapper;
    private final CompTradBrandRepository compTradBrandRepository;
    private final PurchasePlanRepository purchasePlanRepository;
    private final PurchasePlanProductSupplierRepository purchasePlanProductSupplierRepository;
    private final CompanyRepository companyRepository;
    private final PurchasePlanProductRepository purchasePlanProductRepository;
    /**
     * 根据单位id、操作员编码查询该操作员的临时采购计划列表
     * @param temporaryPlanId 单位id 操作员编码
     * @return 临时采购计划列表信息
     */
    public List<TTemporaryPlan>  findTemporaryPlanByOperator(TemporaryPlanId temporaryPlanId){
        return  temporaryPlanRepository.findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedBy(temporaryPlanId.getDcCompId(),temporaryPlanId.getCreatedBy()).stream()
            .map(temporaryPlanMapper::toTemporaryPlan)
            .collect(Collectors.toList());
    }
    //
    /**
     * 根据单位id、操作员编码查询该操作员的临时采购计划列表
     * @return 已经存在于采购临时计划表中的产品列表
     */
    public Optional<VVerificationPlanResponse>  TemporaryPlanVerification(List<String> product){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        var list =temporaryPlanRepository.findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedBy(session.getSession().getCompanyCode(),session.getSession().getOperatorCode()).stream()
            .filter(temporaryPlan -> product.contains(temporaryPlan.getTemporaryPlanId().getProductId()))
            .map(temporaryPlanMapper::toTemporaryPlan)
            .map(temporaryPlanMapper::toPreloadVerificationTemporaryPlan)
            .collect(Collectors.toList());
        if(list.size()==0)
            return Optional.empty();
        return Optional.of(
            VVerificationPlanResponse.builder()
                .code(201)
                .message("验证未通过，需要加入计划的产品有部分已存在于计划列表，请重新添加")
                .products(list)
                .build()
        ) ;
    }
    /**
     * 保存临时采购计划
     * @param product 产品列表
     * @param id 单位id
     * @param operatorCode 操作员编码
     */
    @Transactional
    public void saveTemporaryPlan(List<VTemporaryPlanRequest> product, String id, String operatorCode){
        List<String> proCodeList = new ArrayList<>();
        Map<String, Product> productMap = new HashMap<>();
        product.stream().forEach(p -> proCodeList.add(p.getProductId()));
        List<Product> products=productRepository.findProductByIdIn(proCodeList);
        products.stream().forEach(product1 ->
            productMap.put(product1.getId(),product1)
        );
        List<TemporaryPlan>  saveList = new ArrayList<>();
        product.forEach(pr -> {
            Product  p=productMap.get(pr.getProductId());
            TemporaryPlan temporaryPlan =TemporaryPlan.builder()
                .temporaryPlanId(TemporaryPlanId.builder().dcCompId(id).productId(p.getId()).createdBy(operatorCode).build())
                .productCode(p.getCode())
                .chargeUnit(p.getChargeUnit())
                .brand(p.getBrand())
                .brandCode(p.getBrandCode())
                .describe(p.getDescribe())
                .demand(pr.getDemand())
                .build();
            saveList.add(temporaryPlan);
        });
        temporaryPlanRepository.saveAll(saveList);
    }

    /**
     * 修改计划需求
     * @param product 计划产品列表
     * @param id 单位id
     * @param operatorCode 操作员编码
     */
    @Transactional
    public void modifyTemporaryPlan(List<VTemporaryPlanRequest> product, String id, String operatorCode){
        product.stream().forEach(pr -> {
            temporaryPlanRepository.updateNameById(pr.getDemand(),TemporaryPlanId.builder().dcCompId(id).productId(pr.getProductId()).createdBy(operatorCode).build());
        });
    }
    /**
     * 删除计划需求
     * @param product 产品id列表
     * @param id 单位id
     * @param operatorCode 操作员编码
     */
    public boolean deleteTemporaryPlan( List<String> product, String id, String operatorCode){
        try{
            List<TemporaryPlanId> list = new ArrayList<>();
            product.stream().forEach(pr -> {
                list.add(TemporaryPlanId.builder().dcCompId(id).productId(pr).createdBy(operatorCode).build());
            });
            temporaryPlanRepository.deleteAllById(list);
            return  true;
        }catch (Exception e){
           return false;
        }

    }

    /**
     * 保存采购计划
     * @param products 产品id列表
     * @param suppliers 供应商编码列表
     * @param id 单位id
     * @param operatorCode 操作员编号
     * @return 返回采购计划号
     */
    @Transactional
    public Optional<String> savaPurchasePlan(List<String> products,List<String> suppliers,String id, String operatorCode){
        Map<String,List<Company>>  brandCompMap = new HashMap<>();
             //查出所选计划
            List<TemporaryPlan> temporaryPlans = temporaryPlanRepository.findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedByAndTemporaryPlanId_ProductIdIn(id,operatorCode,products);
            if(temporaryPlans.size()==0)
                return Optional.empty();
            //查看有几个品牌,每个品牌所属供应商有哪些
            List<String> brands =temporaryPlans.stream().map(TemporaryPlan::getBrandCode).distinct().collect(Collectors.toList());
            Map<String,List<Company>> brandsSuppliers = findSuppliersByBrandsAndCompBuyer(brands,id,suppliers);
            //查询采购计划号最大编号
            String maxCode= purchasePlanRepository.findMaxCode(id,operatorCode, LocalDate.now());
            //计划编码
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate data=LocalDate.now();
            String planCode = "HJ-"+operatorCode+"-"+dtf.format(data)+"-"+maxCode;
            List<PurchasePlanProduct> purchasePlanProducts = new ArrayList<>();
            //保存
            temporaryPlans.forEach(temporaryPlan -> {
                List<PurchasePlanProductSupplier> purchasePlanProductSalers = new ArrayList<>();
                List<Company> companies = brandsSuppliers.get(temporaryPlan.getBrandCode());
                companies.forEach(company -> {
                    PurchasePlanProductSupplier productSaler = PurchasePlanProductSupplier.builder()
                        .purchasePlanProductSalerId(PurchasePlanProductSupplierId.builder()
                            .productId(temporaryPlan.getTemporaryPlanId().getProductId())
                            .planCode(planCode)
                            .dcCompId(id)
                            .salerCode(company.getCode())
                            .build())
                        .salerName(company.getShortNameInCN())
                        .demand(BigDecimal.ZERO)
                        .deliverNum(BigDecimal.ZERO)
                        .tranNum(BigDecimal.ZERO)
                        .build();
                    purchasePlanProductSalers.add(productSaler);
                });
                PurchasePlanProduct product=PurchasePlanProduct.builder()
                    .purchasePlanProductId(PurchasePlanProductId.builder()
                        .planCode(planCode)
                        .dcCompId(id)
                        .productId(temporaryPlan.getTemporaryPlanId().getProductId()).build())
                    .productCode(temporaryPlan.getProductCode())
                    .brandCode(temporaryPlan.getBrandCode())
                    .brand(temporaryPlan.getBrand())
                    .chargeUnit(temporaryPlan.getChargeUnit())
                    .describe(temporaryPlan.getDescribe())
                    .facePrice(temporaryPlan.getProduct().getFacePrice())
                    .demand(temporaryPlan.getDemand())
                    .salers(purchasePlanProductSalers)
                    .deliverNum(BigDecimal.ZERO)
                    .beforeSalesPrice(BigDecimal.ZERO)
                    .inquiryNum(BigDecimal.ZERO)
                    .safetyStock(BigDecimal.ZERO)
                    .tranNum(BigDecimal.ZERO)
                    .build();
                purchasePlanProducts.add(product);
            });
            PurchasePlan purchasePlan = PurchasePlan.builder()
                .purchasePlanId(PurchasePlanId.builder()
                    .planCode(planCode)
                    .dcCompId(id)
                    .build())
                .createdBy(operatorCode)
                .createdAt(LocalDate.now())
                .source(DemandSource.FUZZY_QUERY)
                .product(purchasePlanProducts)
                .build();
            purchasePlanRepository.save(purchasePlan);
            deleteTemporaryPlan(products,id,operatorCode);
            return Optional.of(planCode);
    }

    /**
     * 根据品牌列表，本单位id，供应商列表查询 每个品牌有哪些供应商
     * @param brands
     * @param compBuyer
     * @param suppliers
     * @return 返回 品牌包含供应商列表的Map
     */
    public Map<String,List<Company>> findSuppliersByBrandsAndCompBuyer(List<String> brands,String compBuyer,List<String> suppliers){
        //查询这几个牌子的供应商有哪些
        List<CompTradBrand> compTradBrands= compTradBrandRepository.findCompTradBrandByCompTradBrandId_BrandCodeInAndCompTradBrandId_CompBuyerOrderBySortDesc(brands,compBuyer);
        Map<String,List<Company>>  NoIncludeCompMap = new HashMap<>();
        Map<String,List<Company>>  IncludeCompMap = new HashMap<>();
        List<CompTradBrand> compTradIncludeCompList= compTradBrands.stream()
            .filter(compTradBrand -> suppliers.contains(compTradBrand.getCompany().getCode())).collect(Collectors.toList());
        List<CompTradBrand> compTradNoIncludeCompList= compTradBrands.stream()
            .filter(compTradBrand -> !suppliers.contains(compTradBrand.getCompany().getCode())).collect(Collectors.toList());
        compTradNoIncludeCompList.stream()
            .forEach(compTradBrand -> {
                List<Company> list = NoIncludeCompMap.get(compTradBrand.getCompTradBrandId().getBrandCode());
                if(list==null)
                    list = new ArrayList<>();
                list.add(compTradBrand.getCompany());
                NoIncludeCompMap.put(compTradBrand.getCompTradBrandId().getBrandCode(),list);
            });
        compTradIncludeCompList.stream()
            .forEach(compTradBrand -> {
                List<Company> list = IncludeCompMap.get(compTradBrand.getCompTradBrandId().getBrandCode());
                if(list==null)
                    list = new ArrayList<>();
                list.add(compTradBrand.getCompany());
                IncludeCompMap.put(compTradBrand.getCompTradBrandId().getBrandCode(),list);
            });
        brands.forEach(s -> {
            List<Company> list = IncludeCompMap.get(s);
            List<Company> nolist = NoIncludeCompMap.get(s);
            if(list==null)
                list = new ArrayList<>();
            if(list==null)
                nolist = new ArrayList<>();
            List<Company> finalHaslist = list;
            nolist.forEach(company -> {
                if(finalHaslist.size()<5)
                    finalHaslist.add(company);
            });
            IncludeCompMap.put(s,finalHaslist);
        });
        return IncludeCompMap;
    }

    /**
     * 根据品牌列表，本单位id，供应商列表查询 每个品牌有哪些供应商
     * @param brands
     * @param compBuyer
     * @return 返回 品牌
     */
    public List<String> brandVerification(List<String> brands,String compBuyer){
        //查询这几个牌子的供应商有哪些
        List<CompTradBrand> compTradBrands= compTradBrandRepository.findCompTradBrandByCompTradBrandId_BrandCodeInAndCompTradBrandId_CompBuyerOrderBySortDesc(brands,compBuyer);
        List<String> brandList = new ArrayList<>();
        Map<String,List<Company>>  map = new HashMap<>();
        compTradBrands.stream()
            .forEach(compTradBrand -> {
                List<Company> list = map.get(compTradBrand.getCompTradBrandId().getBrandCode());
                if(list==null)
                    list = new ArrayList<>();
                list.add(compTradBrand.getCompany());
                map.put(compTradBrand.getCompTradBrandId().getBrandCode(),list);
            });
        brands.forEach(s -> {
            List<Company> list = map.get(s);
            if(list==null)
                brandList.add(s);
        });

        return  brandList;
    }

    /**
     * 根据计划号查询采购计划
     * @param planCode 采购计划号
     * @return 返回采购计划信息
     */
    public Optional<VPurchasePlanResponse> findPurchasePlanByCode(String planCode, String id){
        return purchasePlanRepository.findById(PurchasePlanId.builder().planCode(planCode).dcCompId(id).build())
            .map(purchasePlanMapper::toDTO)
            .map(purchasePlanMapper::toPruchasePlan);
    }

    /**
     * 替换采购计划中的供应商
     * @param id 单位id
     * @param planCode 采购计划号
     * @param productId 产品id
     * @param oldSupplier 原供应商编号
     * @param newSupplier 新的供应编号
     */
    @Transactional
    public boolean  modifyPlanSupplier(String id,String planCode,String productId,String oldSupplier,String newSupplier){
        try{
            Optional<Company> supplier =companyRepository.findById(newSupplier);
            purchasePlanProductSupplierRepository.deleteById(PurchasePlanProductSupplierId.builder()
                .productId(productId).dcCompId(id).planCode(planCode).salerCode(oldSupplier).build());
            purchasePlanProductSupplierRepository.save(PurchasePlanProductSupplier.builder()
                .purchasePlanProductSalerId(PurchasePlanProductSupplierId.builder()
                    .productId(productId).dcCompId(id).planCode(planCode).salerCode(newSupplier).build())
                .salerName(supplier.get().getShortNameInCN())
                .demand(BigDecimal.ZERO)
                .build());
            return   true;
        }catch (Exception e){
            return false;
        }

    }
    @Transactional
    public boolean modifyPurchasePlanForSeveral(String id, List<VPlanDemandRequest> demands){
        try{
            demands.forEach(v -> {
                purchasePlanProductSupplierRepository.updateDemandById(v.getDemand(),PurchasePlanProductSupplierId.builder()
                    .productId(v.getProductId())
                    .dcCompId(id)
                    .salerCode(v.getSupplierCode())
                    .planCode(v.getPlanCode())
                    .build());
            });
            return true;
        }catch (Exception e){
            return  false;
        }
    }
    @Transactional
    public boolean modifyPurchasePlanDemand(String id, List<VPlanDemandRequest> demands){
        try{
            demands.forEach(v -> {
                purchasePlanProductRepository.updateDemandById(v.getDemand(),PurchasePlanProductId.builder()
                    .productId(v.getProductId())
                    .dcCompId(id)
                    .planCode(v.getPlanCode())
                    .build());
            });
            return true;
        }catch (Exception e){
            return  false;
        }
    }
    @Transactional
    public  boolean savePlanProduct(String id,String productId,String planCode,BigDecimal demand){
        try{
            Optional<Product> product = productRepository.findById(productId);
            List<String> brands = new ArrayList<>();
            brands.add(product.get().getBrandCode());
           List<Company> suppliers =  findSuppliersByBrandsAndCompBuyer(brands,id,new ArrayList<>()).get(product.get().getBrandCode());
           List<PurchasePlanProductSupplier> purchasePlanProductSuppliers = new ArrayList<>();
           suppliers.forEach(company -> {
                   PurchasePlanProductSupplier productSaler = PurchasePlanProductSupplier.builder()
                       .purchasePlanProductSalerId(PurchasePlanProductSupplierId.builder()
                           .productId(productId)
                           .planCode(planCode)
                           .dcCompId(id)
                           .salerCode(company.getCode())
                           .build())
                       .salerName(company.getShortNameInCN())
                       .demand(BigDecimal.ZERO)
                       .deliverNum(BigDecimal.ZERO)
                       .tranNum(BigDecimal.ZERO)
                       .build();
               purchasePlanProductSuppliers.add(productSaler);
           });

            PurchasePlanProduct purchasePlanProduct=PurchasePlanProduct.builder()
                .purchasePlanProductId(PurchasePlanProductId.builder()
                    .planCode(planCode)
                    .dcCompId(id)
                    .productId(productId)
                    .build())
                .productCode(product.get().getCode())
                .brandCode(product.get().getBrandCode())
                .brand(product.get().getBrand())
                .chargeUnit(product.get().getChargeUnit())
                .describe(product.get().getDescribe())
                .facePrice(product.get().getFacePrice())
                .demand(demand)
                .salers(purchasePlanProductSuppliers)
                .deliverNum(BigDecimal.ZERO)
                .beforeSalesPrice(BigDecimal.ZERO)
                .inquiryNum(BigDecimal.ZERO)
                .safetyStock(BigDecimal.ZERO)
                .tranNum(BigDecimal.ZERO)
                .build();
            purchasePlanProductRepository.save(purchasePlanProduct);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
