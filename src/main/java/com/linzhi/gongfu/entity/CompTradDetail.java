package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.TaxMode;
import com.linzhi.gongfu.enumeration.Trade;
import lombok.*;

import javax.persistence.*;
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
public class CompTradDetail implements Serializable {
    /**
     * 主键：卖方编号+买方编号
     */
    @EmbeddedId
    private CompTradId compTradId;

    /**
     * 税模式（0：不含税，1：含税）
     */
    @Column(name = "tax_model")
    private TaxMode taxModel;

    /**
     * 状态（0不可交易，2申请交易，1可交易）
     */
    @Column(name = "state")
    private Trade state;

    /**
     *所属买方操作员
     */
    @Column(name = "buyer_belong_to")
    private TaxMode buyerBelongTo;

    /**
     * 所属卖方单位操作员
     */
    @Column(name = "saler_belong_to")
    private String  salerBelongTo;





}
