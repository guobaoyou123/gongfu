package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于前端查询税率列表的响应体组建
 *
 * @author zgh
 * @create_at 2022-01-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VTaxRateResponse extends VBaseResponse {

    /**
     * 税率列表
     */
    List<VTaxRates> taxRates;

    @Data
    public static class VTaxRates {
        /**
         * 税率编码
         */
        private String id;
        /**
         * 类型
         */
        private String type;

        /**
         * 税率
         */
        private BigDecimal rate;

        /**
         * 是否默认
         */
        private String deflag;

        /**
         * 创建时间
         */
        private long createdAt;
    }
}
