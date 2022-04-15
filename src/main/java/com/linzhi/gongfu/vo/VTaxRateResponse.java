package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于前端查询供应商列表的响应体组建
 *
 * @author zgh
 * @create_at 2022-01-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VTaxRateResponse extends VBaseResponse  {

     List<VTaxRates> taxRates;

     @Data
    public  static class VTaxRates{
        private String id;
        private String type;
        private BigDecimal rate;
        private String deflag;
        private long createdAt;
    }
}
