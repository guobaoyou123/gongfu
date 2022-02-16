package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.entity.TemporaryPlanId;
import com.linzhi.gongfu.mapper.TemporaryPlanMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.PlanService;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

;

/**
 * 用于处理采购、销售流程等
 *
 * @author zgh
 * @create_at 2022-02-10
 */
@RestController
@RequiredArgsConstructor
public class ContractController {
    private final PlanService planService;
    private final TemporaryPlanMapper temporaryPlanMapper;

    /**
     * 根据操作员编码、单位id查询该操作员的临时计划表
     * @return 临时计划列表信息
     */
    @GetMapping("/contract/temporary/purchase/plan")
    public VTemporaryPlanResponse  temporaryPlans(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        var temporaryPlans = planService.findTemporaryPlanByOperator(TemporaryPlanId.builder()
                                                                                             .createdBy(session.getSession().getOperatorCode())
                                                                                             .dcCompId(session.getSession().getCompanyCode()).build());
        return VTemporaryPlanResponse.builder()
            .message("获取采购临时计划表成功")
            .code(200)
            .products(temporaryPlans.stream().map(temporaryPlanMapper::toPreloadTemporaryPlan).collect(Collectors.toList()))
            .build();
    }
    /**
     * 根据操作员编码、单位id查询该操作员的临时计划表
     * @return 临时计划列表信息
     */
    @GetMapping("/contract/temporary/purchase/plan/product/verification")
    public VVerificationPlanResponse  verification(@RequestBody  Optional<List<String>> products){
       return products
           .flatMap(planService::TemporaryPlanVerification)
           .orElse(VVerificationPlanResponse.builder()
               .message("验证成功")
               .products(new ArrayList<>())
               .code(200)
               .build());
    }
    /**
     * 保存临时采购计划
     * @return
     */
    @PostMapping("/contract/temporary/purchase/plan")
    public VBaseResponse  saveTemporaryPlan(@RequestBody Optional<List<VTemporaryPlanRequest>> products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        planService.saveTemporaryPlan(products.get(),session.getSession().getCompanyCode(),session.getSession().getOperatorCode());
        return VBaseResponse.builder()
            .message("加入计划成功")
            .code(200)
            .build();
    }
    /**
     * 修改临时采购计划
     * @return
     */
    @PutMapping("/contract/temporary/purchase/plan")
    public VBaseResponse  modifyTemporaryPlan(@RequestBody Optional<List<VTemporaryPlanRequest>> products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        planService.modifyTemporaryPlan(products.get(),session.getSession().getCompanyCode(),session.getSession().getOperatorCode());
        return VBaseResponse.builder()
            .message("修改计划成功")
            .code(200)
            .build();
    }
    /**
     * 删除临时采购计划
     * @return
     */
    @DeleteMapping("/contract/temporary/purchase/plan")
    public VBaseResponse  deleteTemporaryPlan(@RequestBody Optional<List<String>> products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        var flag = planService.deleteTemporaryPlan(products.get(),session.getSession().getCompanyCode(),session.getSession().getOperatorCode());
        if(flag)
            return VBaseResponse.builder()
                .message("删除计划成功")
                .code(200)
                .build();
        return VBaseResponse.builder()
            .message("删除计划失败")
            .code(500)
            .build();
    }
    /**
     * 验证所选产品品牌是否有供应商
     * @return 品牌列表
     */
    @GetMapping("/contract/temporary/purchase/plan/brand/verification")
    public VVerificationBrandResponse  brandVerification(@RequestParam  Optional<List<String>> brand){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        List<String> brands =brand.
             map(b -> planService.brandVerification(b,session.getSession().getCompanyCode())).get();
        if(brands.size()>0)
            return VVerificationBrandResponse.builder()
                .message("验证未通过，部分品牌没有供应商")
                .brands(brands)
                .code(201)
                .build();
        return VVerificationBrandResponse.builder()
                .message("验证成功")
                .brands(new ArrayList<>())
                .code(200)
                .build();
    }
    /**
     * 开始计划，生成采购计划
     * @return
     */
    @PostMapping("/contract/purchase/plan")
    public VPlanResponse savePlan(@RequestBody VPurchasePlanRequest products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        var code =planService.savaPurchasePlan(products.getProducts(),products.getSuppliers(),session.getSession().getCompanyCode(),session.getSession().getOperatorCode());
        if(code.isEmpty())
            return VPlanResponse.builder()
                .message("产品已不存在于临时计划表中，开始计划失败")
                .planCode("UNKNOWN")
                .code(202)
                .build();
        return VPlanResponse.builder()
            .message("开始计划成功！")
            .code(200)
            .planCode(code.get())
            .build();
    }
    /**
     * 采购计划
     * @return 采购计划信息
     */
    @GetMapping("/contract/purchase/plan")
     public VPurchasePlanResponse purchasePlan(@RequestParam Optional<String> planCode){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
         return  planService.findPurchasePlanByCode(planCode.get(),session.getSession().getCompanyCode())
             .orElse(VPurchasePlanResponse.builder()
                 .code(202)
                 .message("采购计划不存在")
                 .planCode("UNKNOWN")
                 .products(new ArrayList<>())
                 .build());
     }

    /**
     * 采购计划替换供应商
     * @return
     */
    @PutMapping("/contract/purchase/plan/supplier")
    public VBaseResponse modifyPlanSupplier(@RequestParam Optional<String> planCode,@RequestParam Optional<String> productId,
                                             @RequestParam Optional<String> oldSupplierCode ,@RequestParam Optional<String> newSupplierCode){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        var flag = planService.modifyPlanSupplier(session.getSession().getCompanyCode(),planCode.get(),productId.get(),oldSupplierCode.get(),newSupplierCode.get());
        if(flag)
            return VBaseResponse.builder()
                .code(200)
                .message("保存成功")
                .build();
        return VBaseResponse.builder()
            .code(500)
            .message("保存失败！")
            .build();
    }
    /**
     * 修改采购计划中的需求
     * @return
     */
    @PutMapping("/contract/purchase/plan/forseveral")
    public VBaseResponse modifyPurchasePlan(@RequestBody Optional<List<VPlanDemandRequest>> demands){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        var flag = planService.modifyPurchasePlan(session.getSession().getCompanyCode(),demands.get());
        if(flag)
            return VBaseResponse.builder()
                .code(200)
                .message("修改采购计划需求成功！")
                .build();
        return VBaseResponse.builder()
            .code(500)
            .message("修改失败！")
            .build();
    }
    @PostMapping("/contract/purchase/plan/product")
    public VBaseResponse  savePlanProduct(@RequestParam("planCode")Optional<String> planCode,@RequestParam("productId")Optional<String> productId,@RequestParam("demand") Optional<BigDecimal> demand){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        var flag = planService.savePlanProduct(session.getSession().getCompanyCode(),productId.get(),planCode.get(),demand.get());
        if(flag)
            return VBaseResponse.builder()
                .code(200)
                .message("添加产品成功！")
                .build();
        return VBaseResponse.builder()
            .code(500)
            .message("添加产品失败！")
            .build();
    }
}
