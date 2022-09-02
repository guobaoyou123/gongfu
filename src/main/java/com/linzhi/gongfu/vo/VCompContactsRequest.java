package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于接前端添加、修改地址联系人、启用停用联系人信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VCompContactsRequest {

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
     * 启用、禁用人员编码
     */
    private List<String> code;
}
