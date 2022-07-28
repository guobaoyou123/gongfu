package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应查询询价单列表的请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VInquiryListResponse extends VBaseResponse{
    List<VInquiry> inquiries;

    @Data
    public  static  class  VInquiry{
        private String id ;
        private String code;
        private String ownerCode;
        private String ownerName;
        private String supplierName;
        private String salesContractId;
        private String salesContractCode;
        private String salesContractNo;
        private String  salesCustomerNo;
        private String purchaseContractId;
        private String purchaseContractCode;
        private String purchaseContractNo;
        private String purchaseSupplierNo;
        private String createdAt;
        private String state;
    }
}
