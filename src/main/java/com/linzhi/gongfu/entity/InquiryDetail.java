package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.InquiryState;
import com.linzhi.gongfu.enumeration.InquiryType;
import com.linzhi.gongfu.enumeration.TaxMode;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inquiry_base")

public class InquiryDetail {
    /**
     * 询价单唯一id
     */
    @Id
    @Column(length = 50, nullable = false)
    @NotNull
    @NotBlank
    @NonNull
    private String id;
    /**
     * 询价单编号
     */
    @Column(length = 40, nullable = false)
    @NotNull
    @NotBlank
    private String code;
    /**
     * 合同id
     */
    @Column(name = "contract_id", length = 50)
    private String contractId;
    /*
     * 合同系统编号
     */
    @Column(name = "contract_code", length = 40)
    private String contractCode;

    /**
     * 类型（0-询价单 1-报价当）
     */
    @Column(name = "type", length = 1)
    private InquiryType type;
    /*
     * 对应销售合同记录系统主键
     */
    @Column(name = "sales_contract_id", length = 50)
    private String salesContractId;

    /**
     * 所属单位编码
     */
    @Column(name = "created_by_comp", length = 20)
    private String createdByComp;
    /**
     * 所属操作员编码
     */
    @Column(name = "created_by", length = 20)
    private String createdBy;
    /**
     * 客户公司编码
     */
    @Column(name = "buyer_comp", length = 40, nullable = false)
    private String buyerComp;
    /**
     * 客户名称
     */
    @Column(name = "buyer_comp_name", length = 100)
    private String buyerCompName;

    /**
     * 供应商公司编号
     */
    @Column(name = "saler_comp", length = 40, nullable = false)

    private String salerComp;
    /**
     * 供应商名称
     */
    @Column(name = "saler_comp_name", length = 100)
    private String salerCompName;


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
     * 状态（0-未形成合同 1-以生成合同 2-撤销合同）
     */
    @Column
    private InquiryState state;
    /**
     * 税模式（0-未税 1-含税）
     */
    @Column(name = "offer_mode", length = 1)
    private TaxMode offerMode;
    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    /**
     * 删除时间
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    /**
     * 确认时间
     */
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "inquiry_id", referencedColumnName = "id", insertable = true, updatable = true)
    @NotFound(action = NotFoundAction.IGNORE)
    private List<InquiryRecord> records;
}
