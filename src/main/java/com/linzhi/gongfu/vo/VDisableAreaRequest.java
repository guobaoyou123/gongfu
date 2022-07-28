package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于接前端添加禁用区域的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VDisableAreaRequest {
    /**
     * 区域编码
     */
    private String code;
}
