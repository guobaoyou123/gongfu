package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TProductClass;
import com.linzhi.gongfu.entity.MainProductClass;
import com.linzhi.gongfu.entity.SubProductClass;
import com.linzhi.gongfu.vo.VProductClassResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 用于转换产品分类相关信息
 *
 * @author zgh
 * @create_at 2022-02-08
 */
@Mapper(componentModel = "spring")
public interface MainProductClassMapper {
    @Mapping(target = "code",source = "baseProductClassId.code")
    TProductClass toDTO(MainProductClass mainProductClass);
    @Mapping(target = "code",source = "baseProductClassId.code")
    List<TProductClass> toSubProductClassDTOs(List<SubProductClass> subProductClass);

    @Mapping(target = "code",source = "baseProductClassId.code")
    @Mapping(target = "children",ignore = true )
    TProductClass toDTO(SubProductClass subProductClass);

    VProductClassResponse.VProductClass toPreloadMainProductClass(TProductClass tProductClass);
    List<VProductClassResponse.VSubProductClass> toPreloadSubProductClasses(List<TProductClass> tProductClasses);
    VProductClassResponse.VSubProductClass toPreloadSubProductClass(TProductClass tProductClass);
}
