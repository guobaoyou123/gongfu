package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

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
}
