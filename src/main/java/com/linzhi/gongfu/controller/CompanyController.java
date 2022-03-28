package com.linzhi.gongfu.controller;


import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.mapper.CompanyMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.CompanyService;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
                .map(companyMapper::toPreloadSupliers)
                .collect(Collectors.toList())
            )
            .build();
    }

    /**
     * 本单位的外供应商
     * @return 外供应商列表
     */
    @GetMapping("/suppliers/outer")
    public VOutsideSuppliersResponse  outsideSuppliers(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var list = companyService.findOutsideSuppliers(
            session.getSession().getCompanyCode()
        );

        return VOutsideSuppliersResponse.builder()
            .code(200)
            .message("获取供应商列表成功")
            .suppliers(
                list.stream()
                    .map(companyMapper::toOutsideSupplier)
                    .toList()
            )
            .build();
    }

    /**
     * 本单位的外供应商的详情
     * @return 外供应商列表
     */
    @GetMapping("/supplier/{code}")
    public VSupplierDetailResponse outsideSupplierDetail(@PathVariable String code){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
       var supplier = companyService.findOutsideSupplierDetail(code,session.getSession().getCompanyCode());
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
   public VBaseResponse saveOutsideSupplier(@RequestBody VOutsideSupplierRequest supplier){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var map = companyService.saveOutsideSupplier(supplier,session.getSession().getCompanyCode(),null);
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
    public VBaseResponse modifyOutsideSupplier(@PathVariable("code")String code,@RequestBody VOutsideSupplierRequest supplier){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext()
            .getAuthentication();
        var map = companyService.saveOutsideSupplier(supplier,session.getSession().getCompanyCode(),code);
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
    public VBaseResponse OutsideSupplierDisable(@RequestBody VOutsideSupplierRequest supplier){
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
    public VBaseResponse OutsideSupplierEnable(@RequestBody VOutsideSupplierRequest supplier){
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
}

