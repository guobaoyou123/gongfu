package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用于转移询价记录信息
 *
 * @author zgh
 * @create_at 2022-11-14
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TNotificationInquiry {

    /**
     * 消息通知编码
     */
    private String code;

    /**
     * 状态
     */
    private String state;

    /**
     * 询价单主键
     */
    private String inquiryId;

    /**
     * 税模式
     */
    private String taxModel;

    /**
     * 客户单位
     */
    private String customerName;

    /**
     * 产品列表
     */
    List<TInquiryRecord> products;
}
