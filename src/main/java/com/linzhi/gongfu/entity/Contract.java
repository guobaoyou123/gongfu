package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.ContractState;
import com.linzhi.gongfu.enumeration.InquiryState;
import com.linzhi.gongfu.enumeration.InquiryType;
import com.linzhi.gongfu.enumeration.TaxMode;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="contract_base")
public class Contract {
    /**
     * 合同唯一id
     */
    @Id
    @Column(length = 50,nullable = false)
    @NotNull
    @NotBlank
    @NonNull
    private String id ;
    @Column(name = "revision", insertable = false,updatable = false)
    private int revision;
    @Column(name = "supplierContractNo", insertable = false,updatable = false)
    private String  supplierContractNo;
    /**
     * 合同编号
     */
    @Column(length = 40,nullable = false)
    @NotNull
    @NotBlank
    private String code;

    @Column(name = "order_code", insertable = false,updatable = false)
    private String orderCode;
    /**
     * 类型（0-采购合同 1-销售合同）
     */
    @Column(name = "type",length = 1)
    private InquiryType type;

    /*
     * 对应销售合同记录系统主键
     */
    @Column(name = "sales_contract_id",length = 50)
    private String salesContractId;
    /*
     * 对应销售合同记录系统编码
     */
    @Column(name = "salesContractCode",updatable = false,insertable = false)
    private String salesContractCode;
    /*
     * 对应销售合同记录中本单位编码
     */
    @Column(name = "salesOrderCode",updatable = false,insertable = false)
    private String salesOrderCode;
    /**
     * 所属单位编码
     */
    @Column(name = "created_by_comp",length = 20)
    private String createdByComp;
    /**
     * 所属操作员编码
     */
    @Column(name = "created_by",length = 20)
    private String createdBy;

    @Column(name = "createdByName",updatable = false,insertable = false)
    private String createdByName;
    /**
     * 客户公司编码
     */
    @Column(name = "buyer_comp",length = 40,nullable = false)
    private String buyerComp;
    /**
     * 客户名称
     */
    @Column(name = "buyer_comp_name",length = 100)
    private String buyerCompName;
    /**
     * 供应商公司编号
     */
    @Column(name = "saler_comp",length = 40,nullable = false)

    private String salerComp;
    /**
     * 供应商名称
     */
    @Column(name = "saler_comp_name",length = 100)
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




}
