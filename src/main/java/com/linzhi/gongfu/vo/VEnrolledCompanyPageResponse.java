package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于前端供应商详情的响应体组建
 *
 * @author zgh
 * @create_at 2022-01-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VEnrolledCompanyPageResponse extends VBaseResponse  {

    private int current;
    private int total;
    List<VCompany> companies;

    @Data
    public  static  class  VCompany{
        private String code;

        private String companyName;

        private String companyShortName;
    }
}
