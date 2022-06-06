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

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_code")
    private String productCode;
    @Column
    private BigDecimal delivered;
    @Column
    private BigDecimal received;

    @Column
    private BigDecimal amount;
}
