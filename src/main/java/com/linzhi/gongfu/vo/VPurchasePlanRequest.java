package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Jacksonized
@NoArgsConstructor
public class VPurchasePlanRequest {
    private String planCode;
    private List<String> products;
    private List<String> suppliers;
}
