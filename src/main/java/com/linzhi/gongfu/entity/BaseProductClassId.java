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
public class BaseProductClassId implements Serializable {

    /**
     * 编码
     */
    @Column(name = "code", length = 10, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String code;

    /**
     * 产品分类
     */
    @Column(name = "type", length = 3, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String type;
}
