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
public class CompBrandAuthId implements Serializable {
    /**
     * 品牌编码
     */
    @Column(name = "brand_code", length = 10, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String brandCode;
    /**
     * 被授权单位id
     */
    @Column(name = "be_auth_comp", length = 20, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String beAuthComp;
}
