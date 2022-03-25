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
public class CompContactsId implements Serializable {

    /**
     * 单位id
     */
    @Column(name="dc_comp_id",length = 5,nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String dcCompId;

    /**
     * 单位操作员编号
     */
    @Column(name="operator_code",length = 10,nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String operatorCode;
    /**
     * 地址编码
     */
    @Column(name="addr_code",length = 20,nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String addrCode;
    /**
     * 联系人编码
     */
    @Column(name="code",length = 20,nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String code;
}
