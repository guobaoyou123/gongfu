package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.mapper.CompanyMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.CompanyService;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用于处理供应商信息
 *
 * @author zgh
 * @create_at 2022-01-28
 */
@RequiredArgsConstructor
@RestController
public class SuppliersController {
    private final CompanyService companyService;
    private final CompanyMapper companyMapper;


    /**
     * 通过本公司id查询所有供应商以及经营，自营的品牌
     *
     * @return 对应的本公司id查询所有供应商以及经营，自营的品牌信息
     */
    @GetMapping("/suppliers/paged")
    public VSuppliersPageResponse pageSuppliers(
        @RequestParam("pageNum") Optional<String> pageNum,
        @RequestParam("pageSize") Optional<String> pageSize
    ) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var supplier = companyService.pageSuppliers(
            session.getSession().getCompanyCode(),
            pageNum,
            pageSize
        );
        return VSuppliersPageResponse.builder()
            .code(200)
            .message("获取我的供应以及品牌列表成功。")
            .total(Integer.parseInt(String.valueOf(supplier.getTotalElements())))
            .current(supplier.getNumber() + 1)
            .suppliers(supplier.getContent())
            .build();
    }

    /**
     * 查询本公司所有供应商
     *
     * @param brands 品牌编码列表
     * @return 对应的本公司id查询所有供应商
     */
    @GetMapping("/suppliers/by/brand")
    public VSuppliersResponse listSuppliersByBrands(
        @RequestParam("brand") Optional<List<String>> brands
    ) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        var supplier = companyService.listSuppliersByBrands(
            brands.orElseGet(ArrayList::new), session.getSession().getCompanyCode()
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
     *
     * @return 外供应商列表
     */
    @GetMapping("/suppliers")
    public VForeignSuppliersResponse foreignSuppliers() {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var list = companyService.listForeignSuppliers(
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
     *
     * @return 外供应商列表
     */
    @GetMapping("/supplier/{code}")
    public VForeignSupplierResponse foreignSupplierDetail(@PathVariable String code) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var supplier = companyService.getForeignSupplierDetail(code, session.getSession().getCompanyCode());
        return VForeignSupplierResponse.builder()
            .code(200)
            .message("获取供应商详情成功")
            .supplier(supplier)
            .build();
    }

    /**
     * 保存外供应商
     *
     * @param supplier 供应商信息
     * @return 成功或者失败信息
     */
    @PostMapping("/supplier")
    public VBaseResponse saveForeignSupplier(@RequestBody VForeignSupplierRequest supplier) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var map = companyService.saveForeignSupplier(
            supplier,
            session.getSession().getCompanyCode(),
            null
        );
        return VBaseResponse.builder()
            .code((int) map.get("code"))
            .message((String) map.get("message"))
            .build();
    }

    /**
     * 修改外供应商
     *
     * @param supplier 供应商信息
     * @return 成功或者失败信息
     */
    @PutMapping("/supplier/{code}")
    public VBaseResponse modifyForeignSupplier(@PathVariable("code") String code,
                                               @RequestBody VForeignSupplierRequest supplier
    ) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var map = companyService.saveForeignSupplier(
            supplier,
            session.getSession().getCompanyCode(),
            code
        );
        return VBaseResponse.builder()
            .code((int) map.get("code"))
            .message((String) map.get("message"))
            .build();
    }

    /**
     * 停用外供应商
     *
     * @param supplier 供应商信息
     * @return 成功或者失败信息
     */
    @PutMapping("/supplier/disable")
    public VBaseResponse foreignSupplierDisable(@RequestBody VForeignSupplierRequest supplier) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var flag = companyService.modifySupplierState(supplier.getCodes(), Availability.DISABLED, session.getSession().getCompanyCode(), "1");
        if (flag)
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
     *
     * @param supplier 供应商信息
     * @return 成功或者失败信息
     */
    @PutMapping("/supplier/enable")
    public VBaseResponse foreignSupplierEnable(@RequestBody VForeignSupplierRequest supplier) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var flag = companyService.modifySupplierState(supplier.getCodes(), Availability.ENABLED, session.getSession().getCompanyCode(), "1");
        if (flag)
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
     *
     * @param usci 社会统一信用代码
     * @return 返回公司名称
     */
    @GetMapping("/supplier/verification")
    public VUCSIVerificationResponse supplierVerification(@RequestParam("usci") String usci) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        Map<String, Object> map = companyService.supplierVerification(
            usci,
            session.getSession().getCompanyCode()
        );
        return VUCSIVerificationResponse.builder()
            .code((int) map.get("code"))
            .message((String) map.get("message"))
            .companyname((String) map.get("companyName"))
            .build();
    }

    /**
     * 查询入格的供应商列表
     *
     * @param name     入格的供应商单位名称
     * @param pageNum  页数
     * @param pageSize 每页展示几条
     * @return 返回入格的供应商列表
     */
    @GetMapping("/suppliers/enrolled")
    public VEnrolledTradeCompaniesResponse enrolledSupplierPage(
        @RequestParam("name") Optional<String> name,
        @RequestParam("pageNum") Optional<String> pageNum,
        @RequestParam("pageSize") Optional<String> pageSize) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var page = companyService.pageEnrolledTradeCompanies(
            name.orElse(""),
            pageNum.map(PageTools::verificationPageNum).orElse(0),
            pageSize.map(PageTools::verificationPageSize).orElse(10),
            session.getSession().getCompanyCode(), "1"
        );
        return VEnrolledTradeCompaniesResponse.builder()
            .code(200)
            .message("数据获取成功")
            .current(page.getNumber() + 1)
            .total(Integer.parseInt(String.valueOf(page.getTotalElements())))
            .companies(page.getContent().stream().map(companyMapper::toVEnrolledTradeCompanies).toList())
            .build();
    }

    /**
     * 入格供应商详细信息
     *
     * @param code 入格单位编码
     * @return 供应商详细信息
     */
    @GetMapping("/supplier/enrolled/{code}")
    public VEnrolledTradeCompanyResponse supplierDetail(@PathVariable String code) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var supplier = companyService.enrolledSupplier(code, session.getSession().getCompanyCode())
            .map(companyMapper::toTEnrolledTradeCompany)
            .orElseThrow(() -> new IOException("数据为空"));
        return VEnrolledTradeCompanyResponse.builder()
            .code(200)
            .message("获取数据成功")
            .company(supplier)
            .build();
    }

    /**
     * 格友供应商授权操作员
     *
     * @param code      供应商编码
     * @param operators 授权操作员编码（以逗号隔开）
     * @return 返回成功或者失败信息
     */
    @PostMapping("/supplier/{code}/operators")
    public VBaseResponse authorizedOperator(@PathVariable String code, @RequestBody Optional<VAuthorizedOperatorRequest> operators) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        companyService.authorizedOperator(code, session.getSession().getCompanyCode(), operators.orElseThrow(() -> new NullPointerException("数据未空")).getOperators(), "1");
        return VBaseResponse.builder()
            .code(200)
            .message("保存数据成功")
            .build();
    }

}
