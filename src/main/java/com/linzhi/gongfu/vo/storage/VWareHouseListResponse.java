package com.linzhi.gongfu.vo.storage;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于响应前端对于库房列表的请求
 *
 * @author zhangguanghua
 * @create_at 2022-12-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VWareHouseListResponse extends VBaseResponse {
    private List<VWareHouse> list;

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
         * 库房面积
         */
        private BigDecimal acreage;

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
