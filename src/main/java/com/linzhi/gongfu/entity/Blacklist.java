package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
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
    @Column(name = "created_by",length = 40, nullable = false)
    private String createdBy;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
