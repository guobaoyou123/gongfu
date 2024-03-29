package com.linzhi.gongfu.vo.trade;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于接前端确认生成合同信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VPContractRequest {

    /**
     * 询价单主键
     */
    private String inquiryId;

    /**
     * 本单位合同号
     */
    private String contactNo;

    /**
     * 对应供应商合同号
     */
    private String supplierNo;

    /**
     * 对应客户合同号
     */
    private String customerNo;

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
     * 客户联系人姓名
     */
    private String customerContactName;

    /**
     * 客户联系人电话
     */
    private String customerContactPhone;

    /**
     * 是否强制执行
     */
    private boolean enforce;

    /**
     * 退回产品列表
     */
    private List<DeliveryRecord> deliveryRecords;
    /**
     * 收回产品列表
     */
    private List<VReceivedRecord> receivedRecords;

    @Data
    public static class DeliveryRecord {
        /**
         * 产品主键
         */
        private String productId;

        /**
         * 退回数量
         */
        private BigDecimal returnAmount;
    }

    @Data
    public static class VReceivedRecord {
        /**
         * 产品主键
         */
        private String productId;

        /**
         * 收回数量
         */
        private BigDecimal receivedAmount;
    }
}
