package com.linzhi.gongfu.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 公司操作员场景中间表
 *
 * @author zhangguanghua
 * @create_at 2022-07-13
 */
@Builder
@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dc_operator_scene")
public class OperatorScene  implements Serializable {
    /**
     * 主键
     */
    @EmbeddedId
    private OperatorSceneId operatorSceneId;
}
