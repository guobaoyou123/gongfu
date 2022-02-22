package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.TaxModel;
import com.linzhi.gongfu.enumeration.Whether;
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
@Table(name="inquiry_base")
public class Inquiry {
    /**
     * 询价单唯一id
     */
    @Id
    @Column(length = 50,nullable = false)
    @NotNull
    @NotBlank
    @NonNull
    private String id ;
    /**
     * 询价单编号
     */
    @Column(length = 40,nullable = false)
    @NotNull
    @NotBlank
    private String code;
    /**
     * 客户公司编码
     */
    @Column(name = "comp_buyer",length = 40,nullable = false)
    private String compBuyer;
    /**
     * 供应商名称
     */
    @Column(name = "comp_buyer_name",length = 100)
    private String compBuyerName;
    /**
     * 客户中操作员编号
     */
    @Column(name = "buyer_created_by",length = 40)
    private String buyerCreatedBy;
    /**
     * 供应商公司编号
     */
    @Column(name = "comp_saler",length = 40,nullable = false)

    private String compSaler;
    /**
     * 供应商名称
     */
    @Column(name = "comp_saler_name",length = 100)
    private String compSalerName;
    /**
     * 供应商中操作员编号
     */
    @Column(name = "saler_created_by",length = 40)
    private String salerCreatedBy;

    /**
     * 合同编码
     */
    @Column(name = "order_code",length = 40)
    private String orderCode;
    /**
     * 销售合同编码
     */
    @Column(name = "sales_order_code",length = 40)
    private String salesOrderCode;
    /**
     * 货物税率
     */
    @Column(name = "vat1_rate")
    private BigDecimal vatProductRate;
    /**
     * 货物税率
     */
    @Column(name = "vat2_rate")
    private BigDecimal vatServiceRate;
    /**
     *折扣
     */
    @Column(name = "discount")
    private BigDecimal discount;
    /**
     * 税额
     */
    @Column(name = "vat")
    private BigDecimal vat;
    /**
     * 折扣金额
     */
    @Column(name = "dis_price")
    private BigDecimal disPrice;
    /**
     * 含税折扣金额
     */
    @Column(name = "discount_price_v")
    private BigDecimal disPriceVat;
    /**
     * 未税总价
     */
    @Column(name = "sum_amount")
    private BigDecimal sumAmount;
    /**
     * 含税总价
     */
    @Column(name = "sum_amount_v")
    private BigDecimal sumAmountVat;
    /**
     * 总的价税合计
     */
    @Column(name = "sum_amount_vat")
    private BigDecimal totalPriceTaxSum;
    /**
     * 税额
     */
    @Column(name = "sum_amount_vat_lr")
    private BigDecimal inputTotalPriceTaxSum;
    /**
     * 状态（0-未形成合同 1-以生成合同）
     */
    @Column
    private Whether state;
    /**
     * 税模式（0-未税 1-含税）
     */
    @Column(name = "tax_model",length = 1)
    private TaxModel taxModel;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "inquiry_id",referencedColumnName = "id", insertable = true, updatable = true)
    @NotFound(action= NotFoundAction.IGNORE)
    private List<InquiryRecord> records;
}
