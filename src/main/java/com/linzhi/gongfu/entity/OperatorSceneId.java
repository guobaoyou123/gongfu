package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class OperatorSceneId implements Serializable {

    /**
     * 单位主键
     */

    @Column(name = "dc_comp_id", length = 20, nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String dcCompId;
    /**
     * 序号
     */

    @Column(name = "operator_code", length = 20, nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String operatorCode;
    /**
     * 场景编码
     */

    @Column(name = "scene_code", length = 10, nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String sceneCode;

}
