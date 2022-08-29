package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.ContractState;
import com.linzhi.gongfu.enumeration.InquiryType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
@Table(name = "contract_base")
public class ContractList {
    /**
     * 合同唯一id
     */
    @Id
    @Column(length = 50, nullable = false)
    @NotNull
    @NotBlank
    @NonNull
    private String id;

    /**
     * 版本号
     */
    @Column(name = "revision", insertable = false, updatable = false)
    private int revision;

    /**
     * 供应商合同号
     */
    @Column(name = "supplierContractNo", insertable = false, updatable = false)
    private String supplierContractNo;

    /**
     * 合同编号
     */
    @Column(length = 40, nullable = false)
    @NotNull
    @NotBlank
    private String code;

    /**
     * 本单位合同编号
     */
    @Column(name = "order_code", insertable = false, updatable = false)
    private String orderCode;

    /**
     * 类型（0-采购合同 1-销售合同）
     */
    @Column(name = "type", length = 1)
    private InquiryType type;

    /*
     * 对应销售合同记录系统主键
     */
    @Column(name = "sales_contract_id", length = 50)
    private String salesContractId;

    /*
     * 对应销售合同记录系统编码
     */
    @Column(name = "salesContractCode", updatable = false, insertable = false)
    private String salesContractCode;

    /*
     * 对应销售合同记录中本单位编码
     */
    @Column(name = "salesOrderCode", updatable = false, insertable = false)
    private String salesOrderCode;

    /**
     * 所属单位编码
     */
    @Column(name = "created_by_comp", length = 20)
    private String createdByComp;

    /**
     * 所属操作员编码
     */
    @Column(name = "created_by", length = 20)
    private String createdBy;

    /**
     * 创建者姓名
     */
    @Column(name = "createdByName", updatable = false, insertable = false)
    private String createdByName;

    /**
     * 客户公司编码
     */
    @Column(name = "buyer_comp", length = 40, nullable = false)
    private String buyerComp;

    /**
     * 客户名称
     */
    @Column(name = "buyer_comp_name", length = 100)
    private String buyerCompName;

    /**
     * 供应商公司编号
     */
    @Column(name = "saler_comp", length = 40, nullable = false)
    private String salerComp;

    /**
     * 供应商名称
     */
    @Column(name = "saler_comp_name", length = 100)
    private String salerCompName;

    /**
     * 供应商名称简称
     */
    @Column(name = "salerCompNameShort", updatable = false, insertable = false)
    private String salerCompNameShort;

    /**
     * 状态（0-未确认 1-确认 2-撤销）
     */
    @Column
    private ContractState state;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 种类
     */
    @Column(name = "category", updatable = false, insertable = false)
    private int category;

    /**
     * 含税总价
     */
    @Column(name = "taxedTotal", updatable = false, insertable = false)
    private BigDecimal taxedTotal;

    /**
     * 确认价税合计
     */
    @Column(name = "confirmTaxedTotal", updatable = false, insertable = false)
    private BigDecimal confirmTaxedTotal;
}
