package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

/**
 * 用于接前端询价单或者合同添加产品信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VInquiryProductRequest {

    /**
     * 产品id
     */
    private String productId;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private BigDecimal amount;
}
