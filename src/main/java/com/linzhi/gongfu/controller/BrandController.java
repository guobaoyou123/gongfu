package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.mapper.BrandMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.BrandService;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用于处理品牌信息
 *
 * @author zgh
 * @create_at 2022-01-28
 */
@RequiredArgsConstructor
@RestController
public class BrandController {

    private final BrandService brandService;
    private final BrandMapper brandMapper;


    /**
     * 通过本公司id查询所有供应商以及经营，自营的品牌
     *
     * @return 对应的本公司id查询所有供应商以及经营，自营的品牌信息
     */
    @GetMapping("/brands/paged")
    public VBrandPageResponse pageBrands(
        @RequestParam("pageNum") Optional<String> pageNum,
        @RequestParam("pageSize") Optional<String> pageSize
    ) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var brandPage = brandService.pageBrands(
            session.getSession().getCompanyCode(),
            pageNum,
            pageSize
        );
        return VBrandPageResponse.builder()
            .code(200)
            .message("获取品牌列表成功。")
            .total(Integer.parseInt(String.valueOf(brandPage.getTotalElements())))
            .current(brandPage.getNumber() + 1)
            .brands(brandPage.getContent().stream()
                .map(brandMapper::toBrandPreload)
                .collect(Collectors.toList())
            )
            .build();
    }

    /**
     * 查询所有品牌
     *
     * @return 对系统所有的品牌信息
     */
    @GetMapping("/brands")
    public VDcBrandResponse listBrands() {
        var brandList = brandService.listBrands();
        return VDcBrandResponse.builder()
            .code(200)
            .message("获取品牌列表成功。")
            .brands(brandList.stream()
                .map(brandMapper::toProductBrandPreload)
                .collect(Collectors.toSet())
            )
            .build();
    }

    /**
     * 根据分类查询所有品牌
     *
     * @return 对系统所有的品牌信息
     */
    @GetMapping("/brands/by/class")
    public VDcBrandResponse listBrandsByClass(@RequestParam("class") Optional<String> classes) {
        var brandList = brandService.listBrandsByClass(classes);
        return VDcBrandResponse.builder()
            .code(200)
            .message("获取品牌列表成功。")
            .brands(brandList.stream()
                .map(brandMapper::toProductBrandPreload)
                .collect(Collectors.toSet())
            )
            .build();
    }

    /**
     * 根据供应商查询所有品牌
     *
     * @return 对系统所有的品牌信息
     */
    @GetMapping("/brands/by/company")
    public VDcBrandResponse listBrandsBySuppliers(@RequestParam("company") Optional<List<String>> company) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var brandList = brandService.listBrandsBySuppliers(
            company.orElse(new ArrayList<>()),
            session.getSession().getCompanyCode()
        );
        return VDcBrandResponse.builder()
            .code(200)
            .message("获取品牌列表成功。")
            .brands(
                brandList.stream()
                    .map(brandMapper::toProductBrandPreload)
                    .collect(Collectors.toSet())
            )
            .build();
    }

    /**
     * 设置经营品牌
     * @param brandCodes 品牌编码列表
     * @return 返回成功信息
     */
    @PostMapping("/brands/management")
    public VBaseResponse savaManagementBrands(@RequestBody Optional<VBrandsRequest> brandCodes){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        brandService.saveManagementBrands(brandCodes.orElseThrow(()->new NullPointerException("参数为空")).getBrands(),session.getSession().getCompanyCode());
        return VBaseResponse.builder()
            .code(200)
            .message("设置品牌成功")
            .build();
    }

    /**
     * 获取品牌优选供应商列表
     * @return 返回品牌优选供应商列表
     */
    @GetMapping("/brand/preference/supplier")
    public VPreferenceSupplierResponse preferenceSuppliers(){
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var list = brandService.listBrandsByCompanyCode(session.getSession().getCompanyCode())
            .stream()
            .map(brandMapper::toPreferenceSupplier)
            .toList();
        return  VPreferenceSupplierResponse.builder()
            .code(200)
            .message("获取数据成功")
            .brands(list)
            .build();
    }

}
