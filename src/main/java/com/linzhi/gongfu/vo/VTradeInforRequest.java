package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于接前端修改交易品牌或者修改报价模式的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VTradeInforRequest {
    private List<String> brands;
    private  String taxmodel;
}
