package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ContractReceived {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     *
     */
    @Column(name = "code")
    private String code;
    @Column(name = "describe")
    private String describe;
    @Column(name = "charge_unit")
    private String chargeUnit;
    @Column
    private BigDecimal delivered;
    @Column
    private BigDecimal received;

    @Column
    private BigDecimal amount;
}
