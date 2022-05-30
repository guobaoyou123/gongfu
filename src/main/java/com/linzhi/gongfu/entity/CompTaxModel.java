package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.TaxMode;
import com.linzhi.gongfu.enumeration.Trade;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * 交易表，用于查询税模式
 *
 * @author zgh
 * @create_at 2021-12-27
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comp_trade")
public class CompTaxModel {
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

}
