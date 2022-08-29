package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.TradeApply;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 格友申请采购表
 *
 * @author zgh
 * @create_at 2022-07-19
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comp_trade_apply")
public class CompTradeApply implements Serializable {

    @Id
    @Column(name = "code", length = 50, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String code;

    /**
     * 类型（1-申请采购 ）
     */
    @Column(length = 1, nullable = false)
    private String type;

    /**
     * 创建单位
     */
    @Column(name = "created_comp_by", length = 40, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String createdCompBy;

    /**
     * 创建人
     */
    @Column(name = "created_by", length = 40, nullable = false)
    private String createdBy;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 创建者申请备注
     */
    @Column(name = "created_remark", length = 100)
    private String createdRemark;

    /**
     * 处理单位
     */
    @Column(name = "handled_comp_by", length = 40, nullable = false)
    private String handledCompBy;

    /**
     * 处理人
     */
    @Column(name = "handled_by", length = 40)
    private String handledBy;

    /**
     * 拒绝原因
     */
    @Column(name = "refuse_remark", length = 100)
    private String refuseRemark;

    /**
     * 处理时间
     */
    @Column(name = "handled_at")
    private LocalDateTime handledAt;

    /**
     * 状态  0-申请中 1-同意 2-拒绝 3-始终拒绝
     */
    @Column(name = "state")
    private TradeApply state;

    /**
     * 申请单位公司
     */
    @OneToOne
    @JoinColumn(name = "created_comp_by", referencedColumnName = "id", insertable = false, updatable = false)
    private EnrolledCompany createdCompany;

    /**
     * 处理单位公司
     */
    @OneToOne
    @JoinColumn(name = "handled_comp_by", referencedColumnName = "id", insertable = false, updatable = false)
    private EnrolledCompany handledCompany;

    /**
     * 公司简称
     */
    @Column(name = "chi_short", length = 20)
    @NotNull
    @NotBlank
    private String shortNameInCN;

    /**
     * 区域编号，区级行政区划编号
     */
    @Column(name = "area_code", length = 6)
    private String areaCode;

    /**
     * 区域名称，区级行政区划全称
     */
    @Column(name = "area_name", length = 60)
    private String areaName;

    /**
     * 公司地址
     */
    @Column(length = 100)
    private String address;

    /**
     * 公司联系人
     */
    @Column(name = "cont_name", length = 40)
    private String contactName;

    /**
     * 公司联系人电话
     */
    @Column(name = "cont_phone", length = 20)
    private String contactPhone;

    /**
     * 公司简介
     */
    @Column
    private String introduction;

}
