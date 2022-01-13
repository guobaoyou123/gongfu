package com.linzhi.gongfu.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class OperatorId implements Serializable {
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
