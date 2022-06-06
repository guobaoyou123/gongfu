package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TContractRecordPreview {
    private String productId;


    private String productCode;
    /**
     * 已收货
     */

    private BigDecimal delivered;
    /**
     * 退回数量
     */

    private BigDecimal received;
    /**
     * 已开票数量
     */

    private BigDecimal invoicedAmount;
    /**
     * 数量
     */

    private BigDecimal amount;
    /**
     * 合同修改后数量
     */

    private BigDecimal modifiedAmount;
}
