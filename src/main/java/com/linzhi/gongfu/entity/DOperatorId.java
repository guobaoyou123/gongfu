package com.linzhi.gongfu.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.*;

@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class DOperatorId implements Serializable {
    @Column(name = "comp_code", length = 50, nullable = false)
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
