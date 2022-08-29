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
public class AddressId implements Serializable {

    /**
     * 编码
     */
    @Column(name = "code", length = 20, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String code;

    /**
     * 单位主键
     */
    @Column(name = "dc_comp_id", length = 5, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String dcCompId;
}
