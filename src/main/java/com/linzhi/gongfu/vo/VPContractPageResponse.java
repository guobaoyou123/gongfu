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
public class VPContractPageResponse extends VBaseResponse{

    private int current;
    private int total;
    List<VContract> contracts;

    @Data
    public static class VContract{
        private String id ;
        private String code;
        private int revision;
        private String contractNo;
        private String ownerCode;
        private String ownerName;
        private String supplierName;
        private String salesContractId;
        private String salesContractCode;
        private String salesContractNo;
        private String createdAt;
        private String state;
        private Boolean paired;
        private String supplierContractNo;
        private int category;
        private BigDecimal taxedTotal;
        private BigDecimal confirmTaxedTotal;
    }
}