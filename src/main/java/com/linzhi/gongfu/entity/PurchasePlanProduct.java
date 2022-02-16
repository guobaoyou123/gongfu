package com.linzhi.gongfu.entity;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "purchase_plan_product")
public class PurchasePlanProduct implements Serializable {
    @EmbeddedId
    private PurchasePlanProductId purchasePlanProductId;
    /**
     * 产品代码
     */
    @Column(name = "product_code",length = 50,nullable = false)
    private String productCode;
    /**
     * 品牌编码
     */
    @Column(name = "brand_code",length = 10,nullable = false)
    private String brandCode;
    /**
     * 可销库存
     */
    @Column(name = "deliver_num")
    private BigDecimal deliverNum;
    /**
     * 在途库存
     */
    @Column(name = "tran_num")
    private BigDecimal tranNum;
    /**
     * 需求数量
     */
    @Column(name = "demand")
    private BigDecimal demand;
    /**
     * 安全库存
     */
    @Column(name = "safety_stock")
    private BigDecimal safetyStock;
    /**
     * 上次采购价格
     */
    @Column(name = "before_sales_price")
    private BigDecimal beforeSalesPrice;
    /**
     * 正在询价数量
     */
    @Column(name = "inquiry_num")
    private BigDecimal inquiryNum;
    /**
     * 品牌名称
     */
    @Column(name = "brand",length = 10)
    private String brand;
    /**
     * 产品描述
     */
    @Column(name = "describe",length = 100)
    private String describe;
    /**
     * 计价单位
     */
    @Column(name = "charge_unit",length = 50)
    private String chargeUnit;
    /**
     * 面价
     */
    @Column(name = "face_price")
    private BigDecimal facePrice;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumns({@JoinColumn(name = "plan_code",referencedColumnName = "plan_code", insertable = true, updatable = true),
        @JoinColumn(name = "dc_comp_id",referencedColumnName = "dc_comp_id", insertable = true, updatable = true),
    @JoinColumn(name = "product_id",referencedColumnName = "product_id", insertable = true, updatable = true)})
    @NotFound(action= NotFoundAction.IGNORE)
    private List<PurchasePlanProductSupplier> salers;


}
