package com.linzhi.gongfu.vo;

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
public class VInvoicedResponse extends VBaseResponse{
    List<VProduct> products;

    @Data
    public static class VProduct{
        private String id;
        private String code;
        private String describe;
        private String chargeUnit;
        private BigDecimal amount;
        private BigDecimal invoiceAmount;
    }
}
