package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.DemandSource;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "purchase_plan")
public class PurchasePlan implements Serializable {
    @EmbeddedId
    private PurchasePlanId purchasePlanId;
    /**
     * 创建者
     */
    @Column(name = "created_by",length = 20,nullable = false)
    private String createdBy;
    /**
     * 对应销售合同号
     */
    @Column(name = "sales_code",length = 50)
    private String salesCode;
    /**
     * 创建时间
     */
    @Column(name = "created_at", columnDefinition = "DATE")
    private LocalDate createdAt;
    /**
     * 来源（1-模糊查询 2-单采 3-导入、录入需求  4-run需求）
     */
    @Column(length = 1)
    private DemandSource source;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumns({@JoinColumn(name = "plan_code",referencedColumnName = "plan_code", insertable = true, updatable = true),
    @JoinColumn(name = "dc_comp_id",referencedColumnName = "dc_comp_id", insertable = true, updatable = true)})
    @NotFound(action= NotFoundAction.IGNORE)
    private List<PurchasePlanProduct> product;
}
