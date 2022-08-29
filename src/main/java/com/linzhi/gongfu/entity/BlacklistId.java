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
public class BlacklistId implements Serializable {

    /**
     * 单位id
     */
    @Column(name = "dc_comp_id", length = 40, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String dcCompId;

    /**
     * 类型（1-申请采购 ）
     */
    @Column(name = "type", length = 1, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String type;

    /**
     * 被拒绝单位id
     */
    @Column(name = "berefuse_comp_id", length = 40, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String beRefuseCompId;
}
