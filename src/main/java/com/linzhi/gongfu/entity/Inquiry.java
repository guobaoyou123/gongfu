package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.InquiryState;
import com.linzhi.gongfu.enumeration.InquiryType;
import com.linzhi.gongfu.enumeration.TaxMode;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="inquiry_base")
public class Inquiry {
    /**
     * 询价单唯一id
     */
    @Id
    @Column(length = 50,nullable = false)
    @NotNull
    @NotBlank
    private String id ;
    /**
     * 询价单编号
     */
    @Column(length = 40,nullable = false)
    @NotNull
    @NotBlank
    private String code;
    /**
     * 合同编码
     */
    @Column(name = "order_code",length = 40)
    private String orderCode;
    /**
     * 供应商合同编码
     */
    @Column(name = "saler_order_code",length = 40)
    private String salerOrderCode;
    /**
     * 类型（0-询价单 1-报价当）
     */
    @Column(name = "type",length = 1)
    private InquiryType type;
    /*
     * 对应销售合同编码
     */
    @Column(name = "sales_order_code",length = 40)
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
     * 买方联系人姓名
     */
    @Column(name = "buyer_contact_name",length = 20)
    private String buyerContactName;
    /**
     * 买方联系人电话
     */
    @Column(name = "buyer_contact_phone",length = 20)
    private String buyerContactPhone;
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
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 状态（0-未形成合同 1-以生成合同 2-撤销合同）
     */
    @Column
    private InquiryState state;
    /**
     * 税模式（0-未税 1-含税）
     */
    @Column(name = "offer_mode",length = 1)
    private TaxMode offerMode;

}
