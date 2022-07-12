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
public class VOperatorDetailResponse extends VBaseResponse{
    VOperator operator;

    @Data
    public  static  class  VOperator{

        private String code;
        private String name;
        private String phone;
        private String sex;
        private String birthday;
        private String areaCode;
        private String areaName;
        private String address;
        private String entryAt;
        private String resignationAt;
        private List<VScene> scenes;
    }

    @Data
    public  static  class  VScene{

        private String code;
        private String name;

    }
}
