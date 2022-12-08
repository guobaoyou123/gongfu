package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于响应前端对于合同中已开票产品列表的预加载请求
 *
 * @author zgh
 * @create_at 2022-02-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VInvoicedResponse extends VBaseResponse {
    /**
     * 产品列表
     */
    List<VProduct> products;

    @Data
    public static class VProduct {

        /**
         * 产品主键
         */
        private String id;

        /**
         * 产品编码
         */
        private String code;

        /**
         * 描述
         */
        private String describe;

        /**
         * 计价单位
         */
        private String chargeUnit;

        /**
         * 数量
         */
        private BigDecimal amount;

        /**
         * 已开票数量
         */
        private BigDecimal invoiceAmount;
    }
}
