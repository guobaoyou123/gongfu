package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

/**
 * 用于接前端添加退回和不退回临时记录的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VDeliveryTempRequest {
    private String productId;
    private BigDecimal receivedAmount;
    private BigDecimal returnAmount;
}
