package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.mapper.BrandMapper;
import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.BrandService;
import com.linzhi.gongfu.service.CompanyService;
import com.linzhi.gongfu.vo.VBrandResponse;
import com.linzhi.gongfu.vo.VSuppliersIncludeBrandsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public VBrandResponse brandsPage(@RequestParam("pageNum") Optional<Integer> pageNum, @RequestParam("pageSize") Optional<Integer> pageSize) {

        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        var brandPage = brandService.brandsPagebyId(session.getSession().getCompanyCode(),pageNum,pageSize);
        return VBrandResponse.builder()
            .code(200)
            .message("获取品牌列表成功。")
            .total(brandPage.getPages())
            .current(brandPage.getPageNum())
            .brands(brandPage.getList().stream().map(brandMapper::toBrandPreload).collect(Collectors.toList()))
            .build();
    }
}
