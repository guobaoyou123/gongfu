package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.dto.TTemporaryPlan;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.*;
import com.linzhi.gongfu.mapper.PurchasePlanMapper;
import com.linzhi.gongfu.mapper.TemporaryPlanMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.vo.VBaseResponse;
import com.linzhi.gongfu.vo.VPlanDemandRequest;
import com.linzhi.gongfu.vo.VPurchasePlanResponse;
import com.linzhi.gongfu.vo.VTemporaryPlanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
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
    private final PurchasePlanMapper purchasePlanMapper;
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
        return  temporaryPlanRepository.findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedBy(temporaryPlanId.getDcCompId(),temporaryPlanId.getCreatedBy()).stream()
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
    public Map saveTemporaryPlan(List<VTemporaryPlanRequest> product, String id, String operatorCode){
        Map resultMap = new HashMap<>();
        List<String> resultList = new ArrayList<>();
        final String[] message = {""};
        List<String> proCodeList = new ArrayList<>();
        Map<String, Product> productMap = new HashMap<>();
        try{
            product.stream().forEach(p ->
                proCodeList.add(p.getProductId())
            );
            List<Product> products=productRepository.findProductByIdIn(proCodeList);
            products.stream().forEach(product1 ->
                productMap.put(product1.getId(),product1)
            );
            //判断是否有已存在于计划列表中的产品
            var list =temporaryPlanRepository.findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedBy(id,operatorCode).stream()
                .filter(temporaryPlan -> proCodeList.contains(temporaryPlan.getTemporaryPlanId().getProductId()))
                .collect(Collectors.toList());
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
            resultMap.put("flag",true);
            resultMap.put("message","加入计划成功");
            if(resultList.size()>0)
                resultMap.put("message","加入计划成功，产品编号为："+message[0]+"以存在于计划表中，并对需求数进行累加");
        }catch (Exception e){
            resultMap.put("flag",false);
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
    @Transactional
    public boolean deleteTemporaryPlan( List<String> product, String id, String operatorCode){
        try{
            List<TemporaryPlanId> list = new ArrayList<>();
            product.stream().forEach(pr -> {
                list.add(TemporaryPlanId.builder().dcCompId(id).productId(pr).createdBy(operatorCode).build());
            });
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
    public Map savaPurchasePlan(List<String> products,List<String> suppliers,String id, String operatorCode){
           Map result = new HashMap();
        try{
            //查出所选计划
            List<TemporaryPlan> temporaryPlans = temporaryPlanRepository.findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedByAndTemporaryPlanId_ProductIdIn(id,operatorCode,products);
            if(temporaryPlans.size()==0) {
                result.put("flag", false);
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
            String planCode = "HJ-"+operatorCode+"-"+dtf.format(data)+"-"+maxCode;
            List<PurchasePlanProduct> purchasePlanProducts = new ArrayList<>();
            //保存
            temporaryPlans.forEach(temporaryPlan -> {
                List<PurchasePlanProductSupplier> purchasePlanProductSalers = new ArrayList<>();
                List<Company> companies = brandsSuppliers.get(temporaryPlan.getBrandCode());
                if(companies!=null){
                    companies.forEach(company -> {
                        PurchasePlanProductSupplier productSaler = PurchasePlanProductSupplier.builder()
                            .purchasePlanProductSupplierId(PurchasePlanProductSupplierId.builder()
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
            temporaryPlanRepository.deleteAll(temporaryPlans);
            result.put("flag",true);
            result.put("planCode",planCode);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            result.put("flag",false);
            result.put("message",e.getMessage());
            return result;
        }

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
     * 验证是否有未完成的采购计划
     * @param id 单位编号 operateorCode 操作员编号
     * @return 返回采购计划信息
     */
    public Optional<VBaseResponse> verification(String id, String operateorCode){
        var list = purchasePlanRepository.findPurchasePlanByPurchasePlanId_DcCompIdAndAndCreatedBy(id, operateorCode);
        if(list.size()>0)
            return Optional.of(
                VBaseResponse.builder()
                    .code(200)
                    .message("存在未完成的计划，请前往采购计划详情完成计划，在开始新的计划！")
                    .build()
            );
        return Optional.of(
            VBaseResponse.builder()
                .code(404)
                .message("不存在未完成的采购计划")
                .build()
        );
    }

    /**
     * 根据计划号查询采购计划
     * @param operateorCode 操作员编号 id 单位id
     * @return 返回采购计划信息
     */
    public Optional<VPurchasePlanResponse> findPurchasePlanByCode(String id, String operateorCode){
        var list = purchasePlanRepository.findPurchasePlanByPurchasePlanId_DcCompIdAndAndCreatedBy(id, operateorCode);
        if(list.size()==0)
            return  null;
        return Optional.of(list.get(0))
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
                .purchasePlanProductSupplierId(PurchasePlanProductSupplierId.builder()
                    .productId(productId)
                    .dcCompId(id)
                    .planCode(planCode)
                    .salerCode(newSupplier)
                    .build())
                .salerName(supplier.get().getShortNameInCN())
                .demand(BigDecimal.ZERO)
                .tranNum(BigDecimal.ZERO)
                .deliverNum(BigDecimal.ZERO)
                .build());
            return   true;
        }catch (Exception e){
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            return  false;
        }
    }

    /**
     * 添加产品
     * @param id
     * @param productId
     * @param planCode
     * @param demand
     * @return
     */
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
                       .purchasePlanProductSupplierId(PurchasePlanProductSupplierId.builder()
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
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 删除产品
     * @param id
     * @param productId 产品id列表
     * @param planCode 采购计划号
     * @return
     */
    @Transactional
    public  boolean deletePlanProduct(String id,List<String> productId,String planCode){
        try{
            purchasePlanProductSupplierRepository.deleteSupplier(id,planCode,productId);
            List<PurchasePlanProductId> list = new ArrayList<>();
            productId.forEach(s -> {
                list.add(PurchasePlanProductId.builder()
                    .productId(s)
                    .dcCompId(id)
                    .planCode(planCode)
                    .build()) ;
            });
            purchasePlanProductRepository.deleteAllById(list);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 撤销采购计划
     * @param id
     * @param planCode 采购计划号
     * @return
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
     * @param planCode
     * @param id
     * @return 供应商列表
     */
    public List<TCompanyBaseInformation> findSuppliersByPlanCode(String planCode, String id){
        return purchasePlanProductSupplierRepository.findDistinctSuppliers(id,planCode)
            .stream().map(stringStringMap -> {
               return TCompanyBaseInformation.builder()
                    .code(stringStringMap.get("saler_code"))
                    .shortName(stringStringMap.get("saler_name"))
                    .build();
            })
            .collect(Collectors.toList());
    }

    /**
     * 保存询价单
     * @param planCode
     * @param id
     * @param compName
     * @param operatorCode
     * @return
     */
    @CachePut(value="inquiry_List;1800", key="T(String).valueOf(#id).concat(#operatorCode)")
    @Transactional
    public VBaseResponse savePurchaseInquiry(String planCode, String id,String compName, String operatorCode){
        try{
            Map<String,List<InquiryRecord>> supplierInquerRecordMap = new HashMap<>();
            List<String> suppliers = new ArrayList<>();
            List<Inquiry> inquiries = new ArrayList<>();
            //查找采购计划
            Optional<PurchasePlan> purchasePlan = purchasePlanRepository.findById(
                PurchasePlanId.builder()
                    .dcCompId(id)
                    .planCode(planCode)
                    .build()
            );
            if(purchasePlan.isEmpty())
                return VBaseResponse.builder()
                    .code(404)
                    .message("数据不存在")
                    .build();
            //查出货物税率
           Optional<VatRates> goods= vatRatesRepository.findByTypeAndDeflagAndUseCountry(VatRateType.GOODS,Whether.YES,"001");
            //查出服务税率
            Optional<VatRates> service=vatRatesRepository.findByTypeAndDeflagAndUseCountry(VatRateType.SERVICE,Whether.YES,"001");
            //查出向每个供应商询价商品且询价数量>o的有哪些
            purchasePlan.get().getProduct().forEach(purchasePlanProduct -> {
                purchasePlanProduct.getSalers().forEach(supplier -> {
                    if(supplier.getDemand().intValue()>0) {
                        InquiryRecord record = InquiryRecord.builder()
                            .productId(purchasePlanProduct.getPurchasePlanProductId().getProductId())
                            .productCode(purchasePlanProduct.getProductCode())
                            .productDescription(purchasePlanProduct.getDescribe())
                            .compBuyer(id)
                            .compSaler(supplier.getPurchasePlanProductSupplierId().getSalerCode())
                            .brandCode(purchasePlanProduct.getBrandCode())
                            .brand(purchasePlanProduct.getBrand())
                            .amount(supplier.getDemand())
                            .charge_unit(purchasePlanProduct.getChargeUnit())
                            .type(VatRateType.GOODS)
                            .vatRate(goods.get().getRate())
                            .build();
                        List<InquiryRecord> list = supplierInquerRecordMap.get(supplier.getPurchasePlanProductSupplierId().getSalerCode());
                        if (list==null) {
                            list = new ArrayList<>();
                            suppliers.add(supplier.getPurchasePlanProductSupplierId().getSalerCode());
                        }
                        list.add(record);
                        supplierInquerRecordMap.put(supplier.getPurchasePlanProductSupplierId().getSalerCode(),list);
                    }
                });
            });
            //查询每个供应商税模式对本单位设置的税模式
            List<CompTrad>compTades=compTradeRepository.findSuppliersByCompTradIdCompBuyerAndState(id, Trade.TRANSACTION);
            Map<String,CompTrad> compTradMap = new HashMap<>();
            compTades.forEach(compTrad -> {
                compTradMap.put(compTrad.getCompTradId().getCompSaler(),compTrad);
            });
            //查询询价单最大编号
             String maxCode = inquiryRepository.findMaxCode(id, operatorCode);
            if(maxCode ==null)
                maxCode ="01";
            AtomicInteger max = new AtomicInteger(Integer.valueOf(maxCode));
            //对每个供应商生成询价单
            companyRepository.findAllById(suppliers).forEach(company -> {
                if(!company.getCode().equals("1001")){
                    String mCode = ("0000"+max.get()).substring(("0000"+max.get()).length()-2);
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                    LocalDate data=LocalDate.now();
                    String inquiryId = "XJ-"+id+"-"+operatorCode+"-"+company.getCode()+"-"+dtf.format(data)+"-"+mCode;
                    String inquiryCode ="XJ-"+operatorCode+"-"+company.getCode()+"-"+dtf.format(data)+"-"+mCode;
                    List<InquiryRecord> records = supplierInquerRecordMap.get(company.getCode());
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
                        .buyerCreatedBy(operatorCode)
                        .compBuyer(id)
                        .compBuyerName(compName)
                        .compSaler(company.getCode())
                        .compSalerName(company.getNameInCN())
                        .createdAt(LocalDateTime.now())
                        .salesOrderCode(purchasePlan.get().getSalesCode())
                        .state(Whether.NO)
                        .taxModel(compTradMap.get(company.getCode())==null? TaxModel.UNTAXED :compTradMap.get(company.getCode()).getTaxModel())
                        .vatProductRate(goods.get().getRate())
                        .vatServiceRate(service.get().getRate())
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
            return VBaseResponse.builder()
                .code(200)
                .message("生成询价单成功")
                .build();
        }catch (Exception e){
            e.printStackTrace();
            return VBaseResponse.builder()
                .code(500)
                .message("生成询价单失败")
                .build();
        }
    }
}
