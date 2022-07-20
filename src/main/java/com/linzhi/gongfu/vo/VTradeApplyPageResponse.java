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
public class VTradeApplyPageResponse extends VBaseResponse  {

    private int current;
    private int total;
    List<VTradeApply> applies;

    @Data
    public  static  class  VTradeApply{
        private String code;

        private String companyCode;

        private String companyName;

        private String companyShortName;
    }
}
