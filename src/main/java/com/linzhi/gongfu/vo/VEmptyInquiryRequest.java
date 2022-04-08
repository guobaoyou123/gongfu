package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于接前端新建空的询价单供应商信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VEmptyInquiryRequest {
    private String supplierCode;
}
