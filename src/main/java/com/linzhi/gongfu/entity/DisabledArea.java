package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comp_area_disabled")
public class DisabledArea implements Serializable {

    /**
     * 主键
     */
    @EmbeddedId
    private DisabledAreaId disabledAreaId;

    /**
     *区域名称
     */
    @Column(length = 100)
    private String name;
}
