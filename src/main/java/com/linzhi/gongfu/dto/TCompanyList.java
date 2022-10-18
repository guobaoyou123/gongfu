package com.linzhi.gongfu.dto;

import com.linzhi.gongfu.enumeration.Availability;
import lombok.*;


/**
 * 客户或者供应商列表信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TCompanyList {

    private String code;

    private String enCode;

    private String shortName;

    private String operator;

    private String operatorName;

    private String brand;

    private String brandName;

    private Availability state;
}
