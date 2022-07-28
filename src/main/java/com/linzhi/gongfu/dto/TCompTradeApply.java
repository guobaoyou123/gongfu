package com.linzhi.gongfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于转申请采购表基本信息
 *
 * @author zgh
 * @create_at 2022-07-20
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TCompTradeApply {
    private String code;

    private String companyCode;

    private String companyName;

    private String companyShortName;

    private String handledCompanyCode;


    private String handledCompanyName;

    private String handledCompanyShortName;

    private String type;

    private String state;

    private String dcCompId;

    private String createdAt;
}
