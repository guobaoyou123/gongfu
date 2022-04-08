package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

/**
 * 用于接前端询价单添加产品信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VInquiryProductResquest {
    private String productId;
    private BigDecimal price;
    private BigDecimal amount;
}
