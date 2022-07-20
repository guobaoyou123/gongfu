package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dc_scene_menu")
public class SceneMenu {
    @EmbeddedId
    private SceneMenuId sceneMenuId;
}
