package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.TradeApply;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
     * 通知类型（1-申请采购 ）
     */
    @Column(length = 1, nullable = false)
    private  String type;

    /**
     * 创建单位
     */
    @Column(name = "created_comp_by",length = 40, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String createdCompBy;

    /**
     * 创建人
     */
    @Column(name = "created_by",length = 40, nullable = false)
    private String createdBy;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 创建者申请备注
     */
    @Column(name = "created_remark",length = 100)
    private String createdRemark;

    /**
     * 处理单位
     */
    @Column(name = "handled_comp_by",length = 40, nullable = false)
    private String handledCompBy;

    /**
     * 处理人
     */
    @Column(name = "handled_by",length = 40)
    private String handledBy;

    /**
     * 拒绝原因
     */
    @Column(name = "refuse_remark",length = 100)
    private String refuseRemark;

    /**
     * 处理时间
     */
    @Column(name = "handled_at")
    private LocalDateTime handledAt;

    /**
     * 创建时间
     */
    @Column(name = "state")
    private TradeApply state;


}
