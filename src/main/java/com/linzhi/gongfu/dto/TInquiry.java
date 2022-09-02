package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于转移询价单信息
 *
 * @author zgh
 * @create_at 2022-04-06
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TInquiry {
    /**
     * 询价单唯一id
     */
    private String id;

    /**
     * 询价单编号
     */
    private String code;

    /**
     * 合同编码
     */
    private String contractId;

    /*
     * 合同系统编号
     */
    private String contractCode;

    /**
     * 合同编码
     */
    private String orderCode;

    /**
     * 供应商合同编码
     */
    private String salerOrderCode;

    /**
     * 类型（0-询价单 1-报价当）
     */
    private String type;

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
    private String salesOrderCode;

    /*
     * 对应销售合同记录中客户合同编码
     */
    private String salesBuyerOrderCode;

    /**
     * 所属单位编码
     */
    private String createdByComp;

    /**
     * 所属操作员编码
     */
    private String createdBy;

    /**
     * 所属操作员姓名
     */
    private String createdByName;

    /**
     * 客户公司编码
     */
    private String buyerComp;

    /**
     * 客户名称
     */
    private String buyerCompName;

    /**
     * 买方联系人电话
     */
    private String buyerContactPhone;

    /**
     * 供应商公司编号
     */
    private String salerComp;

    /**
     * 供应商名称
     */
    private String salerCompName;

    /**
     * 供应商中操作员编号
     */
    private String salerContactName;

    /**
     * 供应商中联系人电话
     */
    private String salerContactPhone;

    /**
     * 产品税率
     */
    private BigDecimal vatProductRate;

    /**
     * 货物税率
     */
    private BigDecimal vatServiceRate;

    /**
     * 折扣
     */
    private BigDecimal discount;

    /**
     * 最终未税总价
     */
    private BigDecimal discountedTotalPrice;

    /**
     * 税额
     */
    private BigDecimal vat;

    /**
     * 未税总价
     */
    private BigDecimal totalPrice;

    /**
     * 含税总价
     */
    private BigDecimal totalPriceVat;

    /**
     * 确认价税合计
     */
    private BigDecimal confirmTotalPriceVat;

    /**
     * 状态（0-未形成合同 1-以生成合同 2-撤销）
     */
    private String state;

    /**
     * 税模式（0-未税 1-含税）
     */
    private String offerMode;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 删除时间
     */
    private String deletedAt;

    /**
     * 确认时间
     */
    private String confirmedAt;

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
     * 收货人
     */
    private String consigneeName;

    /**
     * 收货人电话
     */
    private String consigneePhone;

    /**
     * 种类
     */
    private String counts;

    /**
     * 询价单明细记录
     */
    private List<TInquiryRecord> records;
}
