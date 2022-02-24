package com.linzhi.gongfu.controller;

import com.linzhi.gongfu.mapper.MainProductClassMapper;
import com.linzhi.gongfu.mapper.SysCompareDetailMapper;
import com.linzhi.gongfu.service.ProductService;
import com.linzhi.gongfu.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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

    /**
     * 查询所有产品分类
     * @return 对系统所有的产品分类信息
     */
    @GetMapping("/product/classes")
    public VProductClassResponse productClasses() {
        var classList =   productService.productClassList("001")
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
     * @return 驱动方式列表
     */
    @GetMapping("/product/drives")
    public VDriversResponse productDrives() {
        var drivers=productService.productDrivesList("驱动方式").stream()
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
     * @return 主材质列表
     */
    @GetMapping("/product/materials")
    public VMaterialResponse productMaterials() {
        var materials =   productService.productClassList("002").stream()
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
     * @return 连接方式列表
     */
    @GetMapping("/product/connections")
    public VConnectionsResponse productConnections() {
        var connections=productService.productDrivesList("连接方式")
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
     * @return 返回产品列表
     */
    @GetMapping("/products")
    public VProductResponse products(
        @RequestParam("brand")Optional<List<String>> brands,
        @RequestParam("class")Optional<String> classes,
        @RequestParam("material")Optional<String> material,
        @RequestParam("drive")Optional<String> drive,
        @RequestParam("connection1")Optional<String> connection1,
        @RequestParam("connection2")Optional<String> connection2,
        @RequestParam("pageSize")Optional<Integer> pageSize,
        @RequestParam("pageNum")Optional<Integer> pageNum){

        return productService.productList(brands,
            classes,
            material,
            drive,
            connection1,
            connection2,
            pageSize,
            pageNum
        );
    }
}
