package com.linzhi.gongfu.controller;


import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.CompanyRole;
import com.linzhi.gongfu.enumeration.NotificationType;
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
    private final NotificationService notificationService;

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
        var page = companyService.pageEnrolledTradeCompanies(
            name.orElse(""),
            pageNum.map(PageTools::verificationPageNum).orElse(0),
            pageSize.map(PageTools::verificationPageSize).orElse(10),
            session.getSession().getCompanyCode(), "2"
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
    public VBaseResponse modifyTradeTaxModel(@PathVariable String code, @RequestBody Optional<VTradeInforRequest> taxModel) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var operators = companyService.modifyTradeTaxModel(
            session.getSession().getCompanyCode(),
            code,
            taxModel.orElseThrow(() -> new NullPointerException("数据未空")).getTaxmodel()
        );
        if (operators == null)
            return VBaseResponse.builder()
                .code(500)
                .message("保存数据失败")
                .build();
        notificationService.saveNotification(
            session.getSession().getCompanyCode(),
            session.getSession().getCompanyName() + "修改了你们之间的交易税模式，请前往入格供应商管理模块查看",
            session.getSession().getOperatorCode(),
            NotificationType.MODIFY_TRADE,
            session.getSession().getCompanyCode(),
            code,
            null,
            operators.split(",")
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
        var page = companyService.pageForeignCustomers(
            session.getSession().getCompanyCode(),
            session.getSession().getOperatorCode(),
            name.orElse(""),
            pageNum.map(PageTools::verificationPageNum).orElse(0),
            pageSize.map(PageTools::verificationPageSize).orElse(10),
            state.orElse("1")
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
}
