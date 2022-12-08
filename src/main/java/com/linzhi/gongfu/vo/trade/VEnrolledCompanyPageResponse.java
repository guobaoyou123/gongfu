package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于前端查询入格单位信息列表分页的响应体组建
 *
 * @author zgh
 * @create_at 2022-01-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VEnrolledCompanyPageResponse extends VBaseResponse {

    /**
     * 公司列表
     */
    List<VCompany> companies;

    /**
     * 当前页码
     */
    private int current;

    /**
     * 总条数
     */
    private int total;

    @Data
    public static class VCompany {
        /**
         * 公司编码
         */
        private String code;

        /**
         * 公司名称
         */
        private String companyName;
    }
}
