package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于接前端添加地址信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VAddressRequest {

    private String areaCode;

    private String address;

    private Boolean flag;

    private String  country;

    private String state;

    private List<String> codes;

}
