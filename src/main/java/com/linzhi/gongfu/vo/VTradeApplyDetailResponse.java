package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于前端申请采购的响应体组建
 *
 * @author zgh
 * @create_at 2022-01-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VTradeApplyDetailResponse extends VBaseResponse{

    /**
     * 申请记录详情
     */
    private VApply record;

    @Data
    public static class VApply{
        /**
         * 记录编码
         */
        private String code;

        /**
         * 拒绝原因
         */
        private String refuseRemark;

        /**
         * 处理时间
         */
        private String handledAt;

        /**
         * 状态 0-待处理 1-同意申请 2-拒绝申请
         */
        private String state;


        /**
         * 申请或者被申请公司编码
         */
        private String companyCode;

        /**
         * 申请或者被申请公司名称
         */
        private String companyName;

        /**
         * 申请或者被申请公司简称
         */
        private String companyShortName;

        /**
         * 申请或者被申请公司社会统一信用代码
         */
        private String usci;
        /**
         * 申请或者被申请公司联系人姓名
         */
        private String contactName;

        /**
         * 申请或者被申请公司联系人电话
         */
        private String contactPhone;

        /**
         * 申请或者被申请公司区域编码
         */
        private String areaCode;

        /**
         * 申请或者被申请公司区域名称
         */
        private String areaName;

        /**
         * 申请或者被申请公司详细地址
         */
        private String address;

        /**
         * 申请或者被申请公司 公司简介
         */
        private String introduction;

        /**
         * 申请备注
         */
        private String remark;
        /**
         * 创建时间
         */
        private String createdAt;

        /**
         * 税模式
         */
        private String taxModel;

        private List<VBrand> brands;
    }

    @Data
    public static class VBrand{

        /**
         * 品牌编码
         */
        private String code;

        /**
         * 品牌名称
         */
        private String name;
    }
}
