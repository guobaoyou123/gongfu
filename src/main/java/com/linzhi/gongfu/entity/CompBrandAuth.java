package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * 我授权的品牌
 *
 * @author zgh
 * @create_time 2022-02-07
 */
@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comp_brand_auth")
public class CompBrandAuth {
    @EmbeddedId
    private CompBrandAuthId compBrandAuthId;

    /**
     * 授权公司
     */
    @Column(name = "auth_comp", length = 20)
    private String authComp;

    /**
     * 授权书
     */
    @Column(name = "picture", length = 20)
    private String picture;

    /**
     * 授权时间
     */
    @Column(name = "auth_at", columnDefinition = "DATE")
    private LocalDate auth_at;
}
