package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.Availability;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 用于定义产品分类的基础字段信息类，该类不可实例化，不对应任何数据表
 *
 * @author zgh
 * @create_at 2022-02-08
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@MappedSuperclass
public class BaseProductClass {

    /**
     * 产品分类编码
     */
    @EmbeddedId
    private BaseProductClassId baseProductClassId;

    /**
     * 名称
     */
    @Column( length = 50, nullable = false)
    @NonNull
    private String name;

    /**
     * 父级菜单的编号
     */
    @Column( name="parent_code",length = 10, nullable = false)
    private String parentCode;

    /**
     *
     */
    @Column
    @Deprecated
    private String lev;

    /**
     * 启停用状态
     */
    @Column(length = 1)
    @NotNull
    private Availability state;
}
