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
public class SysCompareDetailId implements Serializable {
    @Column(name = "code",length = 3,nullable = false)
    @Setter
    @Getter
    @NonNull
    @NotNull
    @NotBlank
    private String code;
    @Column(name = "compare_type",length = 2,nullable = false)
    @Setter
    @Getter
    @NonNull
    @NotNull
    @NotBlank
    private String compare_type;
}
