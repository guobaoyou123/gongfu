package com.linzhi.gongfu.vo.warehousing;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于响应前端对于库房列表的请求
 *
 * @author zhangguanghua
 * @create_at 2022-12-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VSafetyStockListResponse extends VBaseResponse {

    private List<VSafetyStock> products;

    @Data
    public static class VSafetyStock {
        /**
         * 产品主键
         */
        private String id;
        /**
         * 产品编码
         */
        private String code;

        /**
         * 产品描述
         */
        private String describe;


        /**
         * 品牌编码
         */
        private String brandCode;

        /**
         * 品牌名称
         */
        private String brandName;

        /**
         * 计价单位
         */
        private String chargeUnit;

        /**
         * 可销库存
         */
        private BigDecimal deliverNum;

        /**
         * 在途库存
         */
        private BigDecimal  tranNum;

        /**
         * 安全库存
         */
        private BigDecimal  safetyStock;

        /**
         * 实际库存
         */
        private BigDecimal  physicalStock;

        /**
         * 单次采购量
         */
        private BigDecimal  singlePurchaseQuantity;

        /**
         * 授权操作员列表
         */
        private List<VWareHouse> warehousese;
    }

    /**
     * 一个仓库的库存实体
     */
    @Data
    public static class VWareHouse {
        /**
         * 仓库编码
         */
        private String code;

        /**
         * 仓库名称
         */
        private String name;

        /**
         * 可销库存
         */
        private BigDecimal deliverNum;

        /**
         * 实际库存
         */
        private BigDecimal  physicalStock;

    }
}
