package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.converter.AvailabilityConverter;
import com.linzhi.gongfu.enumeration.Availability;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
@Builder
@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sys_compare_detail")
public class SysCompareDetail implements Serializable {
    @EmbeddedId
    private SysCompareDetailId sysCompareDetailId;
    /**
     * 中文名
     */
    @Column(name = "chi_name",length = 20,nullable = false)
    @NotNull
    @NotBlank
    private String chiName;

    /**
     * 启停用状态
     */
    @Column(length = 1)
    @NotNull
    private Availability state;
}
