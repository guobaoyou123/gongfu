package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VInquiryDetailResponse extends VBaseResponse{
    private VInquiry inquiry;

    @Data
    public static class VInquiry{
        /**
         * 询价单唯一id
         */
        private String id ;
        /**
         * 询价单编号
         */
        private String code;
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
        /*
         * 对应销售合同记录中客户合同编码
         */
        private String  salesCustomerNo;
        /**
         * 合同主键
         */
        private String purchaseContractId;
        /*
         * 合同系统编号
         */
        private String purchaseContractCode;
        /**
         * 合同编码
         */
        private String purchaseContractNo;
        /**
         * 供应商合同编码
         */
        private String purchaseSupplierNo;
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
         * 供应商公司编号
         */

        private String supplierCode;
        /**
         * 供应商名称
         */
        private String supplierName;
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
         * 确认价税合计
         */
        private BigDecimal confirmTaxedTotal;
        /**
         * 状态（0-未形成合同 1-以生成合同 2-撤销合同）
         */
        private String state;
        /**
         * 税模式（0-未税 1-含税）
         */
        private String offerMode;

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
         *折扣
         */
        private BigDecimal discount;
        /**
         * 最终未税总价
         */
        private BigDecimal discountedTotalPrice;

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
         * 描述
         */
        private String describe;
        /**
         * 计价单位
         */
        private String chargeUnit;
        /**
         * 类型（1-货物 2-服务）
         */
        private String type;
        /**
         * 品牌编码
         */
        private String brandCode;
        /**
         *品牌名称
         */
        private String brandName;
        /**
         * 面价
         */
        private BigDecimal facePrice;
        /**
         * 税率
         */
        private BigDecimal vatRate;
        /**
         * 价格
         */
        private BigDecimal price;
        /**
         * 含税价格
         */
        private BigDecimal priceVat;
        /**
         * 数量
         */
        private BigDecimal amount;
        /**
         * 未税总价
         */
        private BigDecimal totalPrice;
        /**
         * 含税总价
         */
        private BigDecimal totalPriceVat;

        /**
         *折扣
         */
        private BigDecimal discount;

        /**
         *折扣后未税价格
         */
        private BigDecimal afterDiscountPrice;
        /**
         *折扣后未税小计
         */
        private BigDecimal totalAfterDiscountPrice;
        /**
         *折扣后含税价格
         */
        private BigDecimal afterDiscountPriceVat;
        /**
         *折扣后含税小计
         */
        private BigDecimal totalAfterDiscountPriceVat;
        /**
         * 备货期
         */
        private int stockTime;

    }
}
