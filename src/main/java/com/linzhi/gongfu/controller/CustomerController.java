package com.linzhi.gongfu.controller;


import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.CompanyRole;
import com.linzhi.gongfu.mapper.CompanyMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.CompanyService;
import com.linzhi.gongfu.service.NotificationService;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 用于处理客户信息
 *
 * @author zgh
 * @create_at 2022-08-24
 */
@RequiredArgsConstructor
@RestController
public class CustomerController {
    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

    /**
     * 入格客户列表
     *
     * @param name     公司名称
     * @param pageNum  页码
     * @param pageSize 每页展示几条
     * @return 返回客户列表
     */
    @GetMapping("/customers/enrolled/page")
    public VEnrolledTradeCompaniesResponse pageEnrolledCustomers(
        @RequestParam("name") Optional<String> name,
        @RequestParam("pageNum") Optional<String> pageNum,
        @RequestParam("pageSize") Optional<String> pageSize) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var page = companyService.pageTradeCompanies(
            name.orElse(""),
            pageNum.map(PageTools::verificationPageNum).orElse(0),
            pageSize.map(PageTools::verificationPageSize).orElse(10),
            session.getSession().getCompanyCode(), "2", session.getSession().getIsAdmin(), "1", session.getSession().getOperatorCode(), CompanyRole.SUPPLIER.getSign()
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
     * 入格客户详细信息
     *
     * @param code 入格单位编码
     * @return 客户详细信息
     */
    @GetMapping("/customer/enrolled/{code}")
    public VEnrolledTradeCompanyResponse customerDetail(@PathVariable String code) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var customer = companyService.enrolledCustomer(code, session.getSession().getCompanyCode())
            .map(companyMapper::toTEnrolledTradeCompany)
            .orElseThrow(() -> new IOException("数据为空"));
        return VEnrolledTradeCompanyResponse.builder()
            .code(200)
            .message("获取数据成功")
            .company(customer)
            .build();
    }

    /**
     * 客户授权操作员
     *
     * @param code      客户编码
     * @param operators 授权操作员编码（以逗号隔开）
     * @return 返回成功或者失败信息
     */
    @PostMapping("/customer/{code}/operators")
    public VBaseResponse authorizedOperator(@PathVariable String code, @RequestBody Optional<VAuthorizedOperatorRequest> operators) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        companyService.authorizedOperator(session.getSession().getCompanyCode(), code, operators.orElseThrow(() -> new NullPointerException("数据未空")).getOperators(), "2");
        return VBaseResponse.builder()
            .code(200)
            .message("保存数据成功")
            .build();
    }

    /**
     * 修改交易品牌
     *
     * @param code   客户编码
     * @param brands 品牌列表
     * @return 返回成功或者失败信息
     */
    @PutMapping("/customer/{code}/brands")
    public VBaseResponse modifyTradeBrands(@PathVariable String code, @RequestBody Optional<VTradeInforRequest> brands) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        companyService.modifyTradeBrands(session.getSession().getCompanyCode(), code, brands.orElseThrow(() -> new NullPointerException("数据未空")).getBrands());
        return VBaseResponse.builder()
            .code(200)
            .message("保存数据成功")
            .build();
    }

    /**
     * 修改交易报价模式
     *
     * @param code     客户编码
     * @param taxModel 报价模式
     * @return 返回成功或者失败信息
     */
    @PutMapping("/customer/{code}/taxmodel")
    public VBaseResponse modifyTradeTaxModel(@PathVariable String code, @RequestBody Optional<VTradeInforRequest> taxModel) throws Exception {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        companyService.modifyTradeTaxModel(
            session.getSession().getCompanyCode(),
            code,
            taxModel.orElseThrow(() -> new NullPointerException("数据未空")).getTaxmodel(),
            session.getSession().getCompanyName(),
            session.getSession().getOperatorCode()
        );
        return VBaseResponse.builder()
            .code(200)
            .message("保存数据成功")
            .build();
    }

    /**
     * 本单位的外客户列表
     *
     * @return 外客户列表
     */
    @GetMapping("/customers/page")
    public VForeignCustomerPageResponse foreignSuppliers(@RequestParam("name") Optional<String> name,
                                                         @RequestParam("pageNum") Optional<String> pageNum,
                                                         @RequestParam("pageSize") Optional<String> pageSize,
                                                         @RequestParam("state") Optional<String> state) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var page = companyService.pageTradeCompanies(

            name.orElse(""),
            pageNum.map(PageTools::verificationPageNum).orElse(0),
            pageSize.map(PageTools::verificationPageSize).orElse(10),
            session.getSession().getCompanyCode(), "2", session.getSession().getIsAdmin(), state.orElse("1"),
            session.getSession().getOperatorCode(), CompanyRole.EXTERIOR_CUSTOMER.getSign()

        );
        return VForeignCustomerPageResponse.builder()
            .code(200)
            .message("数据获取成功")
            .current(page.getNumber() + 1)
            .total(Integer.parseInt(String.valueOf(page.getTotalElements())))
            .customers(page.getContent().stream().map(companyMapper::toVForeignCustomer).toList())
            .build();
    }

    /**
     * 本单位的外客户的详情
     *
     * @return 外客户列表
     */
    @GetMapping("/customer/{code}")
    public VForeignCustomerResponse foreignCustomerDetail(@PathVariable String code) throws IOException {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var customer = companyService.getForeignCustomerDetail(code, session.getSession().getCompanyCode());
        return VForeignCustomerResponse.builder()
            .code(200)
            .message("获取客户详情成功")
            .customer(customer)
            .build();
    }

    /**
     * 保存外客户
     *
     * @param customer 客户信息
     * @return 成功或者失败信息
     */
    @PostMapping("/customer")
    public VBaseResponse saveForeignCustomer(@RequestBody Optional<VForeignCompanyRequest> customer) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var map = companyService.judgeCompanyExists(null,
            session.getSession().getCompanyCode(),
            CompanyRole.EXTERIOR_CUSTOMER,
            customer.orElseThrow(() -> new NullPointerException("数据为空")).getUsci());
        if (((int) map.get("code")) == 201)
            return VBaseResponse.builder()
                .code((int) map.get("code"))
                .message((String) map.get("message"))
                .build();
        companyService.saveForeignCompany(
            customer.orElseThrow(() -> new NullPointerException("数据为空")),
            session.getSession().getCompanyCode(),
            null, CompanyRole.EXTERIOR_CUSTOMER
        );
        return VBaseResponse.builder()
            .code(200)
            .message("数据保存成功")
            .build();
    }

    /**
     * 修改外客户
     *
     * @param customer 客户信息
     * @return 成功或者失败信息
     */
    @PutMapping("/customer/{code}")
    public VBaseResponse modifyForeignCustomer(@PathVariable("code") String code,
                                               @RequestBody VForeignCompanyRequest customer
    ) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        companyService.saveForeignCompany(
            customer,
            session.getSession().getCompanyCode(),
            code,
            CompanyRole.EXTERIOR_CUSTOMER
        );
        return VBaseResponse.builder()
            .code(200)
            .message("数据修改成功")
            .build();
    }

    /**
     * 停用启用外客户
     *
     * @param state 状态 0-禁用 1-启用
     * @return 成功或者失败信息
     */
    @PutMapping("/customer/{code}/state")
    public VBaseResponse foreignCustomerDisable(@PathVariable String code, @RequestBody VForeignCompanyRequest state) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var flag = companyService.modifyTradeState(
            List.of(code),
            state.getState().equals("1") ? Availability.ENABLED : Availability.DISABLED,
            session.getSession().getCompanyCode(),
            CompanyRole.EXTERIOR_CUSTOMER
        );
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
     * 查询所有我负责的客户列表
     *
     * @param name 客户公司名称
     * @return 客户列表
     */
    @GetMapping("/customers")
    public VCustomersResponse findCustomers(@RequestParam("name") Optional<String> name) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var customers = companyService.findAllCustomer(
                name.orElse(""),
                session.getSession().getCompanyCode(),
                session.getSession().getOperatorCode(),
                session.getSession().getIsAdmin()
            ).stream()
            .map(companyMapper::toPreloadCustomer)
            .toList();
        return VCustomersResponse.builder()
            .code(200)
            .message("获取数据成功")
            .customers(customers)
            .build();
    }
}
