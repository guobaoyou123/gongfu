package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TTemporaryPlan;
import com.linzhi.gongfu.entity.Product;
import com.linzhi.gongfu.entity.TemporaryPlan;
import com.linzhi.gongfu.entity.TemporaryPlanId;
import com.linzhi.gongfu.mapper.TemporaryPlanMapper;
import com.linzhi.gongfu.repository.ProductRepository;
import com.linzhi.gongfu.repository.TemporaryPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    /**
     * 根据单位id、操作员编码查询该操作员的临时采购计划列表
     * @param temporaryPlanId 单位id 操作员编码
     * @return 临时采购计划列表信息
     */
    public List<TTemporaryPlan>  findTemporaryPlanByOperator(TemporaryPlanId temporaryPlanId){
        return  temporaryPlanRepository.findAllByTemporaryPlanId(temporaryPlanId).stream()
            .map(temporaryPlanMapper::toTemporaryPlan)
            .collect(Collectors.toList());
    }
    @Transactional
    public void saveTemporaryPlan(Optional<List<Map<String,Object>>> product,String id,String operatorCode){
        List<String> proCodeList = new ArrayList<>();
        Map<String, Product> productMap = new HashMap<>();
        product.get().stream().forEach(p -> proCodeList.add(p.get("id").toString()));
        List<Product> products=productRepository.findProductByIdIn(proCodeList);
        products.stream().forEach(product1 ->
            productMap.put(product1.getId(),product1)
        );
        List<TemporaryPlan>  saveList = new ArrayList<>();
        product.get().stream().forEach(pr -> {
            Product  p=productMap.get(pr.get("id").toString());
            TemporaryPlan temporaryPlan =TemporaryPlan.builder()
                .temporaryPlanId(TemporaryPlanId.builder().dcCompId(id).productId(p.getId()).createdBy(operatorCode).build())
                .chargeUnit(p.getChargeUnit())
                .brand(p.getBrand())
                .brandCode(p.getBrandCode())
                .describe(p.getDescribe())
                .demand((BigDecimal) pr.get("demand"))
                .build();
            saveList.add(temporaryPlan);
        });
        temporaryPlanRepository.saveAll(saveList);
    }
    @Transactional
    public void modifyTemporaryPlan(Optional<List<Map<String,Object>>> product,String id,String operatorCode){

        List<TemporaryPlan>  saveList = new ArrayList<>();
        product.get().stream().forEach(pr -> {
            TemporaryPlan temporaryPlan =TemporaryPlan.builder()
                .temporaryPlanId(TemporaryPlanId.builder().dcCompId(id).productId(pr.get("id").toString()).createdBy(operatorCode).build())
                .demand((BigDecimal) pr.get("demand"))
                .build();
            saveList.add(temporaryPlan);
        });
    }
}
