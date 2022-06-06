package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于响应前端对于修改采购合同预览展示请求
 *
 * @author zgh
 * @create_at 2022-06-06
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VModifyContractPreviewResponse extends  VBaseResponse{

    List<VProduct> products;
    @Data
    public static class  VProduct{
        private String productId;
        private String productCode;
        private BigDecimal amount;
        private BigDecimal modifiedAmount;
        private BigDecimal invoicedAmount;
        private BigDecimal receivedAmount;
    }
}
