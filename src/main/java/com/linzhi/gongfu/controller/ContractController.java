package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.entity.TemporaryPlanId;
import com.linzhi.gongfu.mapper.CompanyMapper;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final CompanyMapper companyMapper;

    /**
     * 根据操作员编码、单位id查询该操作员的临时计划表
     * @return 临时计划列表信息
     */
    @GetMapping("/contract/temporary/purchase/plan")
    public VTemporaryPlanResponse  temporaryPlans(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var temporaryPlans = planService.findTemporaryPlanByOperator
            (TemporaryPlanId.builder()
                .createdBy(session.getSession().getOperatorCode())
                .dcCompId(session.getSession().getCompanyCode())
                .build());
        return VTemporaryPlanResponse.builder()
            .message("获取采购临时计划表成功")
            .code(200)
            .products(temporaryPlans.stream().
                map(temporaryPlanMapper::toPreloadTemporaryPlan)
                .collect(Collectors.toList()))
            .build();
    }

    /**
     * 保存临时采购计划
     * @return
     */
    @PostMapping("/contract/temporary/purchase/plan")
    public VBaseResponse  saveTemporaryPlan(@RequestBody Optional<List<VTemporaryPlanRequest>> products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        Map map=planService.saveTemporaryPlan(
            products.orElse(new ArrayList<>()),
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        if((boolean)map.get("flag"))
            return VBaseResponse.builder()
                .message((String)map.get("message"))
                .code(200)
                .build();
        return VBaseResponse.builder()
            .message((String)map.get("message"))
            .code(500)
            .build();
    }

    /**
     * 修改临时采购计划
     * @return
     */
    @PutMapping("/contract/temporary/purchase/plan")
    public VBaseResponse  modifyTemporaryPlan(@RequestBody Optional<List<VTemporaryPlanRequest>> products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        planService.modifyTemporaryPlan(products.get(),
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
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
    public VBaseResponse  deleteTemporaryPlan(@RequestParam("products") List<String> products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.deleteTemporaryPlan(
            products,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode());
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
     * 开始计划，生成采购计划
     * @return
     */
    @PostMapping("/contract/purchase/plan")
    public VBaseResponse savePlan(@RequestBody VPurchasePlanRequest products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map =planService.savaPurchasePlan(
            products.getProducts(),
            products.getSuppliers(),
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        if(!(boolean)map.get("flag"))
            return VBaseResponse.builder()
                .message(map.get("message").toString())
                .code((Integer) map.get("code"))
                .build();
        return VBaseResponse.builder()
            .message("开始计划成功！")
            .code(200)
            .build();
    }

    /**
     * 验证是否存在未完成的计划
     * @return
     */
    @GetMapping("/contract/purchase/plan/verification")
    public VBaseResponse verification(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        return  planService.verification(session.getSession().getCompanyCode(),session.getSession().getOperatorCode())
            .orElse(VPurchasePlanResponse.builder()
                .code(404)
                .message("采购计划")
                .planCode("UNKNOWN")
                .products(new ArrayList<>())
                .build());
    }

    /**
     * 采购计划
     * @return 采购计划信息
     */
    @GetMapping("/contract/purchase/plan")
     public VPurchasePlanResponse purchasePlan(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        return  planService.findPurchasePlanByCode(session.getSession().getCompanyCode(),session.getSession().getOperatorCode())
             .orElse(VPurchasePlanResponse.builder()
                 .code(404)
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
    public VBaseResponse modifyPlanSupplier(
        @RequestBody VPlanSupplierRequest request
        ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map = planService.modifyPlanSupplier(
            session.getSession().getCompanyCode(),
            request.getPlanCode(),
            request.getProductId(),
            request.getOldSupplierCode(),
            request.getNewSupplierCode()
        );
        return VBaseResponse.builder()
            .code((Integer) map.get("code"))
            .message((String) map.get("message"))
            .build();
    }

    /**
     * 修改采购计划中的需求
     * @return
     */
    @PutMapping("/contract/purchase/plan/forseveral")
    public VBaseResponse modifyPurchasePlanForseveral(
        @RequestBody Optional<VPlanDemandRequest> forSeveral
    ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.modifyPurchasePlanForSeveral(
            session.getSession().getCompanyCode(),
            forSeveral.get()
        );
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

    /**
     * 修改采购计划中的需求
     * @return
     */
    @PutMapping("/contract/purchase/plan/demand")
    public VBaseResponse modifyPurchasePlanDemand(@RequestBody Optional<List<VPlanDemandRequest>> demand){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.modifyPurchasePlanDemand(
            session.getSession().getCompanyCode(),
            demand.get()
        );
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


    /**
     * 采购计划添加产品
     * @param request
     * @return
     */
    @PostMapping("/contract/purchase/plan/product")
    public VBaseResponse  savePlanProduct(
        @RequestBody VPlanDemandRequest request
    ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.savePlanProduct(
            session.getSession().getCompanyCode(),
            request.getProductId(),
            request.getPlanCode(),
            request.getDemand()
        );
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

    /**
     * 采购计划删除产品
     */
    @DeleteMapping("/contract/purchase/plan/product")
    public VBaseResponse  deletePlanProduct(
        @RequestParam String planCode,
        @RequestParam List<String> productId
    ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.deletePlanProduct(
            session.getSession().getCompanyCode(),
            productId,planCode
        );
        if(flag)
            return VBaseResponse.builder()
                .code(200)
                .message("删除产品成功！")
                .build();
        return VBaseResponse.builder()
            .code(500)
            .message("删除产品失败！")
            .build();
    }

    /**
     * 移除采购计划
     * @param planCode 采购计划编号
     * @return
     */
    @DeleteMapping("/contract/purchase/plan")
    public VBaseResponse deletePurchasePlan(@RequestParam("planCode") Optional<String> planCode){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.deletePurchasePlan(
            session.getSession().getCompanyCode(),
            planCode.get()
        );
        if(flag)
            return VBaseResponse.builder()
                .code(200)
                .message("删除采购计划成功！")
                .build();
        return VBaseResponse.builder()
            .code(500)
            .message("删除采购计划失败！")
            .build();
    }

    /**
     * 通过本公司id,采购计划编号查询采购询价预览表头供应商列表
     * @return 供应商列表
     */
    @GetMapping("/contract/purchase/inquiry/preview/suppliers")
    public VSuppliersResponse suppliersByPlancode(@RequestParam("planCode") Optional<String> planCode) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var supplier = planService.findSuppliersByPlanCode(
            planCode.get(),
            session.getSession().getCompanyCode()
        );
        return VSuppliersResponse.builder()
            .code(200)
            .message("获取我的供应列表成功。")
            .suppliers(
                supplier.stream()
                .map(companyMapper::toPreloadSupliers)
                .collect(Collectors.toList())
            )
            .build();
    }

    /**
     * 根据采购计划生成询价单
     * @param request  采购计划号
     * @return
     */
    @PostMapping("/contract/purchase/inquiry")
    public VBaseResponse savePurchaseInquiry(@RequestBody VPurchasePlanRequest request){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        return planService.savePurchaseInquiry(
            request.getPlanCode(),
            session.getSession().getCompanyCode(),
            session.getSession().getCompanyName(),
            session.getSession().getOperatorCode()
        );
    }
}
