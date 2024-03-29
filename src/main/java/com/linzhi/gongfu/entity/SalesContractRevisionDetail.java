package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.TaxMode;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 合同详情
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sales_contract_rev")
public class SalesContractRevisionDetail implements Serializable {
    /**
     * 合同唯一id
     */
    @EmbeddedId
    private SalesContractRevisionId salesContractRevisionId;

    /**
     * 合同编码
     */
    @Column(name = "order_code", length = 40)
    private String orderCode;

    /**
     * 客户合同编码
     */
    @Column(name = "buyer_order_code", length = 40)
    private String buyerOrderCode;

    /*
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
     * 客户中联系人姓名
     */
    @Column(name = "saler_contact_name", length = 20)
    private String salerContactName;

    /**
     * 客户中联系人电话
     */
    @Column(name = "saler_contact_phone", length = 20)
    private String salerContactPhone;

    /**
     * 货物税率
     */
    @Column(name = "goods_rate")
    private BigDecimal vatProductRate;

    /**
     * 服务税率
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
     * 最终未税总价
     */
    @Column(name = "discount_total_price")
    private BigDecimal discountedTotalPrice;

    /**
     * 确认价税合计
     */
    @Column(name = "confirm_total_price_vat")
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
    @Column(name = "fingerprint")
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
     * 收货地址编码
     */
    @Column(name = "delivery_code", length = 20)
    private String deliveryCode;

    /**
     * 交货联系人编码
     */
    @Column(name = "contact_code", length = 20)
    private String contactCode;

    /**
     * 合同明细
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "contract_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "revision", referencedColumnName = "revision", insertable = false, updatable = false)
    })
    private List<SalesContractRecord> contractRecords;

    /**
     * 合同临时明细
     */
    @OneToMany
    @JoinColumns({
        @JoinColumn(name = "contract_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "revision", referencedColumnName = "revision", insertable = false, updatable = false)
    })
    private List<SalesContractRecordTemp> contractRecordTemps;

    /**
     * 合同基础信息
     */
    @OneToOne
    @JoinColumns({
        @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    })
    private SalesContractBase salesContractBase;

    /**
     * 合同版本
     */
    @OneToMany
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<SalesContractRevision> salesContractRevisions;
}
