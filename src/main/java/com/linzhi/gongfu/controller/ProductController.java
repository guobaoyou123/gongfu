package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.mapper.MainProductClassMapper;
import com.linzhi.gongfu.mapper.ProductMapper;
import com.linzhi.gongfu.mapper.SysCompareDetailMapper;
import com.linzhi.gongfu.service.ProductService;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.*;
import com.linzhi.gongfu.vo.trade.VDriversResponse;
import com.linzhi.gongfu.vo.trade.VProductListResponse;
import com.linzhi.gongfu.vo.trade.VProductPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用于处理产品信息
 *
 * @author zgh
 * @create_at 2022-02-08
 */
@RequiredArgsConstructor
@RestController
public class ProductController {
    private final ProductService productService;
    private final MainProductClassMapper mainProductClassMapper;
    private final SysCompareDetailMapper sysCompareDetailMapper;
    private final ProductMapper productMapper;

    /**
     * 查询所有产品分类
     *
     * @return 对系统所有的产品分类信息
     */
    @GetMapping("/product/classes")
    public VProductClassResponse productClasses() {
        var classList = productService.listProductClasses("001")
            .stream()
            .map(mainProductClassMapper::toPreloadMainProductClass)
            .collect(Collectors.toList());

        return VProductClassResponse.builder()
            .code(200)
            .message("获取产品列表成功。")
            .classes(classList)
            .build();
    }

    /**
     * 查询所有产品驱动方式
     *
     * @return 驱动方式列表
     */
    @GetMapping("/product/drives")
    public VDriversResponse productDrives() {
        var drivers = productService.listProductDrives("驱动方式").stream()
            .map(sysCompareDetailMapper::toPreloadDriver)
            .collect(Collectors.toList());
        return VDriversResponse.builder()
            .code(200)
            .message("获取驱动方式列表成功。")
            .drives(drivers)
            .build();
    }

    /**
     * 查询所有产品主材质
     *
     * @return 主材质列表
     */
    @GetMapping("/product/materials")
    public VMaterialResponse productMaterials() {
        var materials = productService.listProductClasses("002").stream()
            .map(mainProductClassMapper::toPreloadMainMaterial)
            .collect(Collectors.toList());
        return VMaterialResponse.builder()
            .code(200)
            .message("获取主材质列表成功。")
            .materials(materials)
            .build();
    }

    /**
     * 查询所有产品连接方式
     *
     * @return 连接方式列表
     */
    @GetMapping("/product/connections")
    public VConnectionsResponse productConnections() {
        var connections = productService.listProductDrives("连接方式")
            .stream()
            .map(sysCompareDetailMapper::toPreloadConnection)
            .collect(Collectors.toList());
        return VConnectionsResponse.builder()
            .code(200)
            .message("获取连接方式列表成功。")
            .connections(connections)
            .build();
    }

    /**
     * 查询产品列表
     *
     * @return 返回产品列表
     */
    @GetMapping("/products")
    public VProductPageResponse products(
        @RequestParam("brand") Optional<List<String>> brands,
        @RequestParam("class") Optional<String> classes,
        @RequestParam("material") Optional<String> material,
        @RequestParam("drive") Optional<String> drive,
        @RequestParam("connection1") Optional<String> connection1,
        @RequestParam("connection2") Optional<String> connection2,
        @RequestParam("pageSize") Optional<String> pageSize,
        @RequestParam("pageNum") Optional<String> pageNum) {

        return productService.pageProducts(
            brands.orElse(new ArrayList<>()),
            classes.orElse(""),
            material.orElse(""),
            drive.orElse(""),
            connection1.orElse(""),
            connection2.orElse(""),
            PageRequest.of(
                pageNum.map(PageTools::verificationPageNum).orElse(0),
                pageSize.map(PageTools::verificationPageSize).orElse(10)
            )
        );
    }

    /**
     * 根据产品编码查询产品
     *
     * @return 返回产品列表
     */
    @GetMapping("/product/{productCode}")
    public VProductListResponse productsByCode(
        @PathVariable Optional<String> productCode) {
        var productList = productService.listProductsByCode(productCode.orElse(""));
        if (productList.size() == 0)
            return VProductListResponse.builder()
                .code(404)
                .message("未找到")
                .products(new ArrayList<>())
                .build();
        return VProductListResponse.builder()
            .code(200)
            .message("查询成功")
            .products(productList.stream().map(productMapper::tProductList).collect(Collectors.toList()))
            .build();
    }

    /**
     * 根据产品id查找产品详情
     *
     * @param productId 产品id
     * @return 返回产品详情
     */
    @GetMapping("/product/detail")
    public VProductDetailResponse productDetail(@RequestParam("productId") Optional<String> productId) {
        var productDetail = productId
            .flatMap(productService::getProduct)
            .map(productMapper::tProductDetail);
        if (productDetail.isEmpty())
            return VProductDetailResponse.builder()
                .code(404)
                .message("请求的产品信息没有找到")
                .product(new VProductDetailResponse.VProduct())
                .build();
        return VProductDetailResponse.builder()
            .code(200)
            .message("成功找到请求的产品信息")
            .product(productDetail.get())
            .build();
    }

}
