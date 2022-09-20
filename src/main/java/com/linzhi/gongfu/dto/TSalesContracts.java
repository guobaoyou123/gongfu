package com.linzhi.gongfu.dto;

import com.linzhi.gongfu.enumeration.ContractState;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TSalesContracts {



    /**
     * 合同唯一id
     */
    private String id;
    /**
     * 合同编号
     */
    private String code;

    /**
     * 版本号
     */
    private int revision;

    /**
     * 本单位合同编号
     */
    private String orderCode;


    /**
     * 创建者姓名
     */
    private String createdByName;

    /**
     * 创建时间
     */

    private LocalDateTime createdAt;

    /**
     * 状态（0-未确认 1-确认 2-撤销）
     */
    private String state;

    /**
     * 是否配对
     */
    private String paired;

    /**
     * 种类
     */
    private int category;

    /**
     * 客户合同号
     */
    private String customerContractNo;

    /**
     * 客户名称
     */
    private String buyerCompName;

    /**
     * 含税总价
     */
    private BigDecimal taxedTotal;

    /**
     * 确认价税合计
     */
    private BigDecimal confirmTaxedTotal;



}
