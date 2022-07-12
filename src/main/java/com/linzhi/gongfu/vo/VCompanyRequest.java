package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于接前端添加、修改本公司信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VCompanyRequest {

    private String companyShortName;

    private String contactName;

    private String contactPhone;

    private String areaCode;

    private String areaName;

    private String address;

    private String introduction;
}
