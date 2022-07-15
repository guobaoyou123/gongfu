package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Set;

/**
 * 用于响应前端对于人员（包括权限）列表的请求
 *
 * @author zgh
 * @create_at 2022-07-15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VOperatorListResponse extends VBaseResponse{
    List<VOperator> operators;

    @Data
    public  static  class  VOperator{

        private String code;
        private String name;
        private Set<String> scenes;
    }
}
