package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * 申请采购请求参数
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VTradeApplyRequest implements Serializable {

    /**
     * 备注
     */
    private String remark;

    /**
     * 邀请码
     */
    private String invitationCode;

    /**
     * 格友编码
     */
    private String applyCompCode;
}
