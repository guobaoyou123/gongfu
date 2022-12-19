package com.linzhi.gongfu.vo.storage;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于响应前端对于库房详情的请求
 *
 * @author zhangguanghua
 * @create_at 2022-12-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VWareHouseResponse extends VBaseResponse {
    private VWareHouse werahouse;

    @Data
    public static class VWareHouse {
        /**
         * 仓库编码
         */
        private String code;

        /**
         * 仓库名称
         */
        private String name;

        /**
         * 类型0-未初始化 1-已经初始化没有产生出库单 2-已经产生出库单
         */
        private String  type;

        /**
         * 库房面积
         */
        private BigDecimal acreage;

        /**
         * 区域编码
         */
        private String  areaCode;

        /**
         * 区域名称
         */
        private String  areaName;

        /**
         * 详细地址
         */
        private String  address;

        /**
         * 创建时间
         */
        private String  createdAt;

        /**
         * 授权操作员列表
         */
        private List<VOperator> AuthorizedOperators;
    }

    /**
     * 操作员实体
     */
    @Data
    public static class VOperator{
        private String code;

        private String name;
    }
}
