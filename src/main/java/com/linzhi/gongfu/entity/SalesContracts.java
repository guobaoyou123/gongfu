package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.ContractState;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 合同列表
 */
@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class SalesContracts {

    /**
     * 合同唯一id
     */
    @Id
    private String id;

    /**
     * 合同编号
     */
    @Column
    private String code;

    /**
     * 版本号
     */
    @Column
    private int revision;

    /**
     * 本单位合同编号
     */
    @Column
    private String orderCode;

    /**
     * 所属操作员编码
     */
    @Column
    private String createdBy;

    /**
     * 创建者姓名
     */
    @Column
    private String createdByName;

    /**
     * 创建时间
     */
    @Column
    private LocalDateTime createdAt;

    /**
     * 状态（0-未确认 1-确认 2-撤销）
     */
    @Column
    private ContractState state;

    /**
     * 是否配对
     */
    @Column
    private String paired;

    /**
     * 种类
     */
    @Column
    private int category;

    /**
     * 客户合同号
     */
    @Column
    private String customerContractNo;

    /**
     * 客户名称
     */
    @Column
    private String buyerCompName;

    /**
     * 含税总价
     */
    @Column
    private BigDecimal taxedTotal;

    /**
     * 确认价税合计
     */
    @Column
    private BigDecimal confirmTaxedTotal;

}
