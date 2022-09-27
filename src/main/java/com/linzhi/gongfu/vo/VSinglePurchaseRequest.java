package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于接前端添保存单独采购数量的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VSinglePurchaseRequest {

    /**
     * 产品id
     */
    private String id;

    /**
     * 单采数量
     */
    private BigDecimal singlePurchaseAmount;

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
