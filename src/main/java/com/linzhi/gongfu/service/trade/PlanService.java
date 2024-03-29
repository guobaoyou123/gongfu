package com.linzhi.gongfu.service.trade;

import com.linzhi.gongfu.dto.TCompanyBaseInformation;
import com.linzhi.gongfu.dto.TTemporaryPlan;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.DemandSource;
import com.linzhi.gongfu.mapper.TemporaryPlanMapper;
import com.linzhi.gongfu.repository.*;
import com.linzhi.gongfu.repository.trade.*;
import com.linzhi.gongfu.vo.VBaseResponse;
import com.linzhi.gongfu.vo.trade.VPlanDemandRequest;
import com.linzhi.gongfu.vo.trade.VTemporaryPlanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private final CompTradeBrandRepository compTradBrandRepository;
    private final PurchasePlanRepository purchasePlanRepository;
    private final PurchasePlanProductSupplierRepository purchasePlanProductSupplierRepository;
    private final CompanyRepository companyRepository;
    private final PurchasePlanProductRepository purchasePlanProductRepository;
    private final PreferenceSupplierRepository preferenceSupplierRepository;
    /**
     * 根据单位id、操作员编码查询该操作员的临时采购计划列表
     *
     * @param temporaryPlanId 单位id 操作员编码
     * @return 临时采购计划列表信息
     */
    public List<TTemporaryPlan> listTemporaryPlansByOperator(TemporaryPlanId temporaryPlanId) {
        return temporaryPlanRepository.findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedByOrderByCreatedAt(
                temporaryPlanId.getDcCompId(),
                temporaryPlanId.getCreatedBy()).stream()
            .map(temporaryPlanMapper::toTemporaryPlan)
            .collect(Collectors.toList());
    }

    /**
     * 保存临时采购计划
     *
     * @param product      产品列表
     * @param id           单位id
     * @param operatorCode 操作员编码
     */
    @Transactional
    public Map<Object, Object> saveTemporaryPlan(List<VTemporaryPlanRequest> product, String id, String operatorCode) {
        Map<Object, Object> resultMap = new HashMap<>();
        List<String> resultList = new ArrayList<>();
        final String[] message = {""};
        List<String> proCodeList = new ArrayList<>();
        Map<String, Product> productMap = new HashMap<>();
        try {
            product.forEach(p -> proCodeList.add(p.getProductId()));
            List<Product> products = productRepository.findProductByIdIn(proCodeList);
            products.forEach(product1 -> productMap.put(product1.getId(), product1));
            //判断是否有已存在于计划列表中的产品
            var list = temporaryPlanRepository.findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedByOrderByCreatedAt(id, operatorCode).stream()
                .filter(temporaryPlan -> proCodeList.contains(
                        temporaryPlan.getTemporaryPlanId()
                            .getProductId()
                    )
                )
                .toList();
            Map<String, TemporaryPlan> temporaryPlanMap = new HashMap<>();
            list.forEach(temporaryPlan -> {
                resultList.add(temporaryPlan.getProductCode());
                temporaryPlanMap.put(temporaryPlan.getTemporaryPlanId().getProductId(), temporaryPlan);
            });
            //保存产品
            List<TemporaryPlan> saveList = new ArrayList<>();
            product.forEach(pr -> {
                Product p = productMap.get(pr.getProductId());
                TemporaryPlan temporaryPlan = temporaryPlanMap.get(pr.getProductId());
                if (temporaryPlan == null) {
                    temporaryPlan = TemporaryPlan.builder()
                        .temporaryPlanId(TemporaryPlanId.builder().dcCompId(id).productId(p.getId()).createdBy(operatorCode).build())
                        .productCode(p.getCode())
                        .chargeUnit(p.getChargeUnit())
                        .brand(p.getBrand())
                        .brandCode(p.getBrandCode())
                        .describe(p.getDescribe())
                        .demand(pr.getDemand())
                        .facePrice(p.getFacePrice())
                        .build();
                } else {
                    message[0] = message[0] + temporaryPlan.getProductCode() + ",";
                    temporaryPlan.setDemand(temporaryPlan.getDemand().add(pr.getDemand()));
                }
                temporaryPlan.setCreatedAt(LocalDateTime.now());
                saveList.add(temporaryPlan);
            });
            temporaryPlanRepository.saveAll(saveList);
            resultMap.put("code", 200);
            resultMap.put("message", "加入计划成功");
            if (resultList.size() > 0)
                resultMap.put("message", "加入计划成功，产品编号为：" + message[0] + "以存在于计划表中，并对需求数进行累加");
        } catch (Exception e) {
            resultMap.put("code", 500);
            resultMap.put("message", "加入计划失败");
            return resultMap;
        }
        return resultMap;
    }

    /**
     * 修改计划需求
     *
     * @param product      计划产品列表
     * @param id           单位id
     * @param operatorCode 操作员编码
     */
    @Transactional
    public void modifyTemporaryPlan(List<VTemporaryPlanRequest> product, String id, String operatorCode) {
        product.forEach(pr -> temporaryPlanRepository.updateNameById(pr.getDemand(), TemporaryPlanId.builder().dcCompId(id).productId(pr.getProductId()).createdBy(operatorCode).build()));
    }

    /**
     * 删除计划需求
     *
     * @param product      产品id列表
     * @param id           单位id
     * @param operatorCode 操作员编码
     */
    @Transactional
    public boolean removeTemporaryPlan(List<String> product, String id, String operatorCode) {
        try {
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 保存采购计划
     *
     * @param products     产品id列表
     * @param suppliers    供应商编码列表
     * @param id           单位id
     * @param operatorCode 操作员编号
     * @return 返回采购计划号
     */
    @Transactional
    public Map<String, Object> savePurchasePlan(List<String> products,
                                                List<String> suppliers,
                                                String id,
                                                String operatorCode
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            //查出所选计划
            List<TemporaryPlan> temporaryPlans = temporaryPlanRepository.findAllByTemporaryPlanId_DcCompIdAndTemporaryPlanId_CreatedByAndTemporaryPlanId_ProductIdInOrderByCreatedAt(id, operatorCode, products);
            if (temporaryPlans.size() == 0) {
                result.put("code", 404);
                result.put("message", "数据不存在");
                return result;
            }
            //查看有几个品牌,每个品牌所属供应商有哪些
            List<String> brands = temporaryPlans.stream()
                .map(TemporaryPlan::getBrandCode)
                .distinct()
                .collect(Collectors.toList());
            Map<String, List<Company>> brandsSuppliers = findSuppliersByBrandsAndCompBuyer(brands, id, suppliers);
            //查询采购计划号最大编号
            String maxCode = purchasePlanRepository.findMaxCode(id, operatorCode, LocalDate.now());
            if (maxCode == null) {
                maxCode = "01";
            }
            //计划编码
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate data = LocalDate.now();
            String planCode = "JH-" + operatorCode + "-" + dtf.format(data) + "-" + maxCode;
            List<PurchasePlanProduct> purchasePlanProducts = new ArrayList<>();
            //保存
            temporaryPlans.forEach(temporaryPlan -> {
                List<PurchasePlanProductSupplier> purchasePlanProductSuppliers = new ArrayList<>();
                List<Company> companies = brandsSuppliers.get(temporaryPlan.getBrandCode());
                AtomicInteger i = new AtomicInteger();
                if (companies != null) {
                    companies.forEach(company -> {
                        i.getAndIncrement();
                        PurchasePlanProductSupplier productSupplier = PurchasePlanProductSupplier.builder()
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
                        purchasePlanProductSuppliers.add(productSupplier);
                    });
                }
                PurchasePlanProduct product = PurchasePlanProduct.builder()
                    .purchasePlanProductId(PurchasePlanProductId.builder()
                        .planCode(planCode)
                        .dcCompId(id)
                        .productId(temporaryPlan.getTemporaryPlanId().getProductId()).build())
                    .productCode(temporaryPlan.getProductCode())
                    .brandCode(temporaryPlan.getBrandCode())
                    .brand(temporaryPlan.getBrand())
                    .chargeUnit(temporaryPlan.getChargeUnit())
                    .describe(temporaryPlan.getDescribe())
                    .facePrice(temporaryPlan.getFacePrice())
                    .demand(temporaryPlan.getDemand())
                    .salers(purchasePlanProductSuppliers)
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
            //删除临时计划
            temporaryPlanRepository.deleteAll(temporaryPlans);
            result.put("code", 200);
            result.put("message", "开始计划成功！");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", e.getMessage());
            return result;
        }
    }

    /**
     * 保存空的采购计划
     *
     * @param id           单位id
     * @param operatorCode 操作员编号
     * @return 返回
     */
    @Transactional
    public Map<String, Object> savaEmptyPurchasePlan(String id, String operatorCode) {
        Map<String, Object> result = new HashMap<>();
        try {
            //计划编码
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate data = LocalDate.now();
            String planCode = "JH-" + operatorCode + "-" + dtf.format(data) + "-" + "01";
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
            result.put("message", "创建计划成功！");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", e.getMessage());
            return result;
        }
    }

    /**
     * 根据品牌列表，本单位id，供应商列表查询 每个品牌有哪些供应商
     *
     * @param brands    品牌编码列表
     * @param compBuyer 买方编码
     * @param suppliers 供应商列表
     * @return 返回 品牌包含供应商列表的Map
     */
    public Map<String, List<Company>> findSuppliersByBrandsAndCompBuyer(List<String> brands, String compBuyer, List<String> suppliers) {
        //查询这几个牌子的供应商有哪些
        List<CompTradeBrand> compTradBrands = compTradBrandRepository.findCompTradeBrandByCompTradeBrandId_BrandCodeInAndCompTradeBrandId_CompBuyerAndCompany_StateOrderBySortDesc(brands, compBuyer, Availability.ENABLED);
        Map<String,List<Company>> compTradBrandsrMap = new HashMap<>();

        //查询这个几个品牌的优选供应商有哪些
        List<PreferenceSupplier> preferredSupplier = preferenceSupplierRepository.findByPreferenceSupplierId_CompCodeAndPreferenceSupplierId_BrandCodeInOrderBySortAsc(compBuyer,brands);
        Map<String,List<PreferenceSupplier>> preferredSupplierMap = preferredSupplier.stream().collect(Collectors.groupingBy(preferenceSupplier->preferenceSupplier.getPreferenceSupplierId().getBrandCode()));

       //将不包含优选供应商的供应商进行根据品牌分组
        compTradBrands.forEach(p->{
            List<Company> list = compTradBrandsrMap.get(p.getCompTradeBrandId().getBrandCode());
            if (list == null)
                list = new ArrayList<>();
            List<Company> preList = preferredSupplierMap.get(p.getCompTradeBrandId().getBrandCode()).stream()
                .map(PreferenceSupplier::getCompany).toList();
            if(!preList.contains(p.getCompany())){
                list.add(p.getCompany());
                compTradBrandsrMap.put(p.getCompTradeBrandId().getBrandCode(), list);
            }
        });
        //找出前五个供应商
        Map<String, List<Company>> IncludeCompMap = new HashMap<>();
        brands.forEach(s -> {
            //该品牌的优选供应商列表
            List<Company> preList =preferredSupplierMap.get(s).stream()
                .sorted(Comparator.comparingInt(PreferenceSupplier::getSort))
                .map(PreferenceSupplier::getCompany)
                .collect(Collectors.toList());
            //该品牌下排除优选供应商的供应商列表，按照公司名称来排序
            List<Company> noPrelist = compTradBrandsrMap.get(s)!=null?compTradBrandsrMap.get(s).stream()
                .sorted(Comparator.comparing(Company::getNameInCN))
                .collect(Collectors.toList()):new ArrayList<Company>();
            //将两列表合并
            preList.addAll(noPrelist);
            //选择的以及自动补齐的供应商列表
            List<Company> finalHasList;
            //将前端已经选择的供应商从优选供应商中筛选出来放入finalHasList列表
            finalHasList=preList.stream().filter(company -> suppliers.contains(company.getCode())).collect(Collectors.toList());
            //在优选供应商列表中筛选出没有选择的供应商列表
            preList=preList.stream().filter(company -> !suppliers.contains(company.getCode()))
                .collect(Collectors.toList());
            //判断已经选择的数据是否超过五个,如果没有超过就从优选供应商中进行自动补齐
            if(suppliers.size()==0){
                finalHasList = new ArrayList<>();
            }
            if(finalHasList.size() < 5){
                int i = 5-finalHasList.size();
                for(int j =0;j<i;j++){
                    if(j<preList.size()){
                        finalHasList.add(preList.get(j));
                    }
                }
            }
            if (finalHasList.size() > 5) {
                for (int i = 5; i < finalHasList.size(); i++) {
                    finalHasList.remove(i);
                    i--;
                }
            }
            IncludeCompMap.put(s, finalHasList);
        });
        return IncludeCompMap;
    }

    /**
     * 验证是否有未完成的采购计划
     *
     * @param id 单位编号 operatorCode 操作员编号
     * @return 返回采购计划信息
     */
    public VBaseResponse verification(String id, String operatorCode) {
        var purchasePlan = getPurchasePlan(id, operatorCode);
        if (purchasePlan.isEmpty())
            return
                VBaseResponse.builder()
                    .code(404)
                    .message("不存在未完成的采购计划")
                    .build()
                ;
        return
            VBaseResponse.builder()
                .code(200)
                .message("存在未完成的计划，请前往采购计划详情完成计划，在开始新的计划！")
                .build()
            ;
    }

    /**
     * 查询采购计划
     *
     * @param operatorCode 操作员编号 id 单位id
     * @return 返回采购计划信息
     */
    public Optional<PurchasePlan> getPurchasePlan(String id, String operatorCode) {
        return purchasePlanRepository.findFirstByPurchasePlanId_DcCompIdAndCreatedBy(id, operatorCode);
    }

    /**
     * 替换采购计划中的供应商
     *
     * @param id          单位id
     * @param planCode    采购计划号
     * @param productId   产品id
     * @param oldSupplier 原供应商编号
     * @param newSupplier 新的供应编号
     */
    @Transactional
    public Map<String, Object> modifyPlanSupplier(String id, String planCode, String productId, String oldSupplier, String newSupplier) {
        Map<String, Object> map = new HashMap<>();
        int serial;
        try {
            Optional<Company> supplier = companyRepository.findById(newSupplier);
            if (supplier.isEmpty()) {
                map.put("code", 404);
                map.put("flag", false);
                map.put("message", "更换失败，更换的供应商不存在");
                return map;
            }
            if (!oldSupplier.isEmpty()) {
                Optional<PurchasePlanProductSupplier> supplier1 = purchasePlanProductSupplierRepository.findById(
                    PurchasePlanProductSupplierId.builder()
                        .productId(productId)
                        .dcCompId(id)
                        .planCode(planCode)
                        .salerCode(oldSupplier)
                        .build());
                if (supplier1.isEmpty()) {
                    map.put("code", 404);
                    map.put("flag", false);
                    map.put("message", "更换失败，被更换的供应商不存在");
                    return map;
                }
                serial = supplier1.get().getSerial();
                purchasePlanProductSupplierRepository.deleteById(
                    PurchasePlanProductSupplierId.builder()
                        .productId(productId)
                        .dcCompId(id)
                        .planCode(planCode)
                        .salerCode(oldSupplier)
                        .build()
                );
            } else {
                serial = purchasePlanProductSupplierRepository.findMaxSerial(productId, planCode, id);
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
            map.put("flag", true);
            map.put("code", 200);
            map.put("message", "供应商替换成功！");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("flag", false);
            map.put("code", 500);
            map.put("message", "供应商替换失败！");
            return map;
        }

    }

    /**
     * 修改询数
     *
     * @param id      单位id
     * @param demands 询数
     * @return 返回布尔（true表示修改成功）
     */
    @Transactional
    public boolean modifyPurchasePlanForSeveral(String id, VPlanDemandRequest demands) {
        try {
            purchasePlanProductSupplierRepository.updateDemandById(demands.getDemand(), PurchasePlanProductSupplierId.builder()
                .productId(demands.getProductId())
                .dcCompId(id)
                .salerCode(demands.getSupplierCode())
                .planCode(demands.getPlanCode())
                .build());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 修改需求数量
     *
     * @param id      单位id
     * @param demands 产品需求
     * @return 返回布尔值（true表示修改成功）
     */
    @Transactional
    public boolean modifyPurchasePlanDemand(String id, VPlanDemandRequest demands) {
        try {
            purchasePlanProductRepository.updateDemandById(demands.getDemand(), PurchasePlanProductId.builder()
                .productId(demands.getProductId())
                .dcCompId(id)
                .planCode(demands.getPlanCode())
                .build());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加产品
     *
     * @param id        单位id
     * @param productId 产品id
     * @param planCode  计划号
     * @param demand    需求数
     * @return 返回布尔值（true表示修改成功， false表示修改失败）
     */
    @Transactional
    public boolean savePlanProduct(String id, String productId, String planCode, BigDecimal demand) {
        try {
            Product product = productRepository.findById(productId).orElseThrow(() -> new IOException("未从数据库查询到该产品"));
            List<String> brands = new ArrayList<>();
            brands.add(product.getBrandCode());
            List<Company> suppliers = findSuppliersByBrandsAndCompBuyer(brands, id, new ArrayList<>()).get(product.getBrandCode());
            List<PurchasePlanProductSupplier> purchasePlanProductSuppliers = new ArrayList<>();
            AtomicInteger i = new AtomicInteger();
            suppliers.forEach(company -> {
                i.getAndIncrement();
                PurchasePlanProductSupplier productSupplier = PurchasePlanProductSupplier.builder()
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
                purchasePlanProductSuppliers.add(productSupplier);
            });

            PurchasePlanProduct purchasePlanProduct = PurchasePlanProduct.builder()
                .purchasePlanProductId(PurchasePlanProductId.builder()
                    .planCode(planCode)
                    .dcCompId(id)
                    .productId(productId)
                    .build())
                .productCode(product.getCode())
                .brandCode(product.getBrandCode())
                .brand(product.getBrand())
                .chargeUnit(product.getChargeUnit())
                .describe(product.getDescribe())
                .facePrice(product.getFacePrice())
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除产品
     *
     * @param id        单位id
     * @param productId 产品id列表
     * @param planCode  采购计划号
     * @return 返回布尔值（true表示删除成功）
     */
    @Transactional
    public boolean removePlanProduct(String id, List<String> productId, String planCode) {
        try {
            purchasePlanProductSupplierRepository.removeSupplier(id, planCode, productId);
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 撤销采购计划
     *
     * @param id       单位id
     * @param planCode 采购计划号
     * @return 返回布尔值（true表示删除成功）
     */
    @Transactional
    public boolean removePurchasePlan(String id, String planCode) {
        try {
            purchasePlanProductSupplierRepository.removeSupplier(id, planCode);
            purchasePlanProductRepository.removeProduct(id, planCode);
            purchasePlanRepository.deletePurchasePlan(PurchasePlanId.builder()
                .dcCompId(id)
                .planCode(planCode)
                .build());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取采购计划中的需求量不为0的供应商列表
     *
     * @param planCode 采购计划
     * @param id       单位id
     * @return 供应商列表
     */
    public List<TCompanyBaseInformation> listSuppliersByPlanCode(String planCode, String id) {
        return purchasePlanProductSupplierRepository.listDistinctSuppliers(id, planCode)
            .stream().map(stringStringMap -> TCompanyBaseInformation.builder()
                .code(stringStringMap.get("saler_code"))
                .shortName(stringStringMap.get("saler_name"))
                .build())
            .collect(Collectors.toList());
    }


}
