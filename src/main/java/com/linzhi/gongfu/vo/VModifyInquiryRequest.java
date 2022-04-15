package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于接前端修改询价单信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VModifyInquiryRequest {
    private String taxModel;
    private BigDecimal goodsVat;
    private BigDecimal serviceVat;
    private List<VProduct> products;

    @Data
    public  static  class  VProduct{
        private int code;
        private BigDecimal price;
        private BigDecimal vatRate;
        private BigDecimal amount;
    }
}
