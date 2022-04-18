package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于接前端询修改导入产品信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VImportProductTempRequest {
    private String brandCode;
    private int itemNo;
}
