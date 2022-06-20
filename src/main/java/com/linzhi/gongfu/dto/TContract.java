package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用于转移合同信息
 *
 * @author zgh
 * @create_at 2022-05-24
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TContract {

    /**
     * 合同唯一id
     */
    private String id ;
    /**
     * 版本号
     */
    private int revision;
    /**
     * 版本号列表
     */
    private List<TRevision> revisions;
    /**
     * 合同编号
     */

    private String code;
    /**
     * 合同编码
     */

    private String orderCode;
    /**
     * 供应商合同编码
     */
    private String  supplierContractNo;
    /**
     * 类型（0-采购合同 1-销售合同）
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
     * 供应商公司编号
     */

    private String salerComp;
    /**
     * 供应商名称
     */
    private String salerCompName;
    /**
     * 状态（0-未确认 1-确认 2-撤销）
     */
    @Column
    private String state;

    /**
     * 创建时间
     */
    private String createdAt;
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
     * 上一版未税总价
     */
    private BigDecimal previousUntaxedTotal;
    /**
     * 上一版本含税总价
     */
    private BigDecimal previousTaxedTotal;
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
     * 确认价税合计
     */
    private BigDecimal confirmTaxedTotal;
    /**
     *折扣
     */
    private BigDecimal discount;
    /**
     * 最终未税总价
     */
    private BigDecimal discountedTotalPrice;

    /**
     * 合同明细
     */
    private List<TContractRecord> records;

   private int category;

    private String deliveryCode;
    /**
     * 交货联系人编码
     */
    private String contactCode;
}
