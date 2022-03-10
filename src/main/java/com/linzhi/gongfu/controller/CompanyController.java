package com.linzhi.gongfu.controller;


import com.linzhi.gongfu.mapper.CompanyMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.CompanyService;
import com.linzhi.gongfu.vo.VSuppliersIncludeBrandsResponse;
import com.linzhi.gongfu.vo.VSuppliersResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
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
        var supplier = companyService.CompanyIncludeBrandbyId(session.getSession().getCompanyCode(),pageNum,pageSize);
        return VSuppliersIncludeBrandsResponse.builder()
               .code(200)
               .message("获取我的供应以及品牌列表成功。")
               .total(Integer.valueOf(String.valueOf(supplier.getTotalElements())))
               .current(supplier.getNumber()+1)
                .suppliers(supplier.getContent())
                .build();
    }

    /**
     * 查询本公司所有供应商
     * @param brands
     * @return 对应的本公司id查询所有供应商
     */
    @GetMapping("/suppliers/by/brand")
    public VSuppliersResponse suppliersByBrands(
        @RequestParam("brand") Optional<List<String>> brands
    ) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        var supplier = companyService.findSuppliersByBrands(
            brands,session.getSession().getCompanyCode()
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

}
