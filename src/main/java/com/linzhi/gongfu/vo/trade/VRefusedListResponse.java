package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于前端始终拒绝名单列表请求的响应体组建
 *
 * @author zgh
 * @create_at 2022-08-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VRefusedListResponse extends VBaseResponse {
    /**
     * 公司列表
     */
    List<VCompany> companies;

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
