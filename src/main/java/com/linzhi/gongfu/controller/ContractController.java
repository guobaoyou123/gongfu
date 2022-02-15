package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.entity.TemporaryPlanId;
import com.linzhi.gongfu.mapper.TemporaryPlanMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.PlanService;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    public VVerificationPlanResponse  verification(@RequestBody  Optional<VVerificationPlanRequest> products){
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

    public VBaseResponse  saveTemporaryPlan(@RequestBody Optional<VTemporaryPlanRequest> products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        planService.saveTemporaryPlan(products.orElse(new VTemporaryPlanRequest()).getProducts(),session.getSession().getCompanyCode(),session.getSession().getOperatorCode());
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
    public VBaseResponse  modifyTemporaryPlan(@RequestBody Optional<VTemporaryPlanRequest> products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        planService.modifyTemporaryPlan(products.orElse(new VTemporaryPlanRequest()),session.getSession().getCompanyCode(),session.getSession().getOperatorCode());
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
    public VBaseResponse  deleteTemporaryPlan(@RequestBody Optional<VVerificationPlanRequest> products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        planService.deleteTemporaryPlan(products.orElse(new VVerificationPlanRequest()).getProducts(),session.getSession().getCompanyCode(),session.getSession().getOperatorCode());
        return VBaseResponse.builder()
            .message("删除计划成功")
            .code(200)
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
                .code(401)
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
    public VPlanResponse savePlan(@RequestBody VVerificationPlanRequest products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        var code =planService.savaPurchasePlan(products.getProducts(),products.getSuppliers(),session.getSession().getCompanyCode(),session.getSession().getOperatorCode());
        if(code.get().equals("1"))
            return VPlanResponse.builder()
                .message("产品已不存在于临时计划表中，开始计划失败")
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
}
