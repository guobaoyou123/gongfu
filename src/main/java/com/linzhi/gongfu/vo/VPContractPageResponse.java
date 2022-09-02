package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于前端根据条件查询采购合同列表的响应体组建
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VPContractPageResponse extends VBaseResponse {

    /**
     * 合同列表
     */
    List<VContract> contracts;

    /**
     * 当前页码
     */
    private int current;

    /**
     * 总条数
     */
    private int total;

    @Data
    public static class VContract {
        /**
         * 合同主键
         */
        private String id;

        /**
         * 合同编码
         */
        private String code;

        /**
         * 版本号
         */
        private int revision;

        /**
         * 本单位合同编码
         */
        private String contractNo;

        /**
         * 所属操作员编码
         */
        private String ownerCode;

        /**
         * 所属操作员名称
         */
        private String ownerName;

        /**
         * 供应商名称
         */
        private String supplierName;

        /**
         * 对应销售合同
         */
        private String salesContractId;

        /**
         * 对应销售合同中本单位编码
         */
        private String salesContractCode;

        /**
         * 对应销售合同系统编码
         */
        private String salesContractNo;

        /**
         *创建时间
         */
        private String createdAt;

        /**
         * 状态
         */
        private String state;

        /**
         * 是否配对
         */
        private Boolean paired;

        /**
         * 配对对应的供应商合同记录号
         */
        private String supplierContractNo;

        /**
         * 产品种类
         */
        private int category;

        /**
         * 价税合计
         */
        private BigDecimal taxedTotal;

        /**
         * 确认价税合计
         */
        private BigDecimal confirmTaxedTotal;
    }
}
