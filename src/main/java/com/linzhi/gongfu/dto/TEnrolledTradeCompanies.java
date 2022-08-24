package com.linzhi.gongfu.dto;

import lombok.*;

/**
 * 入格供应商或者客户简易信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TEnrolledTradeCompanies {

    /**
     * 系统编码
     */
    private String code;

    /**
     * 公司名称
     */
    private String nameInCN;

    /**
     * 公司简称
     */
    private String shortNameInCN;

    /**
     * 社会统一信用代码
     */
    private String USCI;
}
