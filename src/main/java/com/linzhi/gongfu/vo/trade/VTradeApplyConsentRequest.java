package com.linzhi.gongfu.vo.trade;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

/**
 * 同意申请采购的请求参数
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VTradeApplyConsentRequest implements Serializable {

    /**
     * 品牌编码列表
     */
    private List<String> brandCodes;

    /**
     * 税模式
     */
    private String taxModel;

    /**
     * 授权操作员编码列表
     */
    private List<String> authorizedOperator;
}
