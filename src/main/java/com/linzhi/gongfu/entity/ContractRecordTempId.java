package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 合同记录临时表主键
 */
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ContractRecordTempId implements Serializable {
    /**
     * 询价单唯一id
     */

    @Column(name = "contract_id", length = 50, nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String contractId;
    /**
     * 序号
     */

    @Column(name = "code", nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private Integer code;
    /**
     * 版本
     */

    @Column(name = "revision", nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private Integer revision;
}
