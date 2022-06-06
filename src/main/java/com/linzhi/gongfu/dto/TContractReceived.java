package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
/**
 * 用于转移合同中产品收货信息
 *
 * @author zgh
 * @create_at 2022-06-02
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TContractReceived {

    private String productId;


    private String productCode;


    private BigDecimal received;


    private BigDecimal amount;
}
