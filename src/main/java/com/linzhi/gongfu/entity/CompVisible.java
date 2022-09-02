package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 公司设置对格友可见信息
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comp_visible")
public class CompVisible implements Serializable {

    /**
     * 单位主键
     */
    @Id
    @Column(name = "dc_comp_id", length = 12, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    private String dcCompId;

    /**
     * 可见内容
     */
    @Column(name = "visible_content", length = 50, nullable = false)
    private String visibleContent;

}
