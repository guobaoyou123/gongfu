package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;
/**
 * 拒绝申请采购和始终拒绝申请采购的请求参数
 *
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VTradeApplyRefuseRequest implements Serializable {

    private String remark;
    private String state;
}
