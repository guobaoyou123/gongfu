package com.linzhi.gongfu.controller;


import com.linzhi.gongfu.entity.TemporaryPlanId;
import com.linzhi.gongfu.enumeration.ContractState;
import com.linzhi.gongfu.mapper.*;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.*;
import com.linzhi.gongfu.util.ExcelUtil;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
    private final PurchasePlanMapper purchasePlanMapper;
    private final InquiryService inquiryService;
    private final InquiryMapper inquiryMapper;
    private final PurchaseContractService purchaseContractService;
    private final ContractMapper contractMapper;
    private final SalesContractService salesContractService;
    /**
     * 根据操作员编码、单位id查询该操作员的临时计划表
     *
     * @return 临时计划列表信息
     */
    @GetMapping("/contract/temporary/purchase/plan")
    public VTemporaryPlanResponse temporaryPlans() {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var temporaryPlans = planService.listTemporaryPlansByOperator(
            TemporaryPlanId.builder()
                .createdBy(session.getSession().getOperatorCode())
                .dcCompId(session.getSession().getCompanyCode())
                .build()
        );
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
     *
     * @return 返回成功信息
     */
    @PostMapping("/contract/temporary/purchase/plan")
    public VBaseResponse saveTemporaryPlan(@RequestBody Optional<List<VTemporaryPlanRequest>> products) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map = planService.saveTemporaryPlan(
            products.orElse(new ArrayList<>()),
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .message((String) map.get("message"))
            .code((Integer) map.get("code"))
            .build();
    }

    /**
     * 修改临时采购计划
     *
     * @return 返回成功信息
     */
    @PutMapping("/contract/temporary/purchase/plan")
    public VBaseResponse modifyTemporaryPlan(@RequestBody Optional<List<VTemporaryPlanRequest>> products) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        planService.modifyTemporaryPlan(products.orElseGet(ArrayList::new),
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
     *
     * @return 返回成功信息
     */
    @DeleteMapping("/contract/temporary/purchase/plan")
    public VBaseResponse removeTemporaryPlan(@RequestParam("products") List<String> products) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.removeTemporaryPlan(
            products,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode());
        return VBaseResponse.builder()
            .message(flag ? "删除计划成功" : "删除计划失败")
            .code(flag ? 200 : 500)
            .build();
    }

    /**
     * 开始计划，生成采购计划
     *
     * @return 返回成功信息
     */
    @PostMapping("/contract/purchase/plan")
    public VBaseResponse savePlan(@RequestBody VPurchasePlanRequest products) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map = planService.savePurchasePlan(
            products.getProducts(),
            products.getSuppliers(),
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .message(map.get("message").toString())
            .code((Integer) map.get("code"))
            .build();
    }

    /**
     * 验证是否存在未完成的计划
     */
    @GetMapping("/contract/purchase/plan/verification")
    public VBaseResponse verification() {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        return planService.verification(
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
    }

    /**
     * 采购计划
     *
     * @return 采购计划信息
     */
    @GetMapping("/contract/purchase/plan")
    public VPurchasePlanResponse purchasePlan() {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        return planService.getPurchasePlan(
                session.getSession().getCompanyCode(),
                session.getSession().getOperatorCode()
            )
            .map(purchasePlanMapper::toDTO)
            .map(purchasePlanMapper::toPruchasePlan)
            .orElse(VPurchasePlanResponse.builder()
                .code(404)
                .message("采购计划不存在")
                .planCode("UNKNOWN")
                .products(new ArrayList<>())
                .build());
    }

    /**
     * 采购计划替换供应商
     *
     * @return 返回成功信息
     */
    @PutMapping("/contract/purchase/plan/supplier")
    public VBaseResponse modifyPlanSupplier(
        @RequestBody VPlanSupplierRequest request
    ) {

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
     *
     * @return 返回成功信息
     */
    @PutMapping("/contract/purchase/plan/forseveral")
    public VBaseResponse modifyPurchasePlanForSeveral(
        @RequestBody Optional<VPlanDemandRequest> forSeveral
    ) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.modifyPurchasePlanForSeveral(
            session.getSession().getCompanyCode(),
            forSeveral.orElseGet(VPlanDemandRequest::new)
        );
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "修改采购计划需求成功！" : "修改失败！")
            .build();
    }

    /**
     * 修改采购计划中的需求
     *
     * @return 返回成功信息
     */
    @PutMapping("/contract/purchase/plan/demand")
    public VBaseResponse modifyPurchasePlanDemand(@RequestBody Optional<VPlanDemandRequest> demand) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.modifyPurchasePlanDemand(
            session.getSession().getCompanyCode(),
            demand.orElseGet(VPlanDemandRequest::new)
        );
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "修改采购计划需求成功！" : "修改失败！")
            .build();
    }

    /**
     * 采购计划添加产品
     *
     * @param request 产品计划需求数
     * @return 返回成功信息
     */
    @PostMapping("/contract/purchase/plan/product")
    public VBaseResponse savePlanProduct(
        @RequestBody VPlanDemandRequest request
    ) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.savePlanProduct(
            session.getSession().getCompanyCode(),
            request.getProductId(),
            request.getPlanCode(),
            request.getDemand()
        );
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "添加产品成功！" : "添加产品失败！")
            .build();
    }

    /**
     * 采购计划删除产品
     */
    @DeleteMapping("/contract/purchase/plan/product")
    public VBaseResponse deletePlanProduct(
        @RequestParam String planCode,
        @RequestParam List<String> productId
    ) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.removePlanProduct(
            session.getSession().getCompanyCode(),
            productId, planCode
        );
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "删除产品成功！" : "删除产品失败！")
            .build();
    }

    /**
     * 移除采购计划
     *
     * @param planCode 采购计划编号
     * @return 返回成功信息
     */
    @DeleteMapping("/contract/purchase/plan")
    public VBaseResponse deletePurchasePlan(@RequestParam("planCode") Optional<String> planCode) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.removePurchasePlan(
            session.getSession().getCompanyCode(),
            planCode.orElseGet(String::new)
        );
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "删除采购计划成功！" : "删除采购计划失败！")
            .build();
    }

    /**
     * 通过本公司id,采购计划编号查询采购询价预览表头供应商列表
     *
     * @return 供应商列表
     */
    @GetMapping("/contract/purchase/inquiry/preview/suppliers")
    public VSuppliersResponse suppliersByPlanCode(@RequestParam("planCode") Optional<String> planCode) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var supplier = planService.listSuppliersByPlanCode(
            planCode.orElseGet(String::new),
            session.getSession().getCompanyCode()
        );
        return VSuppliersResponse.builder()
            .code(200)
            .message("获取我的供应列表成功。")
            .suppliers(
                supplier.stream()
                    .map(companyMapper::toPreloadSuppliers)
                    .collect(Collectors.toList())
            )
            .build();
    }

    /**
     * 根据采购计划生成询价单
     *
     * @param request 采购计划号
     * @return 返回成功信息
     */
    @PostMapping("/contract/purchase/inquiry")
    public VBaseResponse savePurchaseInquiry(@RequestBody VPurchasePlanRequest request) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map = inquiryService.savePurchaseInquiry(
            request.getPlanCode(),
            session.getSession().getCompanyCode(),
            session.getSession().getCompanyName(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .code((Integer) map.get("code"))
            .message((String) map.get("message"))
            .build();
    }

    /**
     * 生成空的采购计划
     *
     * @return 返回成功信息
     */
    @PostMapping("/contract/purchase/plan/empty")
    public VBaseResponse saveEmptyPlan() {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map = planService.savaEmptyPurchasePlan(
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .message(map.get("message").toString())
            .code((Integer) map.get("code"))
            .build();
    }

    /**
     * 询价单列表
     *
     * @return 询价单列表
     */
    @GetMapping("/contract/purchase/inquiry")
    public VInquiryListResponse inquiryList() {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var list = inquiryService.listInquiries(
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(), "0"
        );
        return VInquiryListResponse.builder()
            .code(200)
            .message("获取询价单列表成功")
            .inquiries(list.stream()
                .map(inquiryMapper::toVInquiryList)
                .toList()
            )
            .build();
    }

    /**
     * 查询对应该供应商未完成的询价单
     *
     * @param supplierCode 供应商编码
     * @return 返回未完成的询价单列表
     */
    @GetMapping("/contract/purchase/inquiry/unfinished")
    public VUnfinishedInquiryListResponse unfinishedInquiries(@RequestParam("supplierCode") String supplierCode) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var list = inquiryService.listUnfinishedInquiries(session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            supplierCode);
        return VUnfinishedInquiryListResponse.builder()
            .code(200)
            .message("获取数据成功")
            .inquiries(list)
            .build();
    }

    /**
     * 查看询价单历史列表
     *
     * @param supplierCode 供应商编码
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param state        状态
     * @param pageNum      页码
     * @param pageSize     每页展示几条
     * @return 询价单历史列表
     */
    @GetMapping("/contract/purchase/inquiry/history")
    public VInquiryPageResponse inquiryHistory(@RequestParam("supplierCode") Optional<String> supplierCode,
                                               @RequestParam("startTime") Optional<String> startTime,
                                               @RequestParam("endTime") Optional<String> endTime,
                                               @RequestParam("state") Optional<String> state,
                                               @RequestParam("pageNum") Optional<String> pageNum,
                                               @RequestParam("pageSize") Optional<String> pageSize
    ) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        //分页
        Pageable pageable = PageRequest.of(
            pageNum.map(PageTools::verificationPageNum).orElse(0),
            pageSize.map(PageTools::verificationPageSize).orElse(10)
        );
        var page = inquiryService.pageInquiryHistories(session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(), supplierCode.orElse(""), startTime.orElse(""), endTime.orElse(""),
            state.orElse("1"), pageable);
        return VInquiryPageResponse.builder()
            .code(200)
            .message("查询成功")
            .current(page.getNumber() + 1)
            .total(Integer.parseInt(String.valueOf(page.getTotalElements())))
            .inquiries(page.stream().map(inquiryMapper::toVInquiryPage).toList())
            .build()
            ;
    }

    /**
     * 查询询价单详情
     *
     * @param id 询价单主键
     * @return 返回询价单详细信息
     */
    @GetMapping("/contract/purchase/inquiry/{id}")
    public VInquiryDetailResponse inquiryDetail(@PathVariable("id") Optional<String> id) {

        var inquiry = id.map(inquiryService::getInquiryDetail)
            .map(inquiryMapper::toVInquiryDetail);
        if (inquiry.isPresent())
            return VInquiryDetailResponse.builder()
                .code(200)
                .message("获取询价单详情成功")
                .inquiry(inquiry.get())
                .build();
        return VInquiryDetailResponse.builder()
            .code(404)
            .message("没有找到要查询的数据")
            .inquiry(new VInquiryDetailResponse.VInquiry())
            .build();
    }

    /**
     * 新建空的询价单
     * vEmptyInquiryRequest 供应商编码
     *
     * @return 询价单编码
     */
    @PostMapping("/contract/purchase/inquiry/empty")
    public VInquiryResponse emptyInquiry(@RequestBody VInquiryIdRequest vEmptyInquiryRequest) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var code = inquiryService.saveEmptyInquiry(
            session.getSession().getCompanyCode(),
            session.getSession().getCompanyName(),
            session.getSession().getOperatorCode(),
            vEmptyInquiryRequest.getSupplierCode()
        );
        if (code == null)
            return VInquiryResponse.builder()
                .code(500)
                .message("新建空的询价单失败")
                .inquiryId("UNKNOWN")
                .build();
        return VInquiryResponse.builder()
            .code(200)
            .message("新建询价单成功")
            .inquiryId(code)
            .build();
    }

    /**
     * 添加产品
     *
     * @param id                     询价单主键
     * @param vInquiryProductRequest 产品信息
     * @return 返回成功或者失败信息
     */
    @PostMapping("/contract/purchase/inquiry/{id}/product")
    public VBaseResponse saveInquiryProduct(
        @PathVariable("id") String id,
        @RequestBody VInquiryProductRequest vInquiryProductRequest) {

        var flag = inquiryService.saveInquiryProduct(
            id,
            vInquiryProductRequest.getProductId(),
            vInquiryProductRequest.getPrice(),
            vInquiryProductRequest.getAmount()
        );
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "添加产品成功" : "添加产品失败")
            .build();
    }


    /**
     * 导出产品
     *
     * @param id       询价单id
     * @param response HttpServletResponse
     */
    @GetMapping("/contract/purchase/inquiry/{id}/products/export")
    public void exportProduct(@PathVariable String id, HttpServletResponse response) {
        var map = inquiryService.exportProduct(id);
        List<LinkedHashMap<String, Object>> database = (List<LinkedHashMap<String, Object>>) map.get("list");
        ExcelUtil.exportToExcel(response, "询价单"+ map.get("encode"), database);
    }

    /**
     * 删除询价单产品
     *
     * @param codes 产品编码列表
     * @param id    询价单编码
     * @return 返回成功或者失败信息
     */
    @DeleteMapping("/contract/purchase/inquiry/{id}/product")
    public VBaseResponse removeInquiryProduct(@RequestParam("codes") List<Integer> codes, @PathVariable("id") String id) {

        var flag = inquiryService.removeInquiryProduct(id, codes);
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "删除产品成功" : "删除产品失败")
            .build();
    }

    /**
     * 修改询价单
     *
     * @param modifyInquiryRequest 修改内容
     * @param id                   询价单id
     * @return 返回成功或者失败信息
     */
    @PutMapping("/contract/purchase/inquiry/{id}")
    public VBaseResponse modifyInquiry(
        @RequestBody VInquiryRequest modifyInquiryRequest,
        @PathVariable String id) {

        var flag = inquiryService.modifyInquiry(
            modifyInquiryRequest,
            id);
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "修改成功" : "修改失败")
            .build();
    }

    /**
     * 撤销询价单
     *
     * @param id 询价单id
     * @return 返回成功或者失败信息
     */
    @DeleteMapping("/contract/purchase/inquiry/{id}")
    public VBaseResponse deleteInquiry(@PathVariable String id) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = inquiryService.removeInquiry(
            id,
            session.getSession().getCompanyCode()
        );
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "撤销成功" : "撤销失败")
            .build();
    }

    /**
     * 生成采购合同
     *
     * @param contractRequest 生成采购合同需要的参数实体
     * @return 返回成功或者失败
     */
    @PostMapping("/contract/purchase")
    public VBaseResponse saveContract(@RequestBody VPContractRequest contractRequest) throws Exception {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = true;
        String contractCodes = "";
        //Inquiry inquiry = inquiryService.getInquiry(contractRequest.getInquiryId());
        //判断是否需要进行判断是否有重复的合同
        if (!contractRequest.isEnforce())
            contractCodes = purchaseContractService.getContractProductRepeat(contractRequest.getInquiryId()).orElse("");
        if (!contractCodes.equals(""))
            return VBaseResponse.builder()
                .code(201)
                .message("可能重复的合同有：" + contractCodes)
                .build();
        flag = purchaseContractService.saveContract(contractRequest, session.getSession().getCompanyCode(), session.getSession().getOperatorName(), session.getSession().getOperatorCode());
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "成功采购合同" : "生成采购合同失败")
            .build();
    }

    /**
     * 税率列表
     *
     * @param type 类型 1-产品 2-服务
     * @return 返回税率列表信息
     */
    @GetMapping("/contract/taxRate")
    public VTaxRateResponse listTaxRates(@RequestParam("type") String type) {

        var list = purchaseContractService.listTaxRates(type);
        return VTaxRateResponse.builder()
            .code(200)
            .message("获取税率列表成功")
            .taxRates(list)
            .build();
    }

    /**
     * 判断采购合同号是否重复
     *
     * @param contractNo 本单位采购合同号
     * @return 是否重复信息
     */
    @GetMapping("/contract/purchase/contractNo")
    public VBaseResponse changeContractNoRepeated(@RequestParam("contractNo") Optional<String> contractNo) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = purchaseContractService.changeContractNoRepeated(contractNo.orElse(""), session.getSession().getCompanyCode(), "");
        return VBaseResponse.builder()
            .code(flag ? 200 : 201)
            .message(flag ? "数据不重复" : "数据重复")
            .build();
    }

    /**
     * 判断采购合同号是否重复
     *
     * @param contractNo 本单位采购合同号
     * @param id         采购合同编码
     * @return 是否重复信息
     */
    @GetMapping("/contract/purchase/{id}/contractNo")
    public VBaseResponse changeContractNoRepeated(@RequestParam("contractNo") Optional<String> contractNo,
                                                  @PathVariable String id) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = purchaseContractService.changeContractNoRepeated(contractNo.orElse(""), session.getSession().getCompanyCode(), id);
        return VBaseResponse.builder()
            .code(flag ? 200 : 201)
            .message(flag ? "数据不重复" : "数据重复")
            .build();
    }

    /**
     * 根据条件查询采购合同列表（分页）
     *
     * @param state        采购合同状态 0-未完成 1-已确定 2-撤销
     * @param supplierCode 供应商合同编码
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param pageNum      页码
     * @param pageSize     每页展示几条
     * @return 采购合同列表分页
     */
    @GetMapping("/contract/purchase")
    public VPContractPageResponse pageContracts(@RequestParam("supplierCode") Optional<String> supplierCode,
                                                @RequestParam("startTime") Optional<String> startTime,
                                                @RequestParam("endTime") Optional<String> endTime,
                                                @RequestParam("state") Optional<String> state,
                                                @RequestParam("pageNum") Optional<String> pageNum,
                                                @RequestParam("pageSize") Optional<String> pageSize
    ) throws Exception {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        //分页
        Pageable pageable = PageRequest.of(
            pageNum.map(PageTools::verificationPageNum).orElse(0),
            pageSize.map(PageTools::verificationPageSize).orElse(10)
        );
        var page = purchaseContractService.pageContracts(
            state.orElse("0"),
            supplierCode.orElse(""),
            startTime.orElse(""),
            endTime.orElse(""),
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            pageable
        );
        return VPContractPageResponse.builder()
            .code(200)
            .message("查询成功")
            .current(page.getNumber() + 1)
            .total(Integer.parseInt(String.valueOf(page.getTotalElements())))
            .contracts(page.stream().map(contractMapper::toContractPage).toList())
            .build()
            ;
    }

    /**
     * 根据采购合同主键和版本号查询合同详情
     *
     * @param id       采购合同主键
     * @param revision 版本号
     * @return 返回合同详情
     * @throws IOException 异常
     */
    @GetMapping("/contract/purchase/{id}/{revision}")
    public VPContractDetailResponse purchaseContractDetail(
        @PathVariable("id") String id,
        @PathVariable("revision") Integer revision
    ) throws IOException {

        boolean repetitive = false;
        //查询采购合同
        var contract = purchaseContractService.getPurchaseContractDetail(id, revision);
        //如果合同版本号大于1且状态为未完成，需要判断是否与上一版内容一致
        if (revision > 1 && contract.getState().equals(ContractState.UN_FINISHED.getState() + ""))
            repetitive = purchaseContractService.judgeContractRev(id, revision);
        return VPContractDetailResponse.builder()
            .code(200)
            .message("获取数据成功")
            .contract(contract)
            .repetitive(repetitive)
            .build();
    }

    /**
     * 生成空的采购合同
     *
     * @param supplierCode 供应商编码
     * @return 返回成功信息
     */
    @PostMapping("/contract/purchase/empty")
    public VPContractResponse savePurchaseContractEmpty(@RequestBody Optional<VInquiryIdRequest> supplierCode) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var id = purchaseContractService.savePurchaseContractEmpty(
                supplierCode.orElse(new VInquiryIdRequest()).getSupplierCode(),
                session.getSession().getCompanyCode(),
                session.getSession().getCompanyName(),
                session.getSession().getOperatorCode(),
                session.getSession().getOperatorName())
            .orElse("");
        if (id.isEmpty())
            return VPContractResponse.builder()
                .code(500)
                .message("保存失败")
                .build();
        return VPContractResponse.builder()
            .code(200)
            .message("保存成功")
            .contractId(id)
            .build();
    }

    /**
     * 获取未确认的采购合同数量
     *
     * @param supplierCode 供应商编码
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @return 返回未确认的采购合同数量
     */
    @GetMapping("/contract/purchase/unconfirmed")
    public VPContractAmountResponse findUnfinishedAmount(
        @RequestParam("supplierCode") Optional<String> supplierCode,
        @RequestParam("startTime") Optional<String> startTime,
        @RequestParam("endTime") Optional<String> endTime
    ) throws Exception {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        return VPContractAmountResponse.builder()
            .code(200)
            .message("获取数据成功")
            .amount(purchaseContractService.getUnFinished(
                session.getSession().getCompanyCode(),
                session.getSession().getOperatorCode(),
                startTime.orElse(""),
                endTime.orElse(""),
                supplierCode.orElse("")
            ))
            .build();
    }

    /**
     * 采购合同添加产品
     *
     * @param product  产品信息
     * @param id       合同主键
     * @param revision 版本号
     * @return 添加成功或者失败的信息
     */
    @PostMapping("/contract/purchase/{id}/{revision}/product")
    public VBaseResponse saveContractProduct(
        @RequestBody Optional<VInquiryProductRequest> product,
        @PathVariable("id") String id,
        @PathVariable("revision") Integer revision
    ) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = purchaseContractService.saveProduct(product.orElseThrow().getProductId(),
            product.get().getPrice(),
            product.get().getAmount(),
            id,
            revision,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "添加产品成功" : "添加产品失败")
            .build();
    }

    /**
     * 采购合同删除产品
     *
     * @param codes    明细序列号列表
     * @param id       合同主键
     * @param revision 版本号
     * @return 返回成功信息
     */
    @DeleteMapping("/contract/purchase/{id}/{revision}/product")
    public VBaseResponse deletePurchaseContract(
        @RequestParam("codes") List<Integer> codes,
        @PathVariable("id") String id,
        @PathVariable("revision") Integer revision
    ) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = purchaseContractService.removeContractProduct(
            codes,
            id,
            revision,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "删除产品成功" : "删除产品失败")
            .build();
    }

    /**
     * 修改合同状态
     *
     * @param id 合同主键
     * @return 返回合同版本号
     */
    @PutMapping("/contract/purchase/{id}")
    public VPContractRevisionResponse modifyContractState(@PathVariable String id) throws IOException {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var revision = purchaseContractService.modifyContractState(id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            purchaseContractService.getMaxRevision(id)
        );
        return VPContractRevisionResponse.builder()
            .code(revision == 0 ? 500 : 200)
            .revision(revision)
            .message(revision == 0 ? "修改合同失败" : "修改合同成功")
            .build();
    }

    /**
     * 修改采购合同
     *
     * @param vModifyInquiryRequest 修改内容
     * @param id                    合同主键
     * @param revision              版本
     * @return 返回成功或者失败
     */
    @PutMapping("/contract/purchase/{id}/{revision}")
    public VBaseResponse modifyPurchaseContract(
        @RequestBody Optional<VInquiryRequest> vModifyInquiryRequest,
        @PathVariable("id") String id,
        @PathVariable("revision") Integer revision
    ) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = purchaseContractService.modifyPurchaseContract(
            vModifyInquiryRequest.orElse(new VInquiryRequest()),
            id,
            revision,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "修改合同成功" : "修改合同失败")
            .build();
    }

    /**
     * 采购合同导出产品模板
     *
     * @param id       合同id
     * @param response HttpServletResponse
     */
    @GetMapping("/contract/purchase/{id}/{revision}/template/export")
    public void exportContractProductTemplate(
        @PathVariable String id,
        @PathVariable Integer revision,
        HttpServletResponse response
    ) {

        var map = purchaseContractService.exportProductTemplate(id, revision);
        List<LinkedHashMap<String, Object>> database = (List<LinkedHashMap<String, Object>>) map.get("list");
        String code = (String) map.get("code");
        ExcelUtil.exportToExcel(response, "采购合同" + code, database);
    }

    /**
     * 采购合同导出产品明细
     *
     * @param id       合同id
     * @param response HttpServletResponse
     */
    @GetMapping("/contract/purchase/{id}/{revision}/products/export")
    public void exportContractProduct(
        @PathVariable String id,
        @PathVariable Integer revision,
        HttpServletResponse response
    ) {

        var map = purchaseContractService.exportProduct(id, revision);
        List<LinkedHashMap<String, Object>> database = (List<LinkedHashMap<String, Object>>) map.get("list");
        String code = (String) map.get("code");
        ExcelUtil.exportToExcel(response, code + "采购合同明细表", database);
    }

    /**
     * 撤销该版本合同
     *
     * @param id 合同主键
     * @return 返货成功信息
     * @throws IOException 异常
     */
    @DeleteMapping("/contract/purchase/{id}/revision")
    public VBaseResponse cancelCurrentRevision(@PathVariable String id) throws Exception {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var contract = purchaseContractService.getContractDetail(id);
        if (contract.getState().equals(ContractState.FINISHED))
            return VBaseResponse.builder()
                .code(500)
                .message("该版本已撤销")
                .build();
        purchaseContractService.removeCurrentRevision(
            id,
            purchaseContractService.getMaxRevision(id),
            contract, session.getSession().getCompanyCode()
        );

        return VBaseResponse.builder()
            .code(200)
            .message("撤销该版本成功")
            .build();
    }

    /**
     * 保存退回临时记录
     *
     * @param deliveryTempRequests 退回临时记录列表
     * @param id                   合同主键
     * @param revision             合同版本
     * @return 返回成功信息
     */
    @PostMapping("/contract/purchase/{id}/{revision}/delivery/record")
    public VBaseResponse saveDeliveryTemp(
        @RequestBody List<VDeliveryTempRequest> deliveryTempRequests,
        @PathVariable("id") String id,
        @PathVariable("revision") Integer revision
    ) {

        purchaseContractService.saveDeliveryTemp(deliveryTempRequests, id, revision);
        return VBaseResponse.builder()
            .code(200)
            .message("保存成功")
            .build();
    }

    /**
     * 生成新一版的采购合同
     *
     * @param id       采购合同id
     * @param revision 版本号
     * @return 返回成功或者失败
     */
    @PostMapping("/contract/purchase/{id}/{revision}")
    public VBaseResponse saveContractRevision(
        @PathVariable String id,
        @PathVariable Integer revision,
        @RequestBody VPContractRequest generateContractRequest
    ) throws Exception {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();

        String contractCodes = "";
        if (!generateContractRequest.isEnforce())
            contractCodes = purchaseContractService.findContractProductRepeat(id, revision).orElse("");

        if (!contractCodes.equals(""))
            return VBaseResponse.builder()
                .code(201)
                .message("可能重复的合同有：" + contractCodes)
                .build();

        purchaseContractService.saveContractRevision(
            id,
            revision,
            generateContractRequest,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );

        return VBaseResponse.builder()
            .code(200)
            .message("成功采购合同")
            .build();
    }

    /**
     * 查询采购合同中已开票产品列表
     *
     * @param id 合同主键
     * @return 产品列表
     */
    @GetMapping("/contract/purchase/{id}/invoiced")
    // TODO: 2022/6/2   需要重新完善
    public VInvoicedResponse getInvoicedList(@PathVariable String id) {
        var products = purchaseContractService.getInvoicedList(id);
        return VInvoicedResponse.builder()
            .code(200)
            .message("获取数据成功")
            .products(products)
            .build();
    }

    /**
     * 查询采购合同中已收货产品列表
     *
     * @param id 合同主键
     * @return 产品列表
     */
    // TODO: 2022/6/2   需要重新完善
    @GetMapping("/contract/purchase/{id}/received")
    public VReceivedResponse getReceivedList(@PathVariable String id) {
        var products = purchaseContractService.getReceivedList(id);
        return VReceivedResponse.builder()
            .code(200)
            .message("获取数据成功")
            .products(products)
            .build();
    }

    /**
     * 撤销采购合同
     *
     * @param id 采购合同主键
     * @return 返回成功或者失败
     */
    @DeleteMapping("/contract/purchase/{id}")
    public VBaseResponse removePurchaseContract(@PathVariable String id) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        purchaseContractService.removePurchaseContract(
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .code(200)
            .message("撤销成功")
            .build();
    }

    /**
     * 修改采购合同明细预览
     *
     * @param id       采购合同主键
     * @param revision 版本号
     * @return 返回采购合同预览列表
     */
    @GetMapping("/contract/purchase/{id}/{revision}/preview")
    public VPContractPreviewResponse modifyContractPreview(
        @PathVariable("id") String id,
        @PathVariable("revision") Integer revision
    ) {

        var list = purchaseContractService.modifyContractPreview(id, revision);
        return VPContractPreviewResponse.builder()
            .code(200)
            .message("获取数据成功")
            .products(list)
            .build();
    }

    /**
     * 生成与该合同相同的新的未确认的采购合同
     *
     * @param id       合同id
     * @param revision 版本
     * @return 返回新合同主键
     */
    @PostMapping("/contract/purchase/{id}/{revision}/copy")
    public VPContractResponse copyContract(@PathVariable String id, @PathVariable Integer revision) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        String contractId = purchaseContractService.copyContract(
            id,
            revision,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );

        return VPContractResponse.builder()
            .code(contractId == null ? 500 : 200)
            .message(contractId == null ? "操作失败" : "操作成功")
            .contractId(contractId)
            .build();
    }

    /**
     * 根据条件查询销售合同列表（分页）
     *
     * @param state        采销售合同状态 0-未完成 1-已确定 2-撤销
     * @param customerCode 客户合同编码
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param pageNum      页码
     * @param pageSize     每页展示几条
     * @return 销售合同列表分页
     */
    @GetMapping("/contract/sales")
    public VPContractPageResponse pageSalesContracts(@RequestParam("customerCode") Optional<String> customerCode,
                                                     @RequestParam("startTime") Optional<String> startTime,
                                                     @RequestParam("endTime") Optional<String> endTime,
                                                     @RequestParam("state") Optional<String> state,
                                                     @RequestParam("pageNum") Optional<String> pageNum,
                                                     @RequestParam("pageSize") Optional<String> pageSize
    ) throws Exception {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        //分页
        Pageable pageable = PageRequest.of(
            pageNum.map(PageTools::verificationPageNum).orElse(0),
            pageSize.map(PageTools::verificationPageSize).orElse(10)
        );
        var page = salesContractService.pageContracts(
            state.orElse("0"),
            customerCode.orElse(""),
            startTime.orElse(""),
            endTime.orElse(""),
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            pageable
        );
        return VPContractPageResponse.builder()
            .code(200)
            .message("查询成功")
            .current(page.getNumber() + 1)
            .total(Integer.parseInt(String.valueOf(page.getTotalElements())))
            .contracts(page.stream().map(contractMapper::toContractPage).toList())
            .build()
            ;
    }

    /**
     * 根据销售合同主键和版本号查询合同详情
     *
     * @param id       销售合同主键
     * @param revision 版本号
     * @return 返回合同详情
     */
    @GetMapping("/contract/sales/{id}/{revision}")
    @Transactional(readOnly = true)
    public VPContractDetailResponse salesContractDetail(
        @PathVariable("id") String id,
        @PathVariable("revision") Integer revision
    ) {
        boolean repetitive = false;
        //查询销售合同
        var contract = salesContractService.getSalesContractDetail(id, revision);
        //如果合同版本号大于1且状态为未完成，需要判断是否与上一版内容一致
        if (revision > 1 && contract.getState().equals(ContractState.UN_FINISHED.getState() + ""))
            repetitive = salesContractService.judgeContractRev(id, revision);
        return VPContractDetailResponse.builder()
            .code(200)
            .message("获取数据成功")
            .contract(contract)
            .repetitive(repetitive)
            .build();
    }

    /**
     * 生成空的销售合同
     *
     * @param customerCode 客户商编码
     * @return 返回成功信息
     */
    @PostMapping("/contract/sales/empty")
    public VPContractResponse saveSalesContractEmpty(@RequestBody Optional<VSEmptyContractRequest> customerCode) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var id = salesContractService.saveSalesContractEmpty(
                customerCode.orElse(new VSEmptyContractRequest()).getCustomerCode(),
                session.getSession().getCompanyCode(),
                session.getSession().getCompanyName(),
                session.getSession().getOperatorCode(),
                session.getSession().getOperatorName())
            .orElse("");
        if (id.isEmpty())
            return VPContractResponse.builder()
                .code(500)
                .message("保存失败")
                .build();
        return VPContractResponse.builder()
            .code(200)
            .message("保存成功")
            .contractId(id)
            .build();
    }

    /**
     * 销售合同添加产品
     *
     * @param product  产品信息
     * @param id       合同主键
     * @param revision 版本号
     * @return 添加成功或者失败的信息
     */
    @PostMapping("/contract/sales/{id}/{revision}/product")
    public VBaseResponse saveSalesContractProduct(
        @RequestBody Optional<VInquiryProductRequest> product,
        @PathVariable("id") String id,
        @PathVariable("revision") Integer revision
    ) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();

        var flag = salesContractService.saveProduct(
            product.orElseThrow().getProductId(),
            product.get().getPrice(),
            product.get().getAmount(),
            id,
            revision,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );

        return VBaseResponse.builder()
            .code(flag ? 200 : 500)
            .message(flag ? "添加产品成功" : "添加产品失败")
            .build();
    }

    /**
     * 销售合同删除产品
     *
     * @param codes    明细序列号列表
     * @param id       合同主键
     * @param revision 版本号
     * @return 返回成功信息
     */
    @DeleteMapping("/contract/sales/{id}/{revision}/product")
    public VBaseResponse deleteSalesContract(
        @RequestParam("codes") List<Integer> codes,
        @PathVariable("id") String id,
        @PathVariable("revision") Integer revision
    ) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        salesContractService.removeContractProduct(
            codes,
            id,
            revision,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .code(200)
            .message("删除产品成功")
            .build();
    }

    /**
     * 修改销售合同状态
     *
     * @param id 合同主键
     * @return 返回合同版本号
     */
    @PutMapping("/contract/sales/{id}")
    public VPContractRevisionResponse modifySalesContractState(@PathVariable String id) throws IOException {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var revision = salesContractService.modifyContractState(id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            salesContractService.getMaxRevision(id)
        );
        return VPContractRevisionResponse.builder()
            .code(revision == 0 ? 500 : 200)
            .revision(revision)
            .message(revision == 0 ? "修改合同失败" : "修改合同成功")
            .build();
    }

    /**
     * 判断销售合同号是否重复
     *
     * @param contractNo 本单位销售合同号
     * @return 是否重复信息
     */
    @GetMapping("/contract/sales/contractNo")
    public VBaseResponse changeSalesContractNoRepeated(@RequestParam("contractNo") Optional<String> contractNo) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = salesContractService.changeContractNoRepeated(contractNo.orElse(""), session.getSession().getCompanyCode(), "");
        return VBaseResponse.builder()
            .code(flag ? 200 : 201)
            .message(flag ? "数据不重复" : "数据重复")
            .build();
    }

    /**
     * 判断销售合同号是否重复
     *
     * @param contractNo 本单位销售合同号
     * @param id         销售合同编码
     * @return 是否重复信息
     */
    @GetMapping("/contract/sales/{id}/contractNo")
    public VBaseResponse changeSalesContractNoRepeated(@RequestParam("contractNo") Optional<String> contractNo,
                                                       @PathVariable String id) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = salesContractService.changeContractNoRepeated(contractNo.orElse(""), session.getSession().getCompanyCode(), id);
        return VBaseResponse.builder()
            .code(flag ? 200 : 201)
            .message(flag ? "数据不重复" : "数据重复")
            .build();
    }

    /**
     * 销售合同导出产品模板
     *
     * @param id       合同id
     * @param response HttpServletResponse
     */
    @GetMapping("/contract/sales/{id}/{revision}/template/export")
    public void exportSalesContractProductTemplate(
        @PathVariable String id,
        @PathVariable Integer revision,
        HttpServletResponse response
    ) {

        var map = salesContractService.exportProductTemplate(id, revision);
        List<LinkedHashMap<String, Object>> database = (List<LinkedHashMap<String, Object>>) map.get("list");
        String code = (String) map.get("code");
        ExcelUtil.exportToExcel(response, "销售合同" + code, database);

    }

    /**
     * 销售合同导出产品明细
     *
     * @param id       合同id
     * @param revision 版本
     * @param type     类型 0-客户自定义代码 1-产品源代码
     * @param response HttpServletResponse
     */
    @GetMapping("/contract/sales/{id}/{revision}/products/export/{type}")
    public void exportSalesContractProduct(
        @PathVariable String id,
        @PathVariable Integer revision,
        @PathVariable String type,
        HttpServletResponse response
    ) {

        var map = salesContractService.exportProduct(id, revision, type);
        List<LinkedHashMap<String, Object>> database = (List<LinkedHashMap<String, Object>>) map.get("list");
        String code = (String) map.get("code");
        ExcelUtil.exportToExcel(response, code + "销售合同明细表", database);
    }

    /**
     * 生成与该合同相同的新的未确认的销售合同
     *
     * @param id       合同id
     * @param revision 版本
     * @return 返回新合同主键
     */
    @PostMapping("/contract/sales/{id}/{revision}/copy")
    public VPContractResponse copySalesContract(@PathVariable String id, @PathVariable Integer revision) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        String contractId = salesContractService.copyContract(
            id,
            revision,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );

        return VPContractResponse.builder()
            .code(contractId == null ? 500 : 200)
            .message(contractId == null ? "操作失败" : "操作成功")
            .contractId(contractId)
            .build();
    }

    /**
     * 修改销售合同
     *
     * @param vModifyInquiryRequest 修改内容
     * @param id                    合同主键
     * @param revision              版本
     * @return 返回成功或者失败
     */
    @PutMapping("/contract/sales/{id}/{revision}")
    public VBaseResponse modifySalesContract(
        @RequestBody Optional<VInquiryRequest> vModifyInquiryRequest,
        @PathVariable("id") String id,
        @PathVariable("revision") Integer revision
    ) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        salesContractService.modifySalesContract(
            vModifyInquiryRequest.orElse(new VInquiryRequest()),
            id,
            revision,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .code(200)
            .message("修改合同成功")
            .build();
    }

    /**
     * 获取未确认的销售合同数量
     *
     * @param customerCode 客户编码
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @return 返回未确认的销售合同数量
     */
    @GetMapping("/contract/sales/unconfirmed")
    public VPContractAmountResponse findSalesUnfinishedAmount(
        @RequestParam("customerCode") Optional<String> customerCode,
        @RequestParam("startTime") Optional<String> startTime,
        @RequestParam("endTime") Optional<String> endTime
    ) throws Exception {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        return VPContractAmountResponse.builder()
            .code(200)
            .message("获取数据成功")
            .amount(salesContractService.getUnFinished(
                session.getSession().getCompanyCode(),
                session.getSession().getOperatorCode(),
                startTime.orElse(""),
                endTime.orElse(""),
                customerCode.orElse("")
            ))
            .build();
    }

    /**
     * 确认销售合同
     *
     * @param id       采购合同id
     * @param revision 版本号
     * @return 返回成功或者失败
     */
    @PostMapping("/contract/sales/{id}/{revision}")
    public VBaseResponse saveSalesContractRevision(
        @PathVariable String id,
        @PathVariable Integer revision,
        @RequestBody VPContractRequest generateContractRequest
    ) throws Exception {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();

        String contractCodes = "";
        if (!generateContractRequest.isEnforce())
            contractCodes = salesContractService.findContractProductRepeat(id, revision).orElse("");

        if (!contractCodes.equals(""))
            return VBaseResponse.builder()
                .code(201)
                .message("可能重复的合同有：" + contractCodes)
                .build();

        salesContractService.saveContractRevision(
            id,
            revision,
            generateContractRequest,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );

        return VBaseResponse.builder()
            .code(200)
            .message("成功采购合同")
            .build();
    }

    /**
     * 撤销该版本销售合同
     *
     * @param id 合同主键
     * @return 返回成功信息
     * @throws IOException 异常
     */
    @DeleteMapping("/contract/sales/{id}/revision")
    public VBaseResponse cancelSalesCurrentRevision(@PathVariable String id) throws Exception {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var contract = salesContractService.getContractDetail(id);
        if (contract.getState().equals(ContractState.FINISHED))
            return VBaseResponse.builder()
                .code(500)
                .message("该版本已撤销")
                .build();
        salesContractService.removeCurrentRevision(
            id,
            salesContractService.getMaxRevision(id),
            contract, session.getSession().getCompanyCode()
        );

        return VBaseResponse.builder()
            .code(200)
            .message("撤销该版本成功")
            .build();
    }

    /**
     * 撤销销售合同
     *
     * @param id 销售合同主键
     * @return 返回成功或者失败
     */
    @DeleteMapping("/contract/sales/{id}")
    public VBaseResponse removeSalesContract(@PathVariable String id) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        salesContractService.removeSalesContract(
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .code(200)
            .message("撤销成功")
            .build();
    }

    /**
     * 查看修改销售合同明细预览
     *
     * @param id       销售合同主键
     * @param revision 版本号
     * @return 返回销售合同预览列表
     */
    @GetMapping("/contract/sales/{id}/{revision}/preview")
    public VPContractPreviewResponse modifySalesContractPreview(
        @PathVariable("id") String id,
        @PathVariable("revision") Integer revision
    ) {

        var list = salesContractService.modifyContractPreview(id, revision);
        return VPContractPreviewResponse.builder()
            .code(200)
            .message("获取数据成功")
            .products(list)
            .build();
    }

    /**
     * 查询销售合同中已开票产品列表
     *
     * @param id 合同主键
     * @return 产品列表
     */
    @GetMapping("/contract/sales/{id}/invoiced")
    // TODO: 2022/6/2   需要重新完善
    public VInvoicedResponse getSaleInvoicedList(@PathVariable String id) {
        var products = purchaseContractService.getInvoicedList(id);
        return VInvoicedResponse.builder()
            .code(200)
            .message("获取数据成功")
            .products(products)
            .build();
    }

    /**
     * 查询采购合同中已收货产品列表
     *
     * @param id 合同主键
     * @return 产品列表
     */
    // TODO: 2022/6/2   需要重新完善
    @GetMapping("/contract/sales/{id}/delivered")
    public VDeliveredResponse getDeliveredList(@PathVariable String id) {
        var products = salesContractService.getReceivedList(id);
        return VDeliveredResponse.builder()
            .code(200)
            .message("获取数据成功")
            .products(products)
            .build();
    }

    /**
     * 保存收回临时记录
     *
     * @param deliveryTempRequests 收回临时记录列表
     * @param id                   合同主键
     * @param revision             合同版本
     * @return 返回成功信息
     */
    @PostMapping("/contract/sales/{id}/{revision}/delivery/record")
    public VBaseResponse saveSalesDeliveryTemp(
        @RequestBody List<VDeliveryTempRequest> deliveryTempRequests,
        @PathVariable("id") String id,
        @PathVariable("revision") Integer revision
    ) {

        salesContractService.saveDeliveryTemp(deliveryTempRequests, id, revision);
        return VBaseResponse.builder()
            .code(200)
            .message("保存成功")
            .build();
    }

    /**
     * 呼叫（向供应商询价）
     * @param id 询价单主键
     * @return 返回成功信息
     */
    @PostMapping("/inquiry/call/{id}")
    public VBaseResponse  inquiryPrice(@PathVariable String id) throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        inquiryService.inquiryPrice(id,
            session.getSession().getCompanyCode(),
            session.getSession().getCompanyName(),
            session.getSession().getOperatorCode());
        return  VBaseResponse.builder()
            .message("呼叫成功")
            .code(200)
            .build();
    }
}
