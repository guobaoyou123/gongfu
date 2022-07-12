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
public class VOperatorPageResponse extends VBaseResponse{
    private int current;
    private int total;
    List<VOperator> operators;

    @Data
    public  static  class  VOperator{

        private String code;
        private String name;
        private String phone;
        private String state;
    }
}
