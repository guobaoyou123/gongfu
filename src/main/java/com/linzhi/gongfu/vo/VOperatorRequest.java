package com.linzhi.gongfu.vo;

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
    private String name;
    private String phone;
    private String sex;
    private String birthday;
    private String areaCode;
    private String address;
    private String entryAt;
    private String resignationAt;
    private List<String> scenes;
    private String state;
}
