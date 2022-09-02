package com.linzhi.gongfu.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

/**
 * 一级主菜单信息实体
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_class")
@Where(clause = "parent_code = '00'")
public class MainProductClass extends BaseProductClass {

    /**
     * 二级分类
     */
    @OneToMany
    @JoinColumns({
        @JoinColumn(name = "parent_code", referencedColumnName = "code"),
        @JoinColumn(name = "type", referencedColumnName = "type")
    })
    private List<SubProductClass> children;
}
