package com.linzhi.gongfu.vo.trade;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于接前端添加和修改人员信息的请求
 */
@Jacksonized
@Data
@NoArgsConstructor
public class VOperatorRequest {

    /**
     * 姓名
     */
    private String name;

    /**
     * 电话
     */
    private String phone;

    /**
     * 性别
     */
    private String sex;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 区域编码
     */
    private String areaCode;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 入职时间
     */
    private String entryAt;

    /**
     * 离职时间
     */
    private String resignationAt;

    /**
     * 场景编码
     */
    private List<String> scenes;

    /**
     * 状态
     */
    private String state;
}
