package com.linzhi.gongfu.mapper;

import java.util.Set;

import com.linzhi.gongfu.dto.TScene;
import com.linzhi.gongfu.entity.Scene;

import org.mapstruct.Mapper;

/**
 * 用于转换操作员以及菜单项所使用的场景信息
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@Mapper(componentModel = "spring")
public interface SceneMapper {
    TScene toDTO(Scene scene);

    Set<TScene> toDTOs(Set<Scene> scenes);

    Scene toEntity(TScene scene);

    Set<Scene> toEntities(Set<TScene> scenes);
}
