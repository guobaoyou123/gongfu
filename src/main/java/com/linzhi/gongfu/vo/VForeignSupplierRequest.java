package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于接前端添加、修改地外供应商信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VForeignSupplierRequest {
    private String code;

    private String encode;

    private String companyName;

    private String companyShortName;

    private String usci;

    private String contactName;

    private String contactPhone;

    private String areaCode;

    private String areaName;

    private String address;

    private String email;

    private String phone;

    private String taxMode;

    private List<String> brands ;

    private List<String> codes;
}
