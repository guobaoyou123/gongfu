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
public class CompAllowedBrandId implements Serializable {
    /**
     * 单位id
     */
    @Column(name = "comp_code", length = 40, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String compCode;

    /**
     * 品牌编码
     */
    @Column(name = "brand_code", length = 4, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String brandCode;
}
