package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

/**
 * 用于接前端询价单添加产品信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VGenerateContractRequest {
    private String inquiryId;
    /**
     * 本单位合同号
     */
    private String contactNo;

    /**
     *对应供应商合同号
     */
    private String supplierNo;
    /**
     * 总的价税合计值
     */
    private BigDecimal sum;
    /**
     * 地址编码
     */
    private String addressCode;
    /**
     * 联系人编码
     */
    private String contactCode;
    /**
     * 供应商联系人姓名
     */
    private String supplierContactName;
    /**
     * 供应商联系人电话
     */
    private String supplierContactPhone;

    /**
     * 是否强制执行
     */
    private boolean enforce;


}
