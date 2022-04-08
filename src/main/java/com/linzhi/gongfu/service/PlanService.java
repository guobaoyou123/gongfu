package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.dto.TTemporaryPlan;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.*;
import com.linzhi.gongfu.mapper.TemporaryPlanMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.vo.VBaseResponse;
import com.linzhi.gongfu.vo.VPlanDemandRequest;
import com.linzhi.gongfu.vo.VTemporaryPlanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final CompTradBrandRepository compTradBrandRepository;
    private final PurchasePlanRepository purchasePlanRepository;
    private final PurchasePlanProductSupplierRepository purchasePlanProductSupplierRepository;
    private final CompanyRepository companyRepository;
    private final PurchasePlanProductRepository purchasePlanProductRepository;
    private final InquiryRepository inquiryRepository;
    private final CompTradeRepository compTradeRepository;
    private final VatRatesRepository vatRatesRepository;

    /**
     * 根据单位id、操作员编码查询该操作员的临时采购计划列表
     * @param temporaryPlanId 单位id 操作员编码
     * @return 临时采购计划列表信息
     */
    public List<TTemporaryPlan>  findTemporaryPlanByOperator(TemporaryPlanId temporaryPlanId){
        return  temporaryPlanRepository.findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedByOrderByCreatedAt(temporaryPlanId.getDcCompId(),temporaryPlanId.getCreatedBy()).stream()
            .map(temporaryPlanMapper::toTemporaryPlan)
            .collect(Collectors.toList());
    }

    /**
     * 保存临时采购计划
     * @param product 产品列表
     * @param id 单位id
     * @param operatorCode 操作员编码
     */
    @Transactional
    public Map<Object, Object> saveTemporaryPlan(List<VTemporaryPlanRequest> product, String id, String operatorCode){
        Map<Object, Object> resultMap = new HashMap<>();
        List<String> resultList = new ArrayList<>();
        final String[] message = {""};
        List<String> proCodeList = new ArrayList<>();
        Map<String, Product> productMap = new HashMap<>();
        try{
            product.forEach(p ->
                proCodeList.add(p.getProductId())
            );
            List<Product> products=productRepository.findProductByIdIn(proCodeList);
            products.forEach(product1 ->
                productMap.put(product1.getId(),product1)
            );
            //判断是否有已存在于计划列表中的产品
            var list = temporaryPlanRepository.findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedByOrderByCreatedAt(id, operatorCode).stream()
                .filter(temporaryPlan -> proCodeList.contains(
                    temporaryPlan.getTemporaryPlanId()
                        .getProductId()
                    )
                )
                .toList();
            Map<String,TemporaryPlan> temporaryPlanMap = new HashMap<>();
            list.forEach(temporaryPlan -> {
                resultList.add(temporaryPlan.getProductCode());
                temporaryPlanMap.put(temporaryPlan.getTemporaryPlanId().getProductId(),temporaryPlan);
            });
            //保存产品
            List<TemporaryPlan>  saveList = new ArrayList<>();
            product.forEach(pr -> {
                Product  p=productMap.get(pr.getProductId());
                TemporaryPlan temporaryPlan =temporaryPlanMap.get(pr.getProductId());
                if (temporaryPlan==null) {
                    temporaryPlan= TemporaryPlan.builder()
                        .temporaryPlanId(TemporaryPlanId.builder().dcCompId(id).productId(p.getId()).createdBy(operatorCode).build())
                        .productCode(p.getCode())
                        .chargeUnit(p.getChargeUnit())
                        .brand(p.getBrand())
                        .brandCode(p.getBrandCode())
                        .describe(p.getDescribe())
                        .demand(pr.getDemand())
                        .build();
                }else{
                    message[0] = message[0] +temporaryPlan.getProductCode()+",";
                    temporaryPlan.setDemand(temporaryPlan.getDemand().add(pr.getDemand()));
                }
                temporaryPlan.setCreatedAt(LocalDateTime.now());
                saveList.add(temporaryPlan);
            });
            temporaryPlanRepository.saveAll(saveList);
            resultMap.put("code",200);
            resultMap.put("message","加入计划成功");
            if(resultList.size()>0)
                resultMap.put("message","加入计划成功，产品编号为："+message[0]+"以存在于计划表中，并对需求数进行累加");
        }catch (Exception e){
            resultMap.put("code",500);
            resultMap.put("message","加入计划失败");
            return  resultMap;
        }
        return  resultMap;
    }

    /**
     * 修改计划需求
     * @param product 计划产品列表
     * @param id 单位id
     * @param operatorCode 操作员编码
     */
    @Transactional
    public void modifyTemporaryPlan(List<VTemporaryPlanRequest> product, String id, String operatorCode){
        product.forEach(pr -> temporaryPlanRepository.updateNameById(pr.getDemand(),TemporaryPlanId.builder().dcCompId(id).productId(pr.getProductId()).createdBy(operatorCode).build()));
    }

    /**
     * 删除计划需求
     * @param product 产品id列表
     * @param id 单位id
     * @param operatorCode 操作员编码
     */
    @Transactional
    public boolean deleteTemporaryPlan( List<String> product, String id, String operatorCode){
        try{
            List<TemporaryPlanId> list = new ArrayList<>();
            product.forEach(pr ->
                list.add(
                    TemporaryPlanId.builder()
                        .dcCompId(id)
                        .productId(pr)
                        .createdBy(operatorCode)
                        .build()
                )
            );
            temporaryPlanRepository.deleteAllById(list);
            return  true;
        }catch (Exception e){
            e.printStackTrace();
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
    public Map<String, Object> savePurchasePlan(List<String> products, List<String> suppliers, String id, String operatorCode){
           Map<String, Object> result = new HashMap<>();
        try{
            //查出所选计划
            List<TemporaryPlan> temporaryPlans = temporaryPlanRepository.findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedByAndTemporaryPlanId_ProductIdInOrderByCreatedAt(id,operatorCode,products);
            if(temporaryPlans.size()==0) {
                result.put("code", 404);
                result.put("message", "数据不存在");
                return result;
            }
            //查看有几个品牌,每个品牌所属供应商有哪些
            List<String> brands =temporaryPlans.stream()
                .map(TemporaryPlan::getBrandCode)
                .distinct()
                .collect(Collectors.toList());
            Map<String,List<Company>> brandsSuppliers = findSuppliersByBrandsAndCompBuyer(brands,id,suppliers);
            //查询采购计划号最大编号
            String maxCode= purchasePlanRepository.findMaxCode(id,operatorCode, LocalDate.now());
            if(maxCode==null){
                maxCode="01";
            }
            //计划编码
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate data=LocalDate.now();
            String planCode = "JH-"+operatorCode+"-"+dtf.format(data)+"-"+maxCode;
            List<PurchasePlanProduct> purchasePlanProducts = new ArrayList<>();
            //保存
            temporaryPlans.forEach(temporaryPlan -> {
                List<PurchasePlanProductSupplier> purchasePlanProductSalers = new ArrayList<>();
                List<Company> companies = brandsSuppliers.get(temporaryPlan.getBrandCode());
                AtomicInteger i = new AtomicInteger();
                if(companies!=null){
                    companies.forEach(company -> {
                        i.getAndIncrement();
                        PurchasePlanProductSupplier productSaler = PurchasePlanProductSupplier.builder()
                            .purchasePlanProductSupplierId(PurchasePlanProductSupplierId.builder()
                                .productId(temporaryPlan.getTemporaryPlanId().getProductId())
                                .planCode(planCode)
                                .dcCompId(id)
                                .salerCode(company.getCode())
                                .build())
                            .serial(i.get())
                            .salerName(company.getShortNameInCN())
                            .demand(BigDecimal.ZERO)
                            .deliverNum(BigDecimal.valueOf(5))
                            .tranNum(BigDecimal.valueOf(5))
                            .build();
                        purchasePlanProductSalers.add(productSaler);
                    });
                }
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
                    .deliverNum(BigDecimal.valueOf(5))
                    .beforeSalesPrice(BigDecimal.valueOf(5))
                    .inquiryNum(BigDecimal.ZERO)
                    .safetyStock(BigDecimal.valueOf(5))
                    .tranNum(BigDecimal.valueOf(5))
                    .createdAt(temporaryPlan.getCreatedAt())
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
            temporaryPlanRepository.deleteAll(temporaryPlans);
            result.put("code", 200);
            result.put("message","开始计划成功！");
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("code", 500);
            result.put("message",e.getMessage());
            return result;
        }
    }

    /**
     * 保存空的采购计划
     * @param id 单位id
     * @param operatorCode 操作员编号
     * @return 返回
     */
    @Transactional
    public Map<String, Object> savaEmptyPurchasePlan(String id, String operatorCode){
        Map<String, Object> result = new HashMap<>();
        try{
            //计划编码
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate data=LocalDate.now();
            String planCode = "JH-"+operatorCode+"-"+dtf.format(data)+"-"+"01";
            List<PurchasePlanProduct> purchasePlanProducts = new ArrayList<>();
            //保存
            PurchasePlan purchasePlan = PurchasePlan.builder()
                .purchasePlanId(PurchasePlanId.builder()
                    .planCode(planCode)
                    .dcCompId(id)
                    .build())
                .createdBy(operatorCode)
                .createdAt(LocalDate.now())
                .source(DemandSource.NEW_DEMAND)
                .product(purchasePlanProducts)
                .build();
            purchasePlanRepository.save(purchasePlan);
            result.put("code", 200);
            result.put("message","创建计划成功！");
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("code", 500);
            result.put("message",e.getMessage());
            return result;
        }
    }

    /**
     * 根据品牌列表，本单位id，供应商列表查询 每个品牌有哪些供应商
     * @param brands 品牌编码列表
     * @param compBuyer 买方编码
     * @param suppliers 供应商列表
     * @return 返回 品牌包含供应商列表的Map
     */
    public Map<String,List<Company>> findSuppliersByBrandsAndCompBuyer(List<String> brands,String compBuyer,List<String> suppliers){
        //查询这几个牌子的供应商有哪些
        List<CompTradBrand> compTradBrands= compTradBrandRepository.findCompTradBrandByCompTradBrandId_BrandCodeInAndCompTradBrandId_CompBuyerOrderBySortDesc(brands,compBuyer);
        Map<String,List<Company>>  NoIncludeCompMap = new HashMap<>();
        Map<String,List<Company>>  IncludeCompMap = new HashMap<>();
        List<CompTradBrand> compTradIncludeCompList= compTradBrands.stream()
            .filter(compTradBrand -> suppliers.contains(compTradBrand.getCompany().getCode())).toList();
        List<CompTradBrand> compTradNoIncludeCompList= compTradBrands.stream()
            .filter(compTradBrand -> !suppliers.contains(compTradBrand.getCompany().getCode())).toList();
        compTradNoIncludeCompList
            .forEach(compTradBrand -> {
                List<Company> list = NoIncludeCompMap.get(compTradBrand.getCompTradBrandId().getBrandCode());
                if(list==null)
                    list = new ArrayList<>();
                list.add(compTradBrand.getCompany());
                NoIncludeCompMap.put(compTradBrand.getCompTradBrandId().getBrandCode(),list);
            });
        compTradIncludeCompList
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
            if(nolist==null)
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
     * 验证是否有未完成的采购计划
     * @param id 单位编号 operateorCode 操作员编号
     * @return 返回采购计划信息
     */
    public Optional<VBaseResponse> verification(String id, String operateorCode){
        var purchasePlan = findPurchasePlanByCode(id, operateorCode);
        if(purchasePlan.isEmpty())
            return Optional.of(
                VBaseResponse.builder()
                    .code(404)
                    .message("不存在未完成的采购计划")
                    .build()
            );
        return Optional.of(
            VBaseResponse.builder()
                .code(200)
                .message("存在未完成的计划，请前往采购计划详情完成计划，在开始新的计划！")
                .build()
        );
    }

    /**
     * 查询采购计划
     * @param operateorCode 操作员编号 id 单位id
     * @return 返回采购计划信息
     */
    public Optional<PurchasePlan> findPurchasePlanByCode(String id, String operateorCode){
        return  purchasePlanRepository.findFirstByPurchasePlanId_DcCompIdAndCreatedBy(id, operateorCode);
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
    public Map<String, Object> modifyPlanSupplier(String id, String planCode, String productId, String oldSupplier, String newSupplier){
          Map<String, Object> map= new HashMap<>();
          int serial;
        try{
            Optional<Company> supplier =companyRepository.findById(newSupplier);
            if(supplier.isEmpty()) {
                map.put("code", 404);
                map.put("flag", false);
                map.put("message", "更换失败，更换的供应商不存在");
                return map;
            }
            if(!oldSupplier.isEmpty()) {
                Optional<PurchasePlanProductSupplier> supplier1 = purchasePlanProductSupplierRepository.findById(
                    PurchasePlanProductSupplierId.builder()
                    .productId(productId)
                    .dcCompId(id)
                    .planCode(planCode)
                    .salerCode(oldSupplier)
                    .build());
                if(supplier1.isEmpty()) {
                    map.put("code", 404);
                    map.put("flag", false);
                    map.put("message", "更换失败，被更换的供应商不存在");
                    return map;
                }
                serial=supplier1.get().getSerial();
                purchasePlanProductSupplierRepository.deleteById(
                    PurchasePlanProductSupplierId.builder()
                        .productId(productId)
                        .dcCompId(id)
                        .planCode(planCode)
                        .salerCode(oldSupplier)
                        .build()
                );
            }else{
                serial =   purchasePlanProductSupplierRepository.findMaxSerial(productId,planCode,id);
            }
            purchasePlanProductSupplierRepository.save(
                PurchasePlanProductSupplier.builder()
                .purchasePlanProductSupplierId(
                    PurchasePlanProductSupplierId.builder()
                    .productId(productId)
                    .dcCompId(id)
                    .planCode(planCode)
                    .salerCode(newSupplier)
                    .build()
                )
                .serial(serial)
                .salerName(supplier.get().getShortNameInCN())
                .demand(BigDecimal.ZERO)
                .tranNum(BigDecimal.valueOf(5))
                .deliverNum(BigDecimal.valueOf(5))
                .build()
            );
            map.put("flag",true);
            map.put("code",200);
            map.put("message","供应商替换成功！");
            return   map;
        }catch (Exception e){
            e.printStackTrace();
            map.put("flag",false);
            map.put("code",500);
            map.put("message","供应商替换失败！");
            return map;
        }

    }

    /**
     * 修改询数
     * @param id  单位id
     * @param demands 询数
     * @return 返回布尔（true表示修改成功）
     */
    @Transactional
    public boolean modifyPurchasePlanForSeveral(String id, VPlanDemandRequest demands){
        try{
            purchasePlanProductSupplierRepository.updateDemandById(demands.getDemand(),PurchasePlanProductSupplierId.builder()
                    .productId(demands.getProductId())
                    .dcCompId(id)
                    .salerCode(demands.getSupplierCode())
                    .planCode(demands.getPlanCode())
                    .build());

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    /**
     * 修改需求数量
     * @param id 单位id
     * @param demands 产品需求
     * @return 返回布尔值（true表示修改成功）
     */
    @Transactional
    public boolean modifyPurchasePlanDemand(String id, VPlanDemandRequest demands){
        try{
            purchasePlanProductRepository.updateDemandById(demands.getDemand(),PurchasePlanProductId.builder()
                    .productId(demands.getProductId())
                    .dcCompId(id)
                    .planCode(demands.getPlanCode())
                    .build());

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    /**
     * 添加产品
     * @param id 单位id
     * @param productId 产品id
     * @param planCode 计划号
     * @param demand 需求数
     * @return 返回布尔值（true表示修改成功， false表示修改失败）
     */
    @Transactional
    public  boolean savePlanProduct(String id,String productId,String planCode,BigDecimal demand){
        try{
            Optional<Product> product = productRepository.findById(productId);
            List<String> brands = new ArrayList<>();
            brands.add(product.get().getBrandCode());
           List<Company> suppliers =  findSuppliersByBrandsAndCompBuyer(brands,id,new ArrayList<>()).get(product.get().getBrandCode());
           List<PurchasePlanProductSupplier> purchasePlanProductSuppliers = new ArrayList<>();
           AtomicInteger i = new AtomicInteger();
           suppliers.forEach(company -> {
                   i.getAndIncrement();
                   PurchasePlanProductSupplier productSaler = PurchasePlanProductSupplier.builder()
                       .purchasePlanProductSupplierId(PurchasePlanProductSupplierId.builder()
                           .productId(productId)
                           .planCode(planCode)
                           .dcCompId(id)
                           .salerCode(company.getCode())
                           .build())
                       .serial(i.get())
                       .salerName(company.getShortNameInCN())
                       .demand(BigDecimal.ZERO)
                       .deliverNum(BigDecimal.valueOf(5))
                       .tranNum(BigDecimal.valueOf(5))
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
                .deliverNum(BigDecimal.valueOf(5))
                .beforeSalesPrice(BigDecimal.ZERO)
                .inquiryNum(BigDecimal.ZERO)
                .safetyStock(BigDecimal.valueOf(5))
                .tranNum(BigDecimal.valueOf(5))
                .createdAt(LocalDateTime.now())
                .build();
            purchasePlanProductRepository.save(purchasePlanProduct);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除产品
     * @param id 单位id
     * @param productId 产品id列表
     * @param planCode 采购计划号
     * @return 返回布尔值（true表示删除成功）
     */
    @Transactional
    public  boolean deletePlanProduct(String id,List<String> productId,String planCode){
        try{
            purchasePlanProductSupplierRepository.deleteSupplier(id,planCode,productId);
            List<PurchasePlanProductId> list = new ArrayList<>();
            productId.forEach(s ->
                list.add(PurchasePlanProductId.builder()
                    .productId(s)
                    .dcCompId(id)
                    .planCode(planCode)
                    .build()
                )
            );
            purchasePlanProductRepository.deleteAllById(list);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 撤销采购计划
     * @param id 单位id
     * @param planCode 采购计划号
     * @return 返回布尔值（true表示删除成功）
     */
    @Transactional
    public  boolean deletePurchasePlan(String id,String planCode){
        try{
            purchasePlanProductSupplierRepository.deleteSupplier(id,planCode);
            purchasePlanProductRepository.deleteProduct(id, planCode);
            purchasePlanRepository.deletePurchasePlan(PurchasePlanId.builder()
                .dcCompId(id)
                .planCode(planCode)
                .build());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取采购计划中的需求量不为0的供应商列表
     * @param planCode 采购计划
     * @param id 单位id
     * @return 供应商列表
     */
    public List<TCompanyBaseInformation> findSuppliersByPlanCode(String planCode, String id){
        return purchasePlanProductSupplierRepository.findDistinctSuppliers(id,planCode)
            .stream().map(stringStringMap -> TCompanyBaseInformation.builder()
                 .code(stringStringMap.get("saler_code"))
                 .shortName(stringStringMap.get("saler_name"))
                 .build())
            .collect(Collectors.toList());
    }

    /**
     * 保存询价单
     * @param planCode 采购计划
     * @param id 单位id
     * @param compName 单位名称
     * @param operatorCode 操作员编码
     * @return 返回成功信息
     */
    @CacheEvict(value="inquiry_List;1800", key="#id+'_'+#operatorCode")
    @Transactional
    public Map<String,Object> savePurchaseInquiry(String planCode, String id,String compName, String operatorCode,String operatorName){
        Map<String,Object> resultMap = new HashMap<>();
        try{

            Map<String,List<InquiryRecord>> supplierInquiryRecordMap = new HashMap<>();
            List<String> suppliers = new ArrayList<>();
            List<Inquiry> inquiries = new ArrayList<>();
            //查找采购计划
            Optional<PurchasePlan> purchasePlan = purchasePlanRepository.findById(
                PurchasePlanId.builder()
                    .dcCompId(id)
                    .planCode(planCode)
                    .build()
            );
            if(purchasePlan.isEmpty()) {
                resultMap.put("code", 404);
                resultMap.put("message", "找不到该数据");
               return resultMap;
            }
            //查出货物税率
           Optional<VatRates> goods= vatRatesRepository.findByTypeAndDeflagAndUseCountry(VatRateType.GOODS,Whether.YES,"001");
            //查出服务税率
            Optional<VatRates> service=vatRatesRepository.findByTypeAndDeflagAndUseCountry(VatRateType.SERVICE,Whether.YES,"001");
            //查出向每个供应商询价商品且询价数量>0的有哪些
            purchasePlan.get().getProduct().forEach(purchasePlanProduct -> purchasePlanProduct.getSalers().forEach(supplier -> {
                if(supplier.getDemand().intValue()>0) {
                    InquiryRecord record = InquiryRecord.builder()
                        .productId(purchasePlanProduct.getPurchasePlanProductId().getProductId())
                        .productCode(purchasePlanProduct.getProductCode())
                        .productDescription(purchasePlanProduct.getDescribe())
                        .brandCode(purchasePlanProduct.getBrandCode())
                        .brand(purchasePlanProduct.getBrand())
                        .amount(supplier.getDemand())
                        .charge_unit(purchasePlanProduct.getChargeUnit())
                        .type(VatRateType.GOODS)
                        .vatRate(goods.isPresent()?goods.get().getRate():BigDecimal.ZERO)
                        .build();
                    List<InquiryRecord> list = supplierInquiryRecordMap.get(supplier.getPurchasePlanProductSupplierId().getSalerCode());
                    if (list==null) {
                        list = new ArrayList<>();
                        suppliers.add(supplier.getPurchasePlanProductSupplierId().getSalerCode());
                    }
                    list.add(record);
                    supplierInquiryRecordMap.put(supplier.getPurchasePlanProductSupplierId().getSalerCode(),list);
                }
            }));
            //查询每个供应商税模式对本单位设置的税模式
            List<CompTrad>compTades=compTradeRepository.findSuppliersByCompTradIdCompBuyerAndState(id, Trade.TRANSACTION);
            Map<String,CompTrad> compTradMap = new HashMap<>();
            compTades.forEach(compTrad -> compTradMap.put(compTrad.getCompTradId().getCompSaler(),compTrad));
            //查询询价单最大编号
             String maxCode = inquiryRepository.findMaxCode(id, operatorCode);
            if(maxCode ==null)
                maxCode ="01";
            AtomicInteger max = new AtomicInteger(Integer.parseInt(maxCode));
            //对每个供应商生成询价单
            companyRepository.findAllById(suppliers).forEach(company -> {
                if(!company.getCode().equals("1001")){
                    String mCode = ("0000"+max.get()).substring(("0000"+max.get()).length()-3);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                    LocalDate data=LocalDate.now();
                    //uuid
                    UUID uuid = UUID.randomUUID();
                    String inquiryId = "XJ-"+id+"-0"+operatorCode+"-"+uuid.toString().substring(0,8);
                    String inquiryCode ="XJ-"+operatorCode+"-"+company.getCode()+"-"+dtf.format(data)+"-"+mCode;
                    List<InquiryRecord> records = supplierInquiryRecordMap.get(company.getCode());
                    AtomicInteger code = new AtomicInteger();
                    records.forEach(inquiryRecord -> {
                        code.getAndIncrement();
                        inquiryRecord.setInquiryRecordId(InquiryRecordId.builder().code(code.get()).inquiryId(inquiryId).build());
                        inquiryRecord.setCreatedAt(LocalDateTime.now());
                    });
                    inquiries.add(Inquiry.builder()
                        .records(records)
                        .id(inquiryId)
                        .code(inquiryCode)
                        .createdByComp(id)
                        .type(InquiryType.INQUIRY_LIST)
                        .createdBy(operatorCode)
                        .buyerComp(id)
                        .buyerCompName(compName)
                        .buyerContactName(operatorName)
                        .salerComp(company.getCode())
                        .salerCompName(company.getNameInCN())
                        .createdAt(LocalDateTime.now())
                        .salesOrderCode(purchasePlan.get().getSalesCode())
                        .state(InquiryState.UN_FINISHED)
                        .offerMode(compTradMap.get(company.getCode())==null? TaxMode.UNTAXED :compTradMap.get(company.getCode()).getTaxModel())
                        .createdAt(LocalDateTime.now())
                        .build());
                    max.getAndIncrement();
                }
           });
            //保存询价单
           inquiryRepository.saveAll(inquiries);
           //删除计划
            purchasePlanProductSupplierRepository.deleteSupplier(id,planCode);
            purchasePlanProductRepository.deleteProduct(id, planCode);
            purchasePlanRepository.deletePurchasePlan(PurchasePlanId.builder()
                .dcCompId(id)
                .planCode(planCode)
                .build());
            resultMap.put("code",200);
            resultMap.put("message","生成询价单成功");
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("code",500);
            resultMap.put("message","生成询价单失败");
            return resultMap;
        }
    }
}
