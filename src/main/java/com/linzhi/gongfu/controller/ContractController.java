package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.mapper.TemporaryPlanMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.PlanService;
import com.linzhi.gongfu.vo.*;
;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
     * 修改临时采购计划
     * @return
     */
    @DeleteMapping("/contract/temporary/purchase/plan")
    public VBaseResponse  deleteTemporaryPlan(@RequestBody Optional<VVerificationPlanRequest> products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        planService.deleteTemporaryPlan(products.orElse(new VVerificationPlanRequest()).getProducts(),session.getSession().getCompanyCode(),session.getSession().getOperatorCode());
        return VBaseResponse.builder()
            .message("修改计划成功")
            .code(200)
            .build();
    }
    /**
     * 修改临时采购计划
     * @return
     */
    @PostMapping("/contract/purchase/plan")
    public VBaseResponse  savePlan(@RequestBody VVerificationPlanRequest products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        planService.deleteTemporaryPlan(products.getProducts(),session.getSession().getCompanyCode(),session.getSession().getOperatorCode());
        return VBaseResponse.builder()
            .message("修改计划成功")
            .code(200)
            .build();
    }
}
