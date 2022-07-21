package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * 申请采购请求参数
 *
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VTradeApplyRequest  implements Serializable {
    private String remark;
    private String invitationCode;
    private String applyCompCode;
}
