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
    /**
     * 产品主键编码
     */
    private String id;

    /**
     * 产品系统编码
     */
    private String code;

    /**
     * 产品描述
     */
    private String describe;

    /**
     * 计价单位
     */
    private String chargeUnit;

    /**
     * 收货（发货）数量
     */
    private BigDecimal received;

    /**
     * 产品数量
     */
    private BigDecimal amount;
}
