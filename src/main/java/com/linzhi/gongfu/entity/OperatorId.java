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
public class OperatorId implements Serializable {
    @Column(name = "dc_comp_id", length = 50, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    private String companyCode;

    @Column(name = "code", length = 10, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    private String operatorCode;
}
