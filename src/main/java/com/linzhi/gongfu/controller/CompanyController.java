package com.linzhi.gongfu.controller;



import com.linzhi.gongfu.security.token.OperatorSessionToken;
import com.linzhi.gongfu.service.CompanyService;
import com.linzhi.gongfu.vo.VSuppliersIncludeBrandsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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

    /**
     * 通过本公司id查询所有供应商以及经营，自营的品牌
     * @return 对应的本公司id查询所有供应商以及经营，自营的品牌信息
     */
    @GetMapping("/suppliers/paged")
    public VSuppliersIncludeBrandsResponse suppliersIncludeBrands(@RequestParam("pageNum") Optional<Integer> pageNum,@RequestParam("pageSize") Optional<Integer> pageSize) {
        AtomicInteger i = new AtomicInteger();
        OperatorSessionToken session = (OperatorSessionToken) SecurityContextHolder.getContext().getAuthentication();
        var supplier = companyService.CompanyIncludeBrandbyId(session.getSession().getCompanyCode(),pageNum,pageSize);
        supplier.getContent().forEach(vSupplier -> {
            i.getAndIncrement();
            vSupplier.setSort(i.get());
        });
        return VSuppliersIncludeBrandsResponse.builder()
               .code(200)
               .message("获取我的供应以及品牌列表成功。")
               .total(supplier.getTotalPages())
               .current(supplier.getNumber())
                .suppliers(supplier.getContent())
                .build();

    }
}
