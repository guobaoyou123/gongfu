package com.linzhi.gongfu.vo.trade;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端查看联系人列表的请求
 *
 * @author zgh
 * @create_at 2021-12-24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VCompContactsResponse extends VBaseResponse {

    /**
     * 联系人列表
     */
    private List<Contacts> contacts;

    @Data
    public static class Contacts {

        /**
         * 编码
         */
        private String code;

        /**
         * 公司名称
         */
        private String companyName;

        /**
         * 姓名
         */
        private String name;

        /**
         * 电话
         */
        private String phone;

        /**
         * 地址编码
         */
        private String addressCode;

        /**
         * 状态
         */
        private String state;

        /**
         * 是否可编辑(0,停用;1,启用)
         */
        private Boolean readOnly;
    }

}
