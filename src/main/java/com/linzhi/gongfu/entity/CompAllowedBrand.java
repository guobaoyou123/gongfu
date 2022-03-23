package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * 我经营的品牌
 * @author zgh
 * @create_time 2022-01-28
 */
@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="comp_allowed_brand")
public class CompAllowedBrand implements Serializable {
    @EmbeddedId
    private CompAllowedBrandId compAllowedBrandId;
}
