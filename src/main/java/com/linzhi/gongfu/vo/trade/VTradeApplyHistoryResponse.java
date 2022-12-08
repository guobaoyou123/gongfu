package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于前端申请历史记录列表的响应体组建
 *
 * @author zgh
 * @create_at 2022-07-22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VTradeApplyHistoryResponse extends VBaseResponse {

    /**
     * 历史申请记录
     */
    List<VApply> applies;

    /**
     * 当前页
     */
    private int current;

    /**
     * 总条数
     */
    private int total;

    /**
     * 用于表示一个申请记录
     */
    @Data
    public static class VApply {
        /**
         * 申请记录编码
         */
        private String code;

        /**
         * 申请单位或者被申请单位编码
         */
        private String companyCode;

        /**
         * 申请单位或者被申请单位名称
         */
        private String companyName;

        /**
         * 申请单位或者被申请单位简称
         */
        private String companyShortName;

        /**
         * 状态 0-申请中 1-同意 2-拒绝 3-始终拒绝
         */
        private String state;

        /**
         * 申请时间
         */
        private String createdAt;

        /**
         * 申请备注
         */
        private String remark;
    }

}
