package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于前端查询全部客户列表的响应体组建
 *
 * @author zgh
 * @create_at 2022-08-31
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VCustomersResponse extends VBaseResponse {

    private List<VCustomer> customers;

    @Data
    public static class VCustomer {
        /**
         * 系统编码
         */
        private String code;

        /**
         * 公司名称
         */
        private String name;

        /**
         * 排序
         */
        private Integer sort;
    }

}
