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
public class ContractRevisionId implements Serializable {

    /**
     * 合同唯一id
     */

    @Column(name = "id",length = 50,nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String id ;
    /**
     * 版本号
     */

    @Column(name = "revision",nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private Integer revision ;
}
