package com.linzhi.gongfu.vo.trade;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于接前端保存报价价格和税模式信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VOfferRequest {

    /**
     * 税模式
     */
    private String taxModel;

    /**
     * 产品列表
     */
    private List<VProduct> products;

    @Data
    public static class VProduct {

        /**
         * 产品序号
         */
        private int code;
        /**
         * 价格
         */
        private BigDecimal price;
    }
}
