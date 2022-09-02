package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端对于人员列表的请求
 *
 * @author zgh
 * @create_at 2022-07-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VOperatorPageResponse extends VBaseResponse {

    /**
     * 人员列表
     */
    List<VOperator> operators;

    /**
     * 当前页面
     */
    private int current;

    /**
     * 总条数
     */
    private int total;

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
    }
}
