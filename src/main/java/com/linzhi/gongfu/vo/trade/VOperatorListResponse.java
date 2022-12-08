package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Set;

/**
 * 用于响应前端权限统计列表的请求
 *
 * @author zgh
 * @create_at 2022-07-15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VOperatorListResponse extends VBaseResponse {

    /**
     * 操作员列表
     */
    List<VOperator> operators;

    @Data
    public static class VOperator {
        /**
         * 人员编码
         */
        private String code;

        /**
         * 姓名
         */
        private String name;

        /**
         * 拥有权限编码
         */
        private Set<String> scenes;
    }
}
