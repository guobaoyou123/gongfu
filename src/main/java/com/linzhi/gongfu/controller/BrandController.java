package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.mapper.BrandMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.BrandService;
import com.linzhi.gongfu.vo.VBrandPageResponse;
import com.linzhi.gongfu.vo.VDcBrandResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * @return 对应的本公司id查询所有供应商以及经营，自营的品牌信息
     */
    @GetMapping("/brands/paged")
    public VBrandPageResponse brandsPage(
        @RequestParam("pageNum") Optional<String> pageNum,
        @RequestParam("pageSize") Optional<String> pageSize
    ) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var brandPage = brandService.brandsPagebyId(
           session.getSession().getCompanyCode(),
           pageNum,
           pageSize
        );
        return VBrandPageResponse.builder()
            .code(200)
            .message("获取品牌列表成功。")
            .total(Integer.valueOf(String.valueOf(brandPage.getTotalElements())))
            .current(brandPage.getNumber()+1)
            .brands(brandPage.getContent().stream()
                .map(brandMapper::toBrandPreload)
                .collect(Collectors.toList())
            )
            .build();
    }

    /**
     * 查询所有品牌
     * @return 对系统所有的品牌信息
     */
    @GetMapping("/brands")
    public VDcBrandResponse brandsList() {
        var brandList = brandService.brandList();
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
     * 查询所有品牌
     * @return 对系统所有的品牌信息
     */
    @GetMapping("/brands/by/class")
    public VDcBrandResponse brandsByClass(@RequestParam("class") Optional<String> classes) {
        var brandList = brandService.brandListByClass(classes);
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
     * @return 对系统所有的品牌信息
     */
    @GetMapping("/brands/by/company")
    public VDcBrandResponse brandsByCompany(@RequestParam("company") Optional<List<String>> company) {
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder
            .getContext().getAuthentication();
        var brandList = brandService.brandListBySupliers(
            company,
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
}
