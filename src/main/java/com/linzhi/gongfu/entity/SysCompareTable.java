package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Builder
@Setter
@Getter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sys_compare_table")
public class SysCompareTable implements Serializable {
    @Id
    @NotNull
    @NotBlank
    @Column(length = 4,nullable = false)
    private String code;
    /**
     * 对照表名称
     */
    @Column(length = 100,nullable = false)
    @NotNull
    @NotBlank
    private String name;
    /**
     * 排序
     */
    @Column(name = "serial_number",nullable = false)
    @NotNull
    private Integer serialNumber;
    @OneToMany
    @JoinColumn(name = "compare_type",referencedColumnName = "code")
    private List<SysCompareDetail> list;
}
