package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SceneMenuId implements Serializable {

    /**
     * 采购计划编码
     */
    @Column(name = "scene_code", nullable = false, length = 10)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    private String sceneCode;

    /**
     * 入格单位id
     */
    @Column(name = "menu_code", nullable = false, length = 10)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    private String menuCode;
}
