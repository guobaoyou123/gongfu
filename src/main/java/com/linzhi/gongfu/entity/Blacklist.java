package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 拒接交易名单实体
 *
 * @author zgh
 * @create_at 2022-07-20
 */
@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comp_trade_apply_refuse")
public class Blacklist implements Serializable {

    @EmbeddedId
    private BlacklistId blacklistId;

    /**
     * 创建人
     */
    @Column(name = "created_by", length = 40)
    private String createdBy;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 黑名单公司详情
     */
    @OneToOne
    @JoinColumn(name = "berefuse_comp_id", referencedColumnName = "id", insertable = false, updatable = false)
    private EnrolledCompany company;
}
