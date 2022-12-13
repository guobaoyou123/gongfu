package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockSumId implements Serializable {

    /**
     * 产品主键
     */
    @Column(name = "product_id", length = 64, nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String productId;

    /**
     * 单位编码
     */
    @Column(name = "comp_id", length = 12, nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String compId;
}
