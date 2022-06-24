package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.entity.TemporaryPlanId;
import com.linzhi.gongfu.enumeration.ContractState;
import com.linzhi.gongfu.mapper.*;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.ContractService;
import com.linzhi.gongfu.service.InquiryService;
import com.linzhi.gongfu.service.PlanService;
import com.linzhi.gongfu.util.ExcelUtil;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ContractService contractService;
    private final ContractMapper contractMapper;

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
     * @return 返回成功信息
     */
    @PostMapping("/contract/temporary/purchase/plan")
    public VBaseResponse  saveTemporaryPlan(@RequestBody Optional<List<VTemporaryPlanRequest>> products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map=planService.saveTemporaryPlan(
            products.orElse(new ArrayList<>()),
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .message((String)map.get("message"))
            .code((Integer)map.get("code"))
            .build();
    }

    /**
     * 修改临时采购计划
     * @return 返回成功信息
     */
    @PutMapping("/contract/temporary/purchase/plan")
    public VBaseResponse  modifyTemporaryPlan(@RequestBody Optional<List<VTemporaryPlanRequest>> products){
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
     * @return 返回成功信息
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
     * @return  返回成功信息
     */
    @PostMapping("/contract/purchase/plan")
    public VBaseResponse savePlan(@RequestBody VPurchasePlanRequest products){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map =planService.savePurchasePlan(
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
     *
     */
    @GetMapping("/contract/purchase/plan/verification")
    public VBaseResponse verification(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        return  planService.verification(
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
    }

    /**
     * 采购计划
     * @return 采购计划信息
     */
    @GetMapping("/contract/purchase/plan")
    public VPurchasePlanResponse purchasePlan(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        return  planService.findPurchasePlanByCode(
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
     * @return 返回成功信息
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
     * @return 返回成功信息
     */
    @PutMapping("/contract/purchase/plan/forseveral")
    public VBaseResponse modifyPurchasePlanForSeveral(
        @RequestBody Optional<VPlanDemandRequest> forSeveral
    ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.modifyPurchasePlanForSeveral(
            session.getSession().getCompanyCode(),
            forSeveral.orElseGet(VPlanDemandRequest::new)
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
     * @return 返回成功信息
     */
    @PutMapping("/contract/purchase/plan/demand")
    public VBaseResponse modifyPurchasePlanDemand(@RequestBody Optional<VPlanDemandRequest> demand){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.modifyPurchasePlanDemand(
            session.getSession().getCompanyCode(),
            demand.orElseGet(VPlanDemandRequest::new)
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
     * @param request  产品计划需求数
     * @return 返回成功信息
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
     * @return 返回成功信息
     */
    @DeleteMapping("/contract/purchase/plan")
    public VBaseResponse deletePurchasePlan(@RequestParam("planCode") Optional<String> planCode){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = planService.deletePurchasePlan(
            session.getSession().getCompanyCode(),
            planCode.orElseGet(String::new)
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
    public VSuppliersResponse suppliersByPlanCode(@RequestParam("planCode") Optional<String> planCode) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var supplier = planService.findSuppliersByPlanCode(
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
     * @param request  采购计划号
     * @return 返回成功信息
     */
    @PostMapping("/contract/purchase/inquiry")
    public VBaseResponse savePurchaseInquiry(@RequestBody VPurchasePlanRequest request){
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
     * @return  返回成功信息
     */
    @PostMapping("/contract/purchase/plan/empty")
    public VBaseResponse saveEmptyPlan(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map =planService.savaEmptyPurchasePlan(
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
     * @return 询价单列表
     */
    @GetMapping("/contract/purchase/inquiry")
    public VInquiryListResponse inquiryList(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var list = inquiryService.inquiryList(
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),"0"
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
     * @param supplierCode 供应商编码
     * @return 返回未完成的询价单列表
     */
    @GetMapping("/contract/purchase/inquiry/unfinished")
    public VUnfinishedInquiryListResponse unfinishedInquiries(@RequestParam("supplierCode") String supplierCode){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var list = inquiryService.unfinishedInquiry(session.getSession().getCompanyCode(),
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
     * @param supplierCode 供应商编码
     * @param startTime  开始时间
     * @param endTime 结束时间
     * @param state 状态
     * @param pageNum 页码
     * @param pageSize 每页展示几条
     * @return 询价单历史列表
     */
    @GetMapping( "/contract/purchase/inquiry/history")
    public VInquiryPageResponse inquiryHistory(@RequestParam("supplierCode") Optional<String> supplierCode,
                                               @RequestParam("startTime") Optional<String> startTime,
                                               @RequestParam("endTime") Optional<String> endTime,
                                               @RequestParam("state") Optional<String> state,
                                               @RequestParam("pageNum") Optional<String> pageNum,
                                               @RequestParam("pageSize") Optional<String> pageSize
                                               ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        //分页
        Pageable pageable = PageRequest.of(
            pageNum.map(PageTools::verificationPageNum).orElse(0),
            pageSize.map(PageTools::verificationPageSize).orElse(10)
        );
        var page = inquiryService.inquiryHistoryPage(session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),supplierCode.orElse(""),startTime.orElse(""),endTime.orElse(""),
            state.orElse("1"),pageable);
        return  VInquiryPageResponse.builder()
            .code(200)
            .message("查询成功")
            .current(page.getNumber()+1)
            .total(Integer.parseInt(String.valueOf(page.getTotalElements())))
            .inquiries(page.stream().map(inquiryMapper::toVInquiryPage).toList())
            .build()
            ;
    }

    /**
     * 查询询价单详情
     * @param id 询价单主键
     * @return 返回询价单详细信息
     */
    @GetMapping("/contract/purchase/inquiry/{id}")
    public VInquiryDetailResponse inquiryDetail(@PathVariable("id") Optional<String> id){
        var inquiry =id.map(inquiryService::inquiryDetail)
            .map(inquiryMapper::toVInquiryDetail);
        if(inquiry.isPresent())
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
     * @return 询价单编码
     */
    @PostMapping("/contract/purchase/inquiry/empty")
    public VEmptyInquiryResponse emptyInquiry(@RequestBody VEmptyInquiryRequest vEmptyInquiryRequest){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var code = inquiryService.emptyInquiry(
            session.getSession().getCompanyCode(),
            session.getSession().getCompanyName(),
            session.getSession().getOperatorCode(),
            vEmptyInquiryRequest.getSupplierCode()
        );
        if(code==null)
            return VEmptyInquiryResponse.builder()
                .code(500)
                .message("新建空的询价单失败")
                .inquiryId("UNKNOWN")
                .build();
        return  VEmptyInquiryResponse.builder()
            .code(200)
            .message("新建询价单成功")
            .inquiryId(code)
            .build();
    }

    /**
     * 添加产品
     * @param id 询价单主键
     * @param vInquiryProductRequest 产品信息
     * @return 返回成功或者失败信息
     */
    @PostMapping("/contract/purchase/inquiry/{id}/product")
    public  VBaseResponse saveInquiryProduct(
        @PathVariable("id") String id,
        @RequestBody VInquiryProductRequest vInquiryProductRequest){
        var flag = inquiryService.saveInquiryProduct(
            id,
            vInquiryProductRequest.getProductId(),
            vInquiryProductRequest.getPrice(),
            vInquiryProductRequest.getAmount()
        );
        if(flag) {
            return VBaseResponse.builder()
                .code(200)
                .message("添加产品成功")
                .build();
        }
        return  VBaseResponse.builder()
            .code(500)
            .message("添加产品失败")
            .build();
    }


    /**
     * 导出产品
     * @param id 询价单id
     * @param response  HttpServletResponse
     */
    @GetMapping("/contract/purchase/inquiry/{id}/products/export")
    public  void  exportProduct(@PathVariable String id, HttpServletResponse response ){
        List<LinkedHashMap<String,Object>> database=inquiryService.exportProduct(id);
        ExcelUtil.exportToExcel(response,"询价单明细表",database);
    }

    /**
     * 删除询价单产品
     * @param codes 产品编码列表
     * @param id 询价单编码
     * @return 返回成功或者失败信息
     */
    @DeleteMapping("/contract/purchase/inquiry/{id}/product")
    public VBaseResponse deleteInquiryProduct(@RequestParam("codes")List<Integer> codes,@PathVariable("id")String id){
        var flag =  inquiryService.deleteInquiryProduct(id,codes);
        if(flag) {
            return VBaseResponse.builder()
                .code(200)
                .message("删除产品成功")
                .build();
        }
        return  VBaseResponse.builder()
            .code(500)
            .message("删除产品失败")
            .build();
    }

    /**
     * 修改询价单
     * @param modifyInquiryRequest 修改内容
     * @param id 询价单id
     * @return 返回成功或者失败信息
     */
    @PutMapping("/contract/purchase/inquiry/{id}")
    public VBaseResponse modifyInquiry(
        @RequestBody VModifyInquiryRequest modifyInquiryRequest,
        @PathVariable String id){
        var flag = inquiryService.modifyInquiry(
            modifyInquiryRequest,
            id);
        if(flag)
            return VBaseResponse.builder()
                .code(200)
                .message("修改成功")
                .build();
        return VBaseResponse.builder()
            .code(500)
            .message("修改失败")
            .build();
    }

    /**
     * 撤销询价单
     * @param id 询价单id
     * @return 返回成功或者失败信息
     */
    @DeleteMapping("/contract/purchase/inquiry/{id}")
    public  VBaseResponse deleteInquiry(@PathVariable String id ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = inquiryService.deleteInquiry(
            id,
            session.getSession().getCompanyCode()
        );
        if(flag)
            return VBaseResponse.builder()
                .code(200)
                .message("撤销成功")
                .build();
        return VBaseResponse.builder()
            .code(500)
            .message("撤销失败")
            .build();
    }

    /**
     * 生成采购合同
     * @param generateContractRequest 生成采购合同需要的参数实体
     * @return 返回成功或者失败
     */
    @PostMapping("/contract/purchase")
    public VBaseResponse saveContract(@RequestBody VGenerateContractRequest generateContractRequest) throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = true;
        String  contractCodes = "";
        if(!generateContractRequest.isEnforce())
            contractCodes =contractService.findContractProductRepeat(generateContractRequest.getInquiryId()).orElse("");
        if(!contractCodes.equals(""))
            return  VBaseResponse.builder()
                .code(201)
                .message("可能重复的合同有："+contractCodes)
                .build();
        flag =  contractService.saveContract(generateContractRequest,session.getSession().getCompanyCode(),session.getSession().getOperatorName(),session.getSession().getOperatorCode());
        if(flag)
            return VBaseResponse.builder()
                .code(200)
                .message("成功采购合同")
                .build();
        return  VBaseResponse.builder()
            .code(500)
            .message("生成采购合同失败")
            .build();
    }

    /**
     * 税率列表
     * @param type 类型 1-产品 2-服务
     * @return 返回税率列表信息
     */
    @GetMapping("/contract/taxRate")
    public VTaxRateResponse findTaxRate(@RequestParam("type")String type){
        var list = contractService.findTaxRates(type);
        return VTaxRateResponse.builder()
            .code(200)
            .message("获取税率列表成功")
            .taxRates(list)
            .build();
    }

    /**
     * 判断采购合同号是否重复
     * @param contractNo 本单位采购合同号
     * @return 是否重复信息
     */
    @GetMapping("/contract/purchase/contractNo")
    public VBaseResponse changeContractNoRepeated(@RequestParam("contractNo") Optional<String> contractNo){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = contractService.changeContractNoRepeated(contractNo.orElse(""),session.getSession().getCompanyCode(),"");
        if(flag)
            return VBaseResponse.builder().code(200).message("数据不重复").build();
        return VBaseResponse.builder().code(201).message("数据重复").build();
    }

    /**
     * 判断采购合同号是否重复
     * @param contractNo 本单位采购合同号
     * @param id 采购合同编码
     * @return 是否重复信息
     */
    @GetMapping("/contract/purchase/{id}/contractNo")
    public VBaseResponse changeContractNoRepeated(@RequestParam("contractNo") Optional<String> contractNo,
                                                  @PathVariable String id){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = contractService.changeContractNoRepeated(contractNo.orElse(""),session.getSession().getCompanyCode(),id);
        if(flag)
            return VBaseResponse.builder().code(200).message("数据不重复").build();
        return VBaseResponse.builder().code(201).message("数据重复").build();
    }

    /**
     * 根据条件查询采购合同列表（分页）
     * @param state 采购合同状态 0-未完成 1-已确定 2-撤销
     * @param supplierCode 供应商合同编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 每页展示几条
     * @return 采购合同列表分页
     */
    @GetMapping("/contract/purchase")
    public VPurchaseContractPageResponse contractPage(@RequestParam("supplierCode") Optional<String> supplierCode,
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
        var page = contractService.findContractPage(
            state.orElse("0"),
            supplierCode.orElse(""),
            startTime.orElse(""),
            endTime.orElse(""),
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            pageable
        );
        return  VPurchaseContractPageResponse.builder()
            .code(200)
            .message("查询成功")
            .current(page.getNumber()+1)
            .total(Integer.parseInt(String.valueOf(page.getTotalElements())))
            .contracts(page.stream().map(contractMapper::toContractPage).toList())
            .build()
            ;
    }

    /**
     * 根据采购合同主键和版本号查询合同详情
     * @param id 采购合同主键
     * @param revision 版本号
     * @return 返回合同详情
     * @throws IOException 异常
     */
    @GetMapping("/contract/purchase/{id}/{revision}")
    public VPurchaseContractDetailResponse purchaseContractDetail(
        @PathVariable("id")String id,
        @PathVariable("revision")Integer revision
    ) throws IOException {
        boolean repetitive =false;
         var contract= contractService.purchaseContractDetail(id,revision);
        if(revision >1&&contract.getState().equals(ContractState.UN_FINISHED.getState()+""))
            repetitive = contractService.judgeContractRev(id, revision);
        return VPurchaseContractDetailResponse.builder()
            .code(200)
            .message("获取数据成功")
            .contract(contract)
            .repetitive(repetitive)
            .build();
    }

    /**
     * 生成空的采购合同
     * @param supplierCode 供应商编码
     * @return 返回成功信息
     */
    @PostMapping("/contract/purchase/empty")
    public VEmptyContractResponse savePurchaseContractEmpty(@RequestBody Optional<VEmptyInquiryRequest> supplierCode){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var id = contractService.savePurchaseContractEmpty(
            supplierCode.get().getSupplierCode(),
            session.getSession().getCompanyCode(),
            session.getSession().getCompanyName(),
            session.getSession().getOperatorCode(),
            session.getSession().getOperatorName())
            .orElse("");
        if(id.isEmpty())
            return VEmptyContractResponse.builder()
                .code(500)
                .message("保存失败")
                .build();
        return  VEmptyContractResponse.builder()
            .code(200)
            .message("保存成功")
            .contractId(id)
            .build();
    }

    /**
     * 获取未确认的采购合同数量
     * @param supplierCode 供应商编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回未确认的采购合同数量
     */
    @GetMapping("/contract/purchase/unconfirmed")
    public VUnFinishedAmountResponse findUnfinishedAmount(
        @RequestParam("supplierCode") Optional<String> supplierCode,
        @RequestParam("startTime") Optional<String> startTime,
        @RequestParam("endTime") Optional<String> endTime
    ) throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        return VUnFinishedAmountResponse.builder()
            .code(200)
            .message("获取数据成功")
            .amount(contractService.getUnFinished(
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
     * @param product 产品信息
     * @param id 合同主键
     * @param revision 版本号
     * @return 添加成功或者失败的信息
     */
    @PostMapping("/contract/purchase/{id}/{revision}/product")
    public  VBaseResponse saveContractProduct(
        @RequestBody Optional<VInquiryProductRequest> product,
        @PathVariable("id")String id,
        @PathVariable("revision") Integer revision
    ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag=contractService.saveProduct(product.get().getProductId(),
            product.get().getPrice(),
            product.get().getAmount(),
            id,
            revision,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
            );
        if(flag)
            return VBaseResponse.builder()
                .code(200)
                .message("添加产品成功")
                .build();
        return VBaseResponse.builder()
            .code(500)
            .message("添加产品失败")
            .build();
    }

    /**
     * 删除产品
     * @param codes 明细序列号列表
     * @param id 合同主键
     * @param revision 版本号
     * @return 返回成功信息
     */
    @DeleteMapping("/contract/purchase/{id}/{revision}/product")
    public VBaseResponse deletePurchaseContract(
        @RequestParam("codes")List<Integer> codes,
        @PathVariable("id")String id,
        @PathVariable("revision") Integer revision
    ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = contractService.deleteContractProduct(
            codes,
            id,
            revision,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        if(flag)
            return VBaseResponse.builder()
                .code(200)
                .message("删除产品成功")
                .build();
        return VBaseResponse.builder()
            .code(500)
            .message("删除产品失败")
            .build();
    }

    /**
     * 修改合同状态
     * @param id 合同主键
     * @return 返回合同版本号
     */
    @PutMapping("/contract/purchase/{id}")
    public VContractRevisionResponse modifyContractState(@PathVariable String id) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var revision = contractService.modifyContractState(id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            contractService.findMaxRevision(id)
        );
        if(revision ==0)
            return VContractRevisionResponse.builder()
                .code(500)
                .revision(revision)
                .message("修改合同失败")
                .build();
        return VContractRevisionResponse.builder()
            .code(200)
            .revision(revision)
            .message("修改合同成功")
            .build();
    }

    /**
     * 修改采购合同
     * @param vModifyInquiryRequest 修改内容
     * @param id 合同主键
     * @param revision 版本
     * @return 返回成功或者失败
     */
    @PutMapping("/contract/purchase/{id}/{revision}")
    public VBaseResponse  modifyPurchaseContract(
        @RequestBody Optional<VModifyInquiryRequest> vModifyInquiryRequest,
        @PathVariable("id")String id,
        @PathVariable("revision")Integer revision
    ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = contractService.modifyPurchaseContract(
             vModifyInquiryRequest.orElse(new VModifyInquiryRequest()),
             id,
             revision,
             session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        if(!flag)
            return VBaseResponse.builder()
                .code(500)
                .message("修改合同失败")
                .build();
        return VBaseResponse.builder()
            .code(200)
            .message("修改合同成功")
            .build();
    }

    /**
     * 采购合同导出产品模板
     * @param id 合同id
     * @param response  HttpServletResponse
     */
    @GetMapping("/contract/purchase/{id}/{revision}/template/export")
    public  void  exportContractProductTemplate(
        @PathVariable String id,
        @PathVariable Integer revision,
        HttpServletResponse response
    ){
        List<LinkedHashMap<String,Object>> database=contractService.exportProductTemplate(id,revision);
        ExcelUtil.exportToExcel(response,"采购合同明细表",database);
    }

    /**
     * 采购合同导出产品明细
     * @param id 合同id
     * @param response  HttpServletResponse
     */
    @GetMapping("/contract/purchase/{id}/{revision}/products/export")
    public  void  exportContractProduct(
        @PathVariable String id,
        @PathVariable Integer revision,
        HttpServletResponse response
    ){
        List<LinkedHashMap<String,Object>> database=contractService.exportProduct(id,revision);
        ExcelUtil.exportToExcel(response,"采购合同明细表",database);
    }

    /**
     * 撤销该版本合同
     * @param id 合同主键
     * @return 返货成功信息
     * @throws IOException 异常
     */
    @DeleteMapping("/contract/purchase/{id}/revision")
    public VBaseResponse cancelCurrentRevision(@PathVariable String id) throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var contract = contractService.getContractDetail(id);
        if(contract.getState().equals(ContractState.FINISHED))
            return VBaseResponse.builder()
                .code(500)
                .message("该版本已撤销")
                .build();
        contractService.cancelCurrentRevision(
            id,
            contractService.findMaxRevision(id),
            contract,session.getSession().getCompanyCode()
        );

        return VBaseResponse.builder()
                .code(200)
                .message("撤销该版本成功")
                .build();
    }

    /**
     * 保存退回临时记录
     * @param deliveryTempRequests 退回临时记录列表
     * @param id 合同主键
     * @param revision 合同版本
     * @return 返回成功信息
     */
    @PostMapping("/contract/purchase/{id}/{revision}/delivery/record")
    public VBaseResponse saveDeliveryTemp(
        @RequestBody List<VDeliveryTempRequest> deliveryTempRequests,
        @PathVariable("id") String id,
        @PathVariable("revision") Integer revision
    ){
        contractService.saveDeliveryTemp(deliveryTempRequests,id,revision);
        return VBaseResponse.builder()
            .code(200)
            .message("保存成功")
            .build();
    }

    /**
     * 生成新一版的采购合同
     * @param id 采购合同id
     * @param revision 版本号
     * @return 返回成功或者失败
     */
    @PostMapping("/contract/purchase/{id}/{revision}")
    public VBaseResponse saveContractRevision(
        @PathVariable String id,
        @PathVariable Integer revision,
        @RequestBody VGenerateContractRequest generateContractRequest
    ) throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        String  contractCodes = "";
        if(!generateContractRequest.isEnforce())
            contractCodes =contractService.findContractProductRepeat(id,revision).orElse("");
        if(!contractCodes.equals(""))
            return  VBaseResponse.builder()
                .code(201)
                .message("可能重复的合同有："+contractCodes)
                .build();
        contractService.saveContractRevision(id,
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
     * @param id 合同主键
     * @return 产品列表
     */
    @GetMapping("/contract/purchase/{id}/invoiced")
    // TODO: 2022/6/2   需要重新完善
    public VInvoicedResponse getInvoicedList(@PathVariable String id){
       var products =  contractService.getInvoicedList(id);
        return VInvoicedResponse.builder()
            .code(200)
            .message("获取数据成功")
            .products(products)
            .build();
    }

    /**
     * 查询采购合同中已收货产品列表
     * @param id 合同主键
     * @return 产品列表
     */
    // TODO: 2022/6/2   需要重新完善
    @GetMapping("/contract/purchase/{id}/received")
    public VReceivedResponse getReceivedList(@PathVariable String id){
        var products = contractService.getReceivedList(id);
        return VReceivedResponse.builder()
            .code(200)
            .message("获取数据成功")
            .products(products)
            .build();
    }

    /**
     * 撤销采购合同
     * @param id 采购合同主键
     * @return 返回成功或者失败
     */
    @DeleteMapping("/contract/purchase/{id}")
    public VBaseResponse cancelPurchaseContract(@PathVariable String id ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        contractService.cancelPurchaseContract(
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
     * @param id 采购合同主键
     * @param revision 版本号
     * @return 返回采购合同预览列表
     */
    @GetMapping("/contract/purchase/{id}/{revision}/preview")
    public VModifyContractPreviewResponse modifyContractPreview(
        @PathVariable("id") String id,
        @PathVariable("revision") Integer revision
    ){
         var list=contractService.modifyContractPreview(id,revision);
         return VModifyContractPreviewResponse.builder()
             .code(200)
             .message("获取数据成功")
             .products(list)
             .build();
    }

    /**
     * 生成与该合同相同的新的未确认的采购合同
     * @param id 合同id
     * @param revision 版本
     * @return 返回新合同主键
     */
    @PostMapping("/contract/purchase/{id}/{revision}/copy")
    public VEmptyContractResponse copyContract(@PathVariable String id,@PathVariable Integer revision){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        String contractId = contractService.copyContract(
            id,
            revision,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );

        return VEmptyContractResponse.builder()
            .code(contractId==null?500:200)
            .message(contractId==null?"操作失败":"操作成功")
            .contractId(contractId)
            .build();
    }
}
