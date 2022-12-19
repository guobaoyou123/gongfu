package com.linzhi.gongfu.service;

import com.linzhi.gongfu.dto.TCompareDetail;
import com.linzhi.gongfu.dto.TProduct;
import com.linzhi.gongfu.dto.TProductClass;
import com.linzhi.gongfu.dto.TProductStockSum;
import com.linzhi.gongfu.entity.*;
import com.linzhi.gongfu.mapper.MainProductClassMapper;
import com.linzhi.gongfu.mapper.ProductMapper;
import com.linzhi.gongfu.mapper.SysCompareDetailMapper;
import com.linzhi.gongfu.mapper.storage.ProductStockMapper;
import com.linzhi.gongfu.repository.MainProductClassRepository;
import com.linzhi.gongfu.repository.ProductRepository;
import com.linzhi.gongfu.repository.SysCompareTableRepository;
import com.linzhi.gongfu.util.PageTools;
import com.linzhi.gongfu.vo.trade.VProductPageResponse;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 产品信息处理及业务服务
 *
 * @author zgh
 * @create_at 2022-02-08
 */
@RequiredArgsConstructor
@Service
public class ProductService {

    private final MainProductClassRepository mainProductClassRepository;
    private final MainProductClassMapper mainProductClassMapper;
    private final SysCompareTableRepository sysCompareTableRepository;
    private final SysCompareDetailMapper sysCompareDetailMapper;
    private final ProductMapper productMapper;
    private final JPAQueryFactory queryFactory;
    private final ProductRepository productRepository;
    private final ProductStockMapper productStockMapper;

    /**
     * 获取产品一级二级分类信息
     *
     * @param type 编号
     * @return 产品一级二级分类信息列表
     */
    @Cacheable(value = "product_class;1800", unless = "#result == null")
    public List<TProductClass> listProductClasses(String type) {
        return mainProductClassRepository.findMainProductClassByBaseProductClassId_Type(type).stream()
            .map(mainProductClassMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * 获取产品对照表信息
     *
     * @param name 名称
     * @return 产品对照表信息列表
     */
    @Cacheable(value = "product_compare;1800", unless = "#result == null")
    public List<TCompareDetail> listProductDrives(String name) {
        return sysCompareTableRepository.findSysCompareTableByName(name).getList().stream()
            .map(sysCompareDetailMapper::toCompareDetail)
            .collect(Collectors.toList());
    }

    /**
     * 根据查询条件获取产品列表信息
     *
     * @param brands      品牌编码列表
     * @param classes     二级分类编码
     * @param material    主材质编码
     * @param drive       驱动方式名称
     * @param connection1 连接方式名称
     * @param connection2 连接方式名称
     * @param pageable    分页
     * @return 产品列表信息
     */
    public VProductPageResponse pageProducts(List<String> brands, String classes, String material

        , String drive, String connection1, String connection2, Pageable pageable) {

        //根据条件查询产品信息
        List<TProduct> products = listProductsAll(brands, classes, material, drive, connection1, connection2).stream()
            .map(productMapper::toProduct)
            .collect(Collectors.toList());
        if (products.size() == 0) {
            products = listProductsAll(new ArrayList<>(), classes, material, drive, connection1, connection2).stream()
                .map(productMapper::toProduct)
                .collect(Collectors.toList());
            Page<TProduct> otherProductPage = PageTools.listConvertToPage(products, pageable);
            return VProductPageResponse.builder()
                .code(200)
                .message("获取产品列表成功。")
                .total(otherProductPage.getTotalPages())
                .current(otherProductPage.getNumber() + 1)
                .products(new ArrayList<>())
                .otherproducts(otherProductPage.getContent().stream().map(productMapper::toPreloadProduct).collect(Collectors.toList()))
                .build();
        }
        //进行分页
        Page<TProduct> productPage = PageTools.listConvertToPage(products, pageable);
        return VProductPageResponse.builder()
            .code(200)
            .message("获取产品列表成功。")
            .total(Integer.parseInt(String.valueOf(productPage.getTotalElements())))
            .current(productPage.getNumber() + 1)
            .otherproducts(new ArrayList<>())
            .products(productPage.getContent().stream().map(productMapper::toPreloadProduct).collect(Collectors.toList()))
            .build();
    }

    /**
     * 获取产品信息列表
     *
     * @param brands      品牌编码列表
     * @param classes     二级分裂编码
     * @param material    主材质编码
     * @param drive       驱动名称
     * @param connection1 连接方式名称
     * @param connection2 连接方式名称
     * @return 产品列表
     */
    @Cacheable(value = "products;1800", unless = "#result == null")
    public List<Product> listProductsAll(List<String> brands, String classes, String material
        , String drive, String connection1, String connection2) {
        //根据条件查询产品信息
        QProduct qProduct = QProduct.product;
        JPAQuery<Product> query = queryFactory.select(qProduct).from(qProduct);
        if (brands.size() > 0)
            query.where(qProduct.brandCode.in(brands));
        if (!classes.isEmpty())
            query.where(qProduct.class2.eq(classes));
        if (!drive.isEmpty())
            query.where(qProduct.drivMode.eq(drive));
        if (!material.isEmpty())
            query.where(qProduct.mainMate.eq(material));
        if (!connection1.isEmpty() && !connection2.isEmpty())
            query.where((qProduct.conn1Type.eq(connection1).and(qProduct.conn2Type.eq(connection2))).or((qProduct.conn1Type.eq(connection2).and(qProduct.conn2Type.eq(connection1)))));
        if (!connection1.isEmpty() && connection2.isEmpty())
            query.where(qProduct.conn1Type.eq(connection1).or(qProduct.conn2Type.eq(connection1)));
        if (connection1.isEmpty() && !connection2.isEmpty())
            query.where(qProduct.conn1Type.eq(connection2).or(qProduct.conn2Type.eq(connection2)));
        return query.fetch();
    }

    /**
     * 获取产品信息
     *
     * @param productId 产品id
     * @return 产品详细信息
     */
    @Cacheable(value = "productDetail;1800", unless = "#result == null")
    public Optional<TProduct> getProduct(String productId) {
        return productRepository.findById(productId)
            .map(productMapper::toProduct)
            ;
    }

    /**
     * 根据产品编码获取产品列表
     *
     * @param productCode 产品编码
     * @return 产品列表信息
     */
    @Cacheable(value = "productsByCode;1800", key = "T(String).valueOf(#productCode)", unless = "#result == null")
    public List<TProduct> listProductsByCode(String productCode) {
        return productRepository.findProductByCode(productCode).stream()
            .map(productMapper::toProduct).collect(Collectors.toList());
    }

    /**
     * 安全库存列表
     * @param companyCode 公司编码
     * @param name 编码/描述
     * @return 安全库存列表
     */
    @Cacheable(value = "Product_Safety_Stock_List;1800",key="#companyCode+'_'+#brandCode+'_'+#name", unless = "#result == null")
    public List<TProductStockSum> listProductSafetyStock(String companyCode, String name,String brandCode){
       QProductStockSum qProductStockSum = QProductStockSum.productStockSum;

       JPAQuery<ProductStockSum> query = queryFactory.select(qProductStockSum).from(qProductStockSum);
       query.where(qProductStockSum.productStockSumId.compId.eq(companyCode));
       if(!name.equals("")){
           query.where(qProductStockSum.product.code.like(name).or(qProductStockSum.product.describe.like(name)));
       }
        if(!brandCode.equals("")){
            query.where(qProductStockSum.product.brandCode.eq(brandCode));
        }
        query.orderBy(qProductStockSum.safetyStock.safetyStock.desc());

        List<ProductStockSum> productStockSums = query
            .offset(0)
            .limit(20)
            .fetch();
        return productStockSums.stream().map(productStockMapper::toTProductStockSum)
             .toList();
    }
}
