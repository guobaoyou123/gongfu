package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ContractRecordPreview {
    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_code")
    private String productCode;
    /**
     * 退回数量/已发货数量
     */
    @Column
    private BigDecimal delivered;
    /**
     * 已收货或者收回数量
     */
    @Column
    private BigDecimal received;
    /**
     * 已开票数量
     */
    @Column
    private BigDecimal invoicedAmount;
    /**
     * 数量
     */
    @Column
    private BigDecimal amount;
    /**
     * 合同修改后数量
     */
    @Column
    private BigDecimal modifiedAmount;
}
