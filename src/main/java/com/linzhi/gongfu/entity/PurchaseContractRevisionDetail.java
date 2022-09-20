package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.TaxMode;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 合同版本详情表
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "purchase_contract_rev")
public class PurchaseContractRevisionDetail {
    /**
     * 合同唯一id
     */
    @EmbeddedId
    private PurchaseContractRevisionId purchaseContractRevisionId;

    @Column(insertable = false, updatable = false)
    private String code;
    /**
     * 合同编码
     */
    @Column(name = "order_code", length = 40)
    private String orderCode;
    /*
     * 对应销售合同记录系统主键
     */
    @Column(insertable = false, updatable = false)
    private String salesContractId;
    /*
     * 对应销售合同记录系统编码
     */
    @Column(insertable = false, updatable = false)
    private String salesContractCode;

    /*
     * 对应销售合同记录中本单位编码
     */
    @Column(insertable = false, updatable = false)
    private String salesOrderCode;

    /**
     * 供应商合同编码
     */
    @Column(name = "saler_order_code", length = 40)
    private String salerOrderCode;

    /**
     * 买方联系人姓名
     */
    @Column(name = "buyer_contact_name", length = 20)
    private String buyerContactName;

    /**
     * 买方联系人电话
     */
    @Column(name = "buyer_contact_phone", length = 20)
    private String buyerContactPhone;

    /**
     * 供应商中联系人姓名
     */
    @Column(name = "saler_contact_name", length = 20)
    private String salerContactName;

    /**
     * 供应商中联系人电话
     */
    @Column(name = "saler_contact_phone", length = 20)
    private String salerContactPhone;

    /**
     * 货物税率
     */
    @Column(name = "goods_rate")
    private BigDecimal vatProductRate;

    /**
     * 货物税率
     */
    @Column(name = "service_rate")
    private BigDecimal vatServiceRate;

    /**
     * 折扣
     */
    @Column(name = "discount")
    private BigDecimal discount;

    /**
     * 税额
     */
    @Column(name = "vat")
    private BigDecimal vat;

    /**
     * 未税总价
     */
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    /**
     * 含税总价
     */
    @Column(name = "total_price_vat")
    private BigDecimal totalPriceVat;

    /**
     * 上一版未税总价
     */
    @Column(name = "previousUntaxedTotal")
    private BigDecimal previousUntaxedTotal;

    /**
     * 上一版本含税总价
     */
    @Column(name = "previousTaxedTotal")
    private BigDecimal previousTaxedTotal;

    /**
     * 最终未税总价
     */
    @Column(name = "discount_total_price", insertable = false, updatable = false)
    private BigDecimal discountedTotalPrice;

    /**
     * 确认价税合计
     */
    @Column(name = "confirm_total_price_vat", insertable = false, updatable = false)
    private BigDecimal confirmTotalPriceVat;

    /**
     * 税模式（0-未税 1-含税）
     */
    @Column(name = "offer_mode", length = 1)
    private TaxMode offerMode;

    /**
     * 区域编码
     */
    @Column(name = "area_code", length = 20)
    private String areaCode;

    /**
     * 区域名称
     */
    @Column(name = "area_name", length = 100)
    private String areaName;

    /**
     * 详细地址
     */
    @Column(name = "address", length = 100)
    private String address;

    /**
     * 收货人
     */
    @Column(name = "consignee_name", length = 20)
    private String consigneeName;

    /**
     * 收货人电话
     */
    @Column(name = "consignee_phone", length = 20)
    private String consigneePhone;

    /**
     * 序列码（由 供应商编码+客户编码+产品+数量 组成）
     */
    @Column(name = "fingerprint", length = 255)
    private String fingerprint;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 删除时间
     */
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    /**
     * 删除人
     */
    @Column(name = "revoked_by")
    private String revokedBy;

    /**
     * 修改时间
     */
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    /**
     * 修改人
     */
    @Column(name = "modified_by")
    private String modifiedBy;

    /**
     * 确认时间
     */
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    /**
     * 确认人
     */
    @Column(name = "confirmed_by")
    private String confirmedBy;

    /**
     * 所属单位编码
     */
    @Column(name = "created_by_comp", insertable = false, updatable = false)
    private String createdByComp;

    /**
     * 所属操作员编码
     */
    @Column(name = "created_by", insertable = false, updatable = false)
    private String createdBy;

    @Column(name = "createdByName", updatable = false, insertable = false)
    private String createdByName;

    /**
     * 客户公司编码
     */
    @Column(name = "buyer_comp", insertable = false, updatable = false)
    private String buyerComp;

    /**
     * 客户名称
     */
    @Column(name = "buyer_comp_name", insertable = false, updatable = false)
    private String buyerCompName;

    /**
     * 供应商公司编号
     */
    @Column(name = "saler_comp", insertable = false, updatable = false)
    private String salerComp;

    /**
     * 供应商名称
     */
    @Column(name = "saler_comp_name", insertable = false, updatable = false)
    private String salerCompName;

    /**
     * 状态（0-未确认 1-确认 2-撤销）
     */
    @Column(insertable = false, updatable = false)
    private String state;

    /**
     * 收货地址编码
     */
    @Column(name = "delivery_code", length = 20)
    private String deliveryCode;

    /**
     * 交货联系人编码
     */
    @Column(name = "contact_code", length = 20)
    private String contactCode;
}
