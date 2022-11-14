package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于响应前端对于查看消息通知详情的请求
 *
 * @author zgh
 * @create_at 2022-08-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VNotificationResponse extends VBaseResponse {
    /**
     * 消息详情
     */
    private VNotification detail;

    @Data
    public static class VNotification {

        /**
         * 消息通知编码
         */
        private String code;

        /**
         * 通知内容
         */
        private String content;

        /**
         * 税模式
         */
        private String taxModel;

        /**
         * 报价产品列表
         */
        private List<VProduct> products;
    }

    @Data
    public static class VProduct{
        /**
         * 序号
         */
        private Integer itemNo;

        /**
         * 产品id
         */
        private String id;

        /**
         * 产品编码
         */
        private String code;

        /**
         * 描述
         */
        private String describe;

        /**
         * 计价单位
         */
        private String chargeUnit;

        /**
         * 品牌编码
         */
        private String brandCode;

        /**
         * 品牌名称
         */
        private String brandName;

        /**
         * 价格
         */
        private BigDecimal price;

        /**
         * 数量
         */
        private BigDecimal amount;

        /**
         * 是否报价
         */
        private Boolean isOffer;

    }
}
