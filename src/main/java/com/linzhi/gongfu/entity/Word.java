package com.linzhi.gongfu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * 前端页面文案实体
 *
 * @author xutao
 * @create_at 2022-01-21
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_noun_app")
@Where(clause = "type in ('31', '32', '33', '34', '35')")
public class Word {
    /**
     * 文案词汇编号
     */
    @Id
    @Column(length = 8, nullable = false)
    @NonNull
    private String code;

    /**
     * 文案词汇父级分类编号
     */
    @Column(name = "parent_code", length = 5)
    private String parent;

    /**
     * 文案词汇父级分类名称
     */
    @Column(name = "parent_name", length = 50)
    private String parentName;

    /**
     * 文案词汇分类名称
     */
    @Column(length = 50, nullable = false)
    @NonNull
    private String name;

    /**
     * 文案词汇分类编号
     */
    @Column(length = 2, nullable = false)
    @NonNull
    private String type;

    /**
     * 文案词汇内容
     */
    @Column(name = "noun_name", length = 100)
    private String word;

    /**
     * 文案词汇的定位键，用于前端对文案词汇进行索引
     */
    @Transient
    public String getWordKey() {
        return name.concat("@").concat(parentName);
    }
}
