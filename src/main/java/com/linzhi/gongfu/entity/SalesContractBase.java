package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.ContractState;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 销售合同基础表实体
 */
@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sales_contract_base")
public class SalesContractBase implements Serializable {
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
     * 合同编号
     */
    @Column(length = 40, nullable = false)
    @NotNull
    @NotBlank
    private String code;

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
     * 所属操作员
     */
    @OneToOne
    @JoinColumns({
        @JoinColumn(name = "created_by_comp", referencedColumnName = "dc_comp_id", insertable = false, updatable = false),
        @JoinColumn(name = "created_by", referencedColumnName = "code", insertable = false, updatable = false)
    })
    private OperatorBase operator;
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
     * 配对码
     */
    @Column(name = "paired_code")
    private String pairedCode;

}
