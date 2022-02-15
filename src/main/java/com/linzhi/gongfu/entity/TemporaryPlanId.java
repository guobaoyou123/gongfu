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
public class TemporaryPlanId implements Serializable {
    /**
     *单位id
     */
    @Column(name = "dc_comp_id",length = 20,nullable = false)
    @Getter
    @Setter
    @NotNull
    @NotBlank
    private String dcCompId;
    /**
     *创建者
     */
    @Column(name = "created_by",length = 20,nullable = false)
    @Getter
    @Setter
    @NotNull
    @NotBlank
    private String createdBy;
    /**
     *产品id
     */
    @Column(name = "product_id",length = 64,nullable = false)
    @Getter
    @Setter
    @NotNull
    @NotBlank
    private String productId;
}
