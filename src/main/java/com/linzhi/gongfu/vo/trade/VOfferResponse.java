package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于响应前端对于查看询价记录详情的请求
 *
 * @author zgh
 * @create_at 2022-11-14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VOfferResponse extends VBaseResponse {
    /**
     * 消息详情
     */
    private VInquiry offer;

    @Data
    public static class VInquiry {

        /**
         * 消息通知编码
         */
        private String code;


        /**
         * 询价单主键
         */
        private String inquiryId;

        /**
         * 税模式
         */
        private String taxModel;

        /**
         * 状态
         */
        private String state;

        /**
         * 客户单位
         */
        private String customerName;

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

        /**
         * 上次报价单价
         */
        private BigDecimal preOfferedPrice;

        /**
         * 上次买给该客户的单价
         */
        private BigDecimal preSalesPrice;
    }
}
