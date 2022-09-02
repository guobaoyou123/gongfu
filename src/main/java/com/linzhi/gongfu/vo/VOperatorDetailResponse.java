package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端对于人员详情的请求
 *
 * @author zgh
 * @create_at 2022-07-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VOperatorDetailResponse extends VBaseResponse {

    /**
     * 人员信息
     */
    VOperator operator;

    @Data
    public static class VOperator {

        /**
         * 编码
         */
        private String code;

        /**
         * 姓名
         */
        private String name;

        /**
         * 电话
         */
        private String phone;

        /**
         * 性别
         */
        private String sex;

        /**
         * 生日
         */
        private String birthday;

        /**
         * 区域编码
         */
        private String areaCode;

        /**
         * 区域名称
         */
        private String areaName;

        /**
         * 详细地址
         */
        private String address;

        /**
         * 入职时间
         */
        private String entryAt;

        /**
         * 离职时间
         */
        private String resignationAt;

        /**
         * 状态
         */
        private String state;

        /**
         * 用于场景列表
         */
        private List<VScene> scenes;
    }

    /**
     * 用于表示一个场景
     */
    @Data
    public static class VScene {
        /**
         * 编码
         */
        private String code;

        /**
         * 名称
         */
        private String name;

    }
}
