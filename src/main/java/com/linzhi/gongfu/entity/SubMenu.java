package com.linzhi.gongfu.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 二级主菜单信息实体
 *
 * @author xutao
 * @create_at 2022-01-20
 */
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "sys_menu")
@Where(clause = "parent != '0'")
public class SubMenu extends BaseMenu {

}
