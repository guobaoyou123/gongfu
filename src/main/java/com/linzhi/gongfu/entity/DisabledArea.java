package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
@Builder
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
     * 国家编码
     */
    @Column(length = 50,nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String country;

    /**
     * 国家编码
     */
    @Column(length = 8,nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String idcode;
    /**
     *区域名称
     */
    @Column(length = 100)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
