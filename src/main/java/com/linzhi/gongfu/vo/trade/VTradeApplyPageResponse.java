package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于前端待处理申请采购列表的响应体组建
 *
 * @author zgh
 * @create_at 2022-01-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VTradeApplyPageResponse extends VBaseResponse {

    /**
     * 待处理申请采购列表
     */
    List<VTradeApply> applies;

    /**
     * 当前页
     */
    private int current;

    /**
     * 总条数
     */
    private int total;

    @Data
    public static class VTradeApply {

        /**
         * 申请记录编码
         */
        private String code;

        /**
         * 公司编码
         */
        private String companyCode;

        /**
         * 公司名称
         */
        private String companyName;

        /**
         * 公司简称
         */
        private String companyShortName;
    }
}
