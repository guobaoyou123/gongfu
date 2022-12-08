package com.linzhi.gongfu.vo.trade;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于接前端添加、修改本公司信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VCompanyRequest {

    /**
     * 公司简称
     */
    private String companyShortName;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 区域编码
     */
    private String areaCode;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 公司简介
     */
    private String introduction;
}
