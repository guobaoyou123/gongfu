package com.linzhi.gongfu.vo;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 用于响应前端对于购计划的加载请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VPurchasePlanResponse extends VBaseResponse{

        private String planCode;
        /**
         * 对应销售合同号
         */
        private String salesCode;
        private List<VProduct> products;

    @Data
    public  static  class  VProduct{
        private String id;
        /**
         * 产品代码
         */
        private String code;
        /**
         * 品牌编码
         */
        private String brandCode;
        /**
         * 可销库存
         */
        private BigDecimal deliverNum;
        /**
         * 在途库存
         */
        private BigDecimal tranNum;
        /**
         * 需求总数量
         */
        private BigDecimal demand;
        /**
         * 安全库存
         */
        private BigDecimal safetyStock;
        /**
         * 上次采购价格
         */
        private BigDecimal beforeSalesPrice;
        /**
         * 正在询价数量
         */
        private BigDecimal inquiryNum;
        /**
         * 品牌名称
         */
        private String brandName;
        /**
         * 产品描述
         */
        private String describe;
        /**
         * 计价单位
         */
        private String chargeUnit;
        /**
         * 面价
         */
        private BigDecimal facePrice;
        /**
         * 创建时间
         */
        private long createdAt;

        private List<VSupplier> suppliers;

    }

    @Data
    public  static  class VSupplier{
        /**
         * 排序
         */
        private int serial;
        /**
         * 供应商公司编码
         */
        private String code;
        /**
         * 供应商公司名称
         */
        private String name;
        /**
         * 可销库存
         */
        private BigDecimal deliverNum;
        /**
         * 在途库存
         */
        private BigDecimal tranNum;
        /**
         * 需求数量
         */
        private BigDecimal demand;
    }
}
