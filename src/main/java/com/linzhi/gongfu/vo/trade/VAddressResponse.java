package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端对于地址列表的请求
 *
 * @author zgh
 * @create_at 2021-12-24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VAddressResponse extends VBaseResponse {

    /**
     * 地址列表
     */
    private List<VAddress> addresses;

    @Data
    public static class VAddress {
        /**
         * 地址编码
         */
        private String code;

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
         * 是否为本部标志
         */
        private Boolean flag;

        /**
         * 状态（0-禁用 1-启用）
         */
        private String state;

        /**
         * 区域是否禁用
         */
        private boolean disabled;

        /**
         * 排序
         */
        private int sort;

        /**
         * 是否可编辑(0,停用;1,启用)
         */
        private Boolean readOnly;
    }
}
