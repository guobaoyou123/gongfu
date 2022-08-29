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
public class CompTradId implements Serializable {
    @Column(name = "comp_saler", length = 40, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String compSaler;

    @Column(name = "comp_buyer", length = 40, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String compBuyer;
}
