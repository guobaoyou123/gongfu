package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePlanProductSupplierId implements Serializable {
    /**
     * 采购计划编码
     */
    @Column(name = "plan_code", nullable = false, length = 30)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    private String planCode;

    /**
     * 入格单位id
     */
    @Column(name = "dc_comp_id", nullable = false, length = 20)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    private String dcCompId;

    /**
     * 产品id
     */
    @Column(name = "product_id", nullable = false, length = 64)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    private String productId;

    /**
     * 产品id
     */
    @Column(name = "saler_code", nullable = false, length = 10)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    private String salerCode;
}
