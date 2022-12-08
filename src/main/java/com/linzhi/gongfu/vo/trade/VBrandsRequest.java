package com.linzhi.gongfu.vo.trade;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用于接前端设置经营品牌的请求参数
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VBrandsRequest implements Serializable {

    /**
     * 品牌编码
     */
    private List<String> brands;
}
