package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
@Data
@Jacksonized
@NoArgsConstructor
public class VTradeApplyRequest  implements Serializable {
    private String remark;
    private String invitationCode;
    private String applyCompCode;
}
