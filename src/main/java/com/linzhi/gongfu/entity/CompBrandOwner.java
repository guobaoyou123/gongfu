package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.*;

/**
 * 我拥有的品牌
 *
 * @author zgh
 * @create_time 2022-02-07
 */
@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comp_brand_owner")
public class CompBrandOwner {
    @EmbeddedId
    private CompBrandOwnerId compBrandOwnerId;
}
