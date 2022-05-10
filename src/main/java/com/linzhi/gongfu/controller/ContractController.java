package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.entity.TemporaryPlanId;
import com.linzhi.gongfu.mapper.CompanyMapper;
import com.linzhi.gongfu.mapper.InquiryMapper;
import com.linzhi.gongfu.mapper.PurchasePlanMapper;
import com.linzhi.gongfu.mapper.TemporaryPlanMapper;
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
import org.springframework.web.multipart.MultipartFile;

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
    public VSuppliersResponse suppliersByPlancode(@RequestParam("planCode") Optional<String> planCode) {
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
                    .map(companyMapper::toPreloadSupliers)
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
            session.getSession().getOperatorCode(),session.getSession().getOperatorName()
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
            session.getSession().getOperatorCode()
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
                                               ) throws IOException {
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
        var inquiry =id.flatMap(inquiryService::inquiryDetail)
            .map(inquiryMapper::toInquiryDetail)
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
            session.getSession().getOperatorName(),
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
     * @param vInquiryProductResquest 产品信息
     * @return 返回成功或者失败信息
     */
    @PostMapping("/contract/purchase/inquiry/{id}/product")
    public  VBaseResponse saveInquiryProduct(
        @PathVariable("id") String id,
        @RequestBody VInquiryProductResquest vInquiryProductResquest){
        var flag = inquiryService.saveInquiryProduct(
            id,
            vInquiryProductResquest.getProductId(),
            vInquiryProductResquest.getPrice(),
            vInquiryProductResquest.getAmount()
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
     * 导入产品
     * @param file 导入文件
     * @param id 询价单id
     * @return 导入产品列表
     */
    @PostMapping("/contract/purchase/inquiry/{id}/products")
    public VImportProductTempResponse importProduct(@RequestParam("products") MultipartFile file,@PathVariable String id) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map = inquiryService.importProduct(
            file,
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        if((int) map.get("code")!=200)
            return VImportProductTempResponse.builder()
                .code((int) map.get("code"))
                .message((String) map.get("message"))
                .build();
        return getvImportProductTempResponse(id, session);
    }

    /**
     * 查询暂存产品详情
     * @param id 询价单id
     * @param session session
     * @return 返回暂存产品列表信息
     * @throws IOException 异常
     */
    private VImportProductTempResponse getvImportProductTempResponse(String id, OperatorSessionToken session) throws IOException {
        var map = inquiryService.findImportProductDetail(session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            id);
        var list =(List<VImportProductTempResponse.VProduct>) map.get("products");
        return VImportProductTempResponse.builder()
            .code(200)
            .message("产品导入临时表成功")
            .confirmable(list.stream().filter(vProduct -> vProduct.getMessages().size() > 0 || vProduct.getConfirmedBrand()==null).toList().size()==0)
            .products(list)
            .inquiryCode((String)map.get("inquiryCode"))
            .build();
    }

    /**
     * 查询导入的产品
     * @param id 询价单id
     * @return 返回导入产品列表
     */
    @GetMapping("/contract/purchase/inquiry/{id}/products")
    public VImportProductTempResponse findImportProduct(@PathVariable String id) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        return getvImportProductTempResponse(id, session);
    }

    /**
     * 修改导入产品
     * @param id 询价单id
     * @return 成功或者失败的信息
     */
    @PutMapping("/contract/purchase/inquiry/{id}/import/products")
    public VBaseResponse modifyImportProduct(@PathVariable String id,@RequestBody List<VImportProductTempRequest> vImportProductTempRequest){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map = inquiryService.modifyImportProduct(
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            vImportProductTempRequest
        );
        return VBaseResponse.builder()
            .code((int)map.get("code"))
            .message((String)map.get("message"))
            .build();
    }

    /**
     * 保存导入的产品
     * @param id 询价单id
     * @return 成功或者失败的信息
     */
    @PostMapping("/contract/purchase/inquiry/{id}/imports")
    public VBaseResponse saveImportProduct(@PathVariable String id){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var map = inquiryService.saveImportProducts(
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .code((int)map.get("code"))
            .message((String)map.get("message"))
            .build();
    }

    /**
     * 清空暂存的导入产品数据
     * @param id 询价单id
     * @return 返回成功或者失败信息
     */
    @DeleteMapping("/contract/purchase/inquiry/{id}/products")
    public VBaseResponse deleteImportProducts(@PathVariable("id")String id){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = inquiryService.deleteImportProducts(
            id,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        if(flag)
            return  VBaseResponse.builder()
                .code(200)
                .message("删除产品成功")
                .build();
        return  VBaseResponse.builder()
            .code(500)
            .message("删除产品失败")
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
        if(!generateContractRequest.isEnforce())
            flag =contractService.findContractProductRepeat(generateContractRequest.getInquiryId());
        if(!flag)
            return  VBaseResponse.builder()
                .code(201)
                .message("有相同的采购合同")
                .build();
        flag =  contractService.saveContract(generateContractRequest,session.getSession().getCompanyCode());
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
    public VBaseResponse changeContractNoRepeated(@RequestParam("contractNo") String contractNo ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var flag = contractService.changeContractNoRepeated(contractNo,session.getSession().getCompanyCode());
        if(flag)
            return VBaseResponse.builder().code(200).message("数据不重复").build();
        return VBaseResponse.builder().code(201).message("数据重复").build();
    }
}
