package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于前端查询采购合同详情的响应体组建
 *
 * @author zgh
 * @create_at 2022-05-26
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VPurchaseContractDetailResponse extends VBaseResponse{
    private VContract contract;

    @Data
    public static class VContract{
        /**
         * 采购合同唯一id
         */
        private String id ;
        /**
         * 版本号
         */
        private int revision;
        /**
         * 版本号列表
         */
        private List<VRevision> revisions;
        /**
         * 采购合同系统编号
         */
        private String code;

        /**
         * 合同编码
         */
        private String contractNo;
        /**
         * 供应商合同编码
         */
        private String supplierNo;
        /**
         * 供应商公司编号
         */

        private String supplierCode;
        /**
         * 供应商名称
         */
        private String supplierName;
        /**
         * 税模式（0-未税 1-含税）
         */
        private String offerMode;


        /**
         * 税额
         */
        private BigDecimal tax;
        /**
         * 未税总价
         */
        private BigDecimal untaxedTotal;
        /**
         * 含税总价
         */
        private BigDecimal taxedTotal;
        /**
         * 供应商中联系人姓名
         */
        private String supplierContactName;
        /**
         * 供应商中联系人电话
         */
        private String supplierContactPhone;
        /**
         * 货物税率
         */
        private BigDecimal goodsVat;
        /**
         * 货物税率
         */
        private BigDecimal serviceVat;
        /**
         * 区域编码
         */
        private String areaCode;
        /**
         * 区域名称
         */
        private String areaName;
        /**
         * 详细地址
         */
        private String address;
        /**
         *收货人
         */
        private String consigneeName;
        /**
         *收货人电话
         */
        private String consigneePhone;

        /**
         * 状态（0-未确认 1-确认 2-撤销）
         */
        private String state;
        /**
         * 确认价税合计
         */
        private BigDecimal confirmTaxedTotal;
        /**
         * 所属操作员编码
         */
        private String ownerCode;
        /**
         * 所属操作员名称
         */
        private String ownerName;
        /**
         * 创建时间
         */
        private String createdAt;
        /**
         *折扣
         */
        private BigDecimal discount;
        /**
         * 最终未税总价
         */
        private BigDecimal discountedTotalPrice;
        /*
         * 对应销售合同记录系统主键
         */
        private String salesContractId;
        /*
         * 对应销售合同记录系统编码
         */
        private String salesContractCode;
        /*
         * 对应销售合同记录中本单位编码
         */
        private String salesContractNo;

        List<VProduct> products;

    }

    @Data
    public static class  VProduct{



        /**
         * 序号
         */
        private Integer itemNo ;
        private String createdAt;

        /**
         * 产品id
         */
        private String id;
        /**
         * 产品编码
         */
        private String code;
        /**
         * 客户自定义产品编码
         */
        private String customerPCode;
        /**
         * 本单位自定义产品编码
         */
        private String localPCode;
        /**
         * 描述
         */
        private String describe;
        /**
         * 品牌编码
         */
        private String brandCode;
        /**
         *品牌名称
         */
        private String brandName;
        /**
         * 数量
         */
        private BigDecimal amount;
        /**
         * 价格
         */
        private BigDecimal price;
        /**
         * 含税价格
         */
        private BigDecimal priceVat;
        /**
         * 未税总价
         */
        private BigDecimal totalPrice;
        /**
         * 含税总价
         */
        private BigDecimal totalPriceVat;

        /**
         * 上一版数量
         */
        private BigDecimal previousAmount;

        /**
         *上一版未税价格
         */
        private BigDecimal previousPrice;
        /**
         *上一版未税小计
         */
        private BigDecimal previousTotalPrice;
        /**
         *上一版含税价格
         */
        private BigDecimal previousPriceVat;
        /**
         *上一版含税小计
         */
        private BigDecimal previousTotalPriceVat;
        /**
         * 税率
         */
        private BigDecimal vatRate;
        /**
         * 上一版税率
         */
        private BigDecimal previousVatRate;
        /**
         * 备货期
         */
        private int stockTime;
        /**
         * 计价单位
         */
        private String chargeUnit;
        /**
         * 上一版计价单位
         */
        private String previousChargeUnit;
        /**
         * 类型（1-货物 2-服务）
         */
        private String type;

        /**
         * 面价
         */
        private BigDecimal facePrice;
    }

    @Data
    public static class VRevision{
       private String createdAt;
       private int revision;
    }
}
