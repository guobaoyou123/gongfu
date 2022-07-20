package com.linzhi.gongfu.controller;


import com.linzhi.gongfu.dto.TOperatorInfo;
import com.linzhi.gongfu.entity.OperatorId;
import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.Whether;
import com.linzhi.gongfu.mapper.CompanyMapper;
import com.linzhi.gongfu.mapper.OperatorMapper;
import com.linzhi.gongfu.mapper.SceneMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.CompTradeApplyService;
import com.linzhi.gongfu.service.CompanyService;
import com.linzhi.gongfu.service.OperatorService;
import com.linzhi.gongfu.service.SceneService;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用于处理单位信息以及供应商、客户等信息
 *
 * @author zgh
 * @create_at 2022-01-28
 */
@RequiredArgsConstructor
@RestController
public class CompanyController {
    private final CompanyService companyService;
    private final CompanyMapper companyMapper;
    private final OperatorService operatorService;
    private final OperatorMapper operatorMapper;
    private final SceneService sceneService;
    private final SceneMapper sceneMapper;
    private final CompTradeApplyService compTradeApplyService;
    /**
     * 通过本公司id查询所有供应商以及经营，自营的品牌
     * @return 对应的本公司id查询所有供应商以及经营，自营的品牌信息
     */
    @GetMapping("/suppliers/paged")
    public VSuppliersIncludeBrandsResponse suppliersIncludeBrands(
        @RequestParam("pageNum") Optional<String> pageNum,
        @RequestParam("pageSize") Optional<String> pageSize
    ) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        var supplier = companyService.CompanyIncludeBrandById(session.getSession().getCompanyCode(),pageNum,pageSize);
        return VSuppliersIncludeBrandsResponse.builder()
               .code(200)
               .message("获取我的供应以及品牌列表成功。")
               .total(Integer.parseInt(String.valueOf(supplier.getTotalElements())))
               .current(supplier.getNumber()+1)
                .suppliers(supplier.getContent())
                .build();
    }

    /**
     * 查询本公司所有供应商
     * @param brands 品牌编码列表
     * @return 对应的本公司id查询所有供应商
     */
    @GetMapping("/suppliers/by/brand")
    public VSuppliersResponse suppliersByBrands(
        @RequestParam("brand") Optional<List<String>> brands
    ) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        var supplier = companyService.findSuppliersByBrands(
            brands.orElseGet(ArrayList::new),session.getSession().getCompanyCode()
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
     * 本单位的外供应商
     * @return 外供应商列表
     */
    @GetMapping("/suppliers")
    public VForeignSuppliersResponse foreignSuppliers(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var list = companyService.findForeignSuppliers(
            session.getSession().getCompanyCode()
        );
        return VForeignSuppliersResponse.builder()
            .code(200)
            .message("获取供应商列表成功")
            .suppliers(
                list.stream()
                    .map(companyMapper::toForeignSupplier)
                    .toList()
            )
            .build();
    }

    /**
     * 本单位的外供应商的详情
     * @return 外供应商列表
     */
    @GetMapping("/supplier/{code}")
    public VSupplierDetailResponse foreignSupplierDetail(@PathVariable String code){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var supplier = companyService.findForeignSupplierDetail(code,session.getSession().getCompanyCode());
        return VSupplierDetailResponse.builder()
            .code(200)
            .message("获取供应商详情成功")
            .supplier(supplier)
            .build();
    }

    /**
     * 保存外供应商
     * @param supplier 供应商信息
     * @return  成功或者失败信息
     */
    @PostMapping("/supplier")
    public VBaseResponse saveForeignSupplier(@RequestBody VForeignSupplierRequest supplier){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var map = companyService.saveForeignSupplier(
            supplier,
            session.getSession().getCompanyCode(),
            null
        );
        return VBaseResponse.builder()
            .code((int)map.get("code"))
            .message((String)map.get("message"))
            .build();
    }

    /**
     * 修改外供应商
     * @param supplier 供应商信息
     * @return  成功或者失败信息
     */
    @PutMapping("/supplier/{code}")
    public VBaseResponse modifyForeignSupplier(@PathVariable("code")String code,
                                               @RequestBody VForeignSupplierRequest supplier
    ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var map = companyService.saveForeignSupplier(
            supplier,
            session.getSession().getCompanyCode(),
            code
        );
        return VBaseResponse.builder()
            .code((int)map.get("code"))
            .message((String)map.get("message"))
            .build();
    }

    /**
     * 停用外供应商
     * @param supplier 供应商信息
     * @return  成功或者失败信息
     */
    @PutMapping("/supplier/disable")
    public VBaseResponse foreignSupplierDisable(@RequestBody VForeignSupplierRequest supplier){
        var flag = companyService.modifySupplierState(supplier.getCodes(), Availability.DISABLED);
        if(flag)
            return VBaseResponse.builder()
                .code(200)
                .message("操作成功")
                .build();
        return VBaseResponse.builder()
            .code(500)
            .message("操作失败")
            .build();
    }

    /**
     * 启用外供应商
     * @param supplier 供应商信息
     * @return  成功或者失败信息
     */
    @PutMapping("/supplier/enable")
    public VBaseResponse foreignSupplierEnable(@RequestBody VForeignSupplierRequest supplier){
        var flag = companyService.modifySupplierState(supplier.getCodes(), Availability.ENABLED);
        if(flag)
            return VBaseResponse.builder()
                .code(200)
                .message("操作成功")
                .build();
        return VBaseResponse.builder()
             .code(500)
             .message("操作失败")
            .build();
    }

    /**
     * 验证社会统一信用代码
     * @param usci  社会统一信用代码
     * @return 返回公司名称
     */
    @GetMapping("/supplier/verification")
    public VUCSIVerificationResponse supplierVerification(@RequestParam("usci") String usci){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        Map<String,Object> map =companyService.supplierVerification(
            usci,
            session.getSession().getCompanyCode()
        );
        return VUCSIVerificationResponse.builder()
            .code((int)map.get("code"))
            .message((String)map.get("message"))
            .companyname((String) map.get("companyName"))
            .build();
    }

    /**
     * 本单位的外供应商的详情
     * @return 外供应商列表
     */
    @GetMapping("/company/detail")
    public VCompanyDetailResponse companyDetail() throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var company = companyService.findCompanyDetail(session.getSession().getCompanyCode());
        return VCompanyDetailResponse.builder()
            .code(200)
            .message("获取供应商详情成功")
            .company(company)
            .build();
    }

    /**
     * 修改本公司
     * @param company 本公司信息
     * @return  成功或者失败信息
     */
    @PutMapping("/company/detail")
    public VBaseResponse modifyCompany(@RequestBody VCompanyRequest company
    ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();

        var flag = companyService.shortNameRepeat(session.getSession().getCompanyCode(),company.getCompanyShortName());
        if(flag)
            return VBaseResponse.builder()
                .code(201)
                .message("公司简称重复")
                .build();
        var str = companyService.saveCompanyDetail(
            company,
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .code(str!=null?200:500)
            .message(str!=null?"数据修改成功":"修改失败")
            .build();
    }

    /**
     * 设置格友可见
     * @param visibleContent 设置是否可见信息
     * @return 返回成功信息
     */
    @PostMapping("/company/visible")
    public VBaseResponse setVisible(@RequestBody VCompanyVisibleRequest visibleContent){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var flag = companyService.setVisible(session.getSession().getCompanyCode(),visibleContent);
        return  VBaseResponse.builder()
            .code(flag?200:500)
            .message(flag?"设置成功":"设置失败")
            .build();
    }

    /**
     * 获取人员列表分页
     * @param pageNum 页码
     * @param pageSize 每页显示几条
     * @param state 状态
     * @return 返回人员信息列表
     */
    @GetMapping("/company/operators")
    public VOperatorPageResponse operatorPage(@RequestParam("pageNum") Optional<String> pageNum ,
                                              @RequestParam("pageSize") Optional<String> pageSize ,
                                              @RequestParam("state") Optional<String> state,
                                              @RequestParam("keyword") Optional<String> keyword){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var page=operatorService.getOperatorPage(PageRequest.of(
            pageNum.map(PageTools::verificationPageNum).orElse(0),
            pageSize.map(PageTools::verificationPageSize).orElse(10)
        ),session.getSession().getCompanyCode(),state.orElse("1"),
            keyword.orElse("")
        );
        return  VOperatorPageResponse.builder()
            .code(200)
            .message("数据成功")
            .current(page.getNumber()+1)
            .total(Integer.parseInt(String.valueOf(page.getTotalElements())))
            .operators(page.getContent())
            .build();
    }

    /**
     * 操作员详情
     * @param code 操作员编码
     * @return 操作员详细信息
     */
    @GetMapping("/company/operator/detail/{code}")
    public  VOperatorDetailResponse operatorDetail(@PathVariable String code) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var operator = operatorService.findOperatorDetail(session.getSession().getCompanyCode(),code)
            .map(operatorMapper::toOperatorDetailDTOs).orElseThrow();
        return VOperatorDetailResponse.builder()
             .code(200)
             .message("获取数据成功")
             .operator(operator)
             .build();
    }

    /**
     * 修改人员基本信息
     * @param operator 操作员信息
     * @param code 操作员编码
     * @return 返回成功或者失败信息
     */
    @PutMapping("/company/operator/detail/{code}")
    public VBaseResponse modifyOperator(@RequestBody VOperatorRequest operator,@PathVariable String code){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var flag = operatorService.modifyOperator(session.getSession().getCompanyCode(),code,operator);
        return  VBaseResponse.builder()
            .code(flag?200:500)
            .message(flag?"修改成功":"修改失败")
            .build();
    }

    /**
     * 添加人员信息
     * @param operator 人员信息
     * @return 返回添加成功或者失败信息
     */
    @PostMapping("/company/operator/detail")
    public VResetPasswordResponse addOperator(@RequestBody VOperatorRequest operator){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var password = operatorService.addOperator(session.getSession().getCompanyCode(),operator);
        return  VResetPasswordResponse.builder()
            .code(password!=null?200:500)
            .message(password!=null?"添加成功":"添加失败")
            .password(password)
            .build();
    }

    /**
     * 获取场景列表
     * @return 返回场景列表
     */
    @GetMapping("/company/scenes")
    public VSceneListResponse findScenes() throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var scenes = sceneService.findScenes(session.getSession().getCompanyCode())
            .stream().map(sceneMapper::toDTO)
            .map(sceneMapper::toVScene).toList();
        return VSceneListResponse.builder()
            .code(200)
            .message("获取数据成功")
            .scenes(scenes)
            .build();
    }

    /**
     * 修改人员场景
     * @param operatorRequests 人员场景信息
     * @return 返回修改成功信息
     */
    @PutMapping("/company/operator/{code}/detail/scene")
    public VBaseResponse modifyOperatorScene(@RequestBody VOperatorRequest operatorRequests,
                                             @PathVariable String code){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var flag = operatorService.modifyOperatorScene(session.getSession().getCompanyCode(),operatorRequests,code);
        return VBaseResponse.builder()
            .code(flag?200:500)
            .message(flag?"操作成功":"操作失败")
            .build();
    }

    /**
     * 重置密码
     * @param code 操作员编码
     * @return 返回成功信息
     */
    @PostMapping("/company/operator/detail/{code}/password")
    public VResetPasswordResponse resetPassword(@PathVariable String code) throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        TOperatorInfo operatorInfo= operatorService.findOperatorByID(
            OperatorId.builder()
                .companyCode(session.getSession().getCompanyCode())
                .operatorCode(session.getSession().getOperatorCode())
            .build()
        ).orElseThrow(()->new Exception("设置失败未查询到"));
        if(operatorInfo.getAdmin().equals(Whether.NO))
            throw new Exception("该用户无该操作权限");
        var newPassword = operatorService.resetPassword(
            session.getSession().getCompanyCode(),
            code,null
        ).orElseThrow(()->new Exception("设置失败"));
        return VResetPasswordResponse.builder()
            .code(200)
            .message("操作成功")
            .password(newPassword)
            .build();
    }

    /**
     * 人员权限统计列表
     * @return 返回人员权限列表
     */
    @GetMapping("/company/operator/scenes/statistics")
    public VOperatorListResponse authorityStatistics(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var list = operatorService.findOperatorList(session.getSession().getCompanyCode())
            .stream().map(operatorMapper::toOperatorDTOs)
            .toList();
        return VOperatorListResponse.builder()
            .code(200)
            .message("获取数据成功")
            .operators(list)
            .build();
    }

    /**
     * 查询入格单位信息列表分页
     * @param name 公司名称
     * @param pageNum 页数
     * @param pageSize 每页展示几条
     * @return 入格单位信息列表
     */
    @GetMapping("/enrolled/companies")
    public VEnrolledCompanyPageResponse findCompanyPage(@RequestParam("name") Optional<String> name,
                                                        @RequestParam("pageNum") Optional<String> pageNum,
                                                        @RequestParam("pageSize") Optional<String> pageSize ){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var page = companyService.findEnrolledCompanyPage(
            name.orElse(""),
            PageRequest.of(pageNum.map(PageTools::verificationPageNum).orElse(0),
                pageSize.map(PageTools::verificationPageSize).orElse(10)),
            session.getSession().getCompanyCode()
        );
        return VEnrolledCompanyPageResponse.builder()
            .code(200)
            .message("获取数据成功")
            .current(page.getNumber()+1)
            .total(Integer.parseInt(String.valueOf(page.getTotalElements())))
            .companies(page.getContent())
            .build();
    }

    /**
     * 查询格友可见详情
     * @param invitationCode 邀请码
     * @param code 格友编码
     * @return 格友可见详情
     * @throws IOException 异常
     */
    @GetMapping("/enrolled/company/detail")
    public VEnrolledCompanyDetailResponse findEnrolledCompanyDetail(@RequestParam("invitationCode") Optional<String> invitationCode,
                                                                    @RequestParam("code")Optional<String> code) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        //判断邀请码是否不为空，找到邀请单位详情
        if(!invitationCode.orElse("").equals("")){
            code =   companyService.findInvitationCode(invitationCode.get());
        }
        if(code.orElse("").equals(""))
            return VEnrolledCompanyDetailResponse.builder()
                .code(404)
                .message("邀请码已过期或邀请码错误")
                .build();
        //查询格友单位详情
        var company = companyService.findEnrolledCompany(
            code.get(),session.getSession().getCompanyCode())
            .orElseThrow(()->new IOException("未从数据库找到"));
        return VEnrolledCompanyDetailResponse.builder()
            .code(200)
            .message("获取数据成功")
            .company(company)
            .build();
    }

    /**
     * 拒绝名单中的格友详情
     * @param code 格友编码
     * @return 格友详情
     * @throws IOException 异常
     */
    @GetMapping("/enrolled/company/apply/refused/detail/{code}")
    public VEnrolledCompanyDetailResponse findRefuseEnrolledCompanyDetail(@PathVariable Optional<String> code) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        //查询格友单位详情
        var company = companyService.findRefuseEnrolledCompanyDetail(
                code.get(),session.getSession().getCompanyCode())
            .orElseThrow(()->new IOException("未从数据库找到"));
        return VEnrolledCompanyDetailResponse.builder()
            .code(200)
            .message("获取数据成功")
            .company(company)
            .build();
    }

    /**
     * 申请采购
     * @param vTradeApplyRequest 申请采购信息
     * @return 返回成功信息或者失败信息
     */
    @PostMapping("/enrolled/company/apply")
   public VBaseResponse tradeApply(@RequestBody Optional<VTradeApplyRequest> vTradeApplyRequest){
       OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
           .getContext()
           .getAuthentication();
       var flag = compTradeApplyService.tradeApply(vTradeApplyRequest.orElseThrow(()->new NullPointerException("数据为空")),
           session.getSession().getCompanyCode(),
           session.getSession().getOperatorCode(),
           session.getSession().getCompanyName());
       return VBaseResponse.builder()
           .code(flag?200:500)
           .message(flag?"操作成功":"操作失败")
           .build();
   }

    /**
     * 查看待处理列表
     * @param name 公司名称
     * @param pageNum 页数
     * @param pageSize 每页展示几条
     * @return 待处理申请列表
     */
   @GetMapping("/enrolled/company/apply")
   public VTradeApplyPageResponse findTradeApply(@RequestParam("name") Optional<String> name,
                                                      @RequestParam("pageNum") Optional<String> pageNum,
                                                      @RequestParam("pageSize") Optional<String> pageSize ){
       OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
           .getContext()
           .getAuthentication();
       var page = compTradeApplyService.findTradeApply(
           session.getSession().getCompanyCode(),
           PageRequest.of(pageNum.map(PageTools::verificationPageNum).orElse(0),
               pageSize.map(PageTools::verificationPageSize).orElse(10)),
           name.orElse("")
       );
       return VTradeApplyPageResponse.builder()
           .code(200)
           .message("获取数据成功")
           .current(page.getNumber()+1)
           .total(Integer.parseInt(String.valueOf(page.getTotalElements())))
           .applies(page.getContent())
           .build();
   }

    /**
     * 同意申请
     * @param code 申请记录编码
     * @return 返回成功或者失败信息
     */
   @PostMapping("/enrolled/company/apply/{code}/pass")
    public  VBaseResponse  consentApply(@PathVariable String code,@RequestBody Optional<VTradeApplyConsentRequest> vTradeApplyConsentRequest){


       return  null;
   }
}

