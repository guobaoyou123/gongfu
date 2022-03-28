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
    private String companyName;
    private String name ;
    private String phone ;
    private String addressCode ;
    private String state ;
    private List<String> code;
}
