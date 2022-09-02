package com.linzhi.gongfu.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Table;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "sys_class")
@Where(clause = "parent_code != '00'")
public class SubProductClass extends BaseProductClass {
}
