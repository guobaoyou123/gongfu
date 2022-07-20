package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

@Data
@Jacksonized
@NoArgsConstructor
public class VTradeApplyConsentRequest implements Serializable {

    private List<String> brandCodes;

    private String taxModel;

    private List<String> authorizedOperator;
}
