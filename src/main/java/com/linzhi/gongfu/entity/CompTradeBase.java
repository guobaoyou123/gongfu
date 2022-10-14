package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.Availability;
import com.linzhi.gongfu.enumeration.TaxMode;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 交易表
 *
 * @author zgh
 * @create_at 2022-07-19
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comp_trade")
public class CompTradeBase implements Serializable {
    /**
     * 主键：卖方编号+买方编号
     */
    @EmbeddedId
    private CompTradeId compTradeId;

    /**
     * 税模式（0：不含税，1：含税）
     */
    @Column(name = "tax_model")
    private TaxMode taxModel;

    /**
     * 状态（0禁用，1启用）
     */
    @Column(name = "state")
    private Availability state;

    /**
     * 所属买方操作员
     */
    @Column(name = "buyer_belong_to")
    private String buyerBelongTo;

    /**
     * 所属卖方单位操作员
     */
    @Column(name = "saler_belong_to")
    private String salerBelongTo;

}
