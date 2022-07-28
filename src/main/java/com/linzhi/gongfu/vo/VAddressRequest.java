package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于接前端添加地址信息或者禁用启用地址或者 修改地址信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VAddressRequest {

    /**
     * 区域编码
     */
    private String areaCode;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 是否为本部地址
     */
    private Boolean flag;

    /**
     * 状态
     */
    private String state;

    /**
     * 禁用地址编码
     */
    private List<String> codes;

}
