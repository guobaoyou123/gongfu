package com.linzhi.gongfu.vo.trade;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * 拒绝申请采购和始终拒绝申请采购的请求参数
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VTradeApplyRefuseRequest implements Serializable {

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private String state;
}
