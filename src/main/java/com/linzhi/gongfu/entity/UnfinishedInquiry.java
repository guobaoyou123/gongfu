package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UnfinishedInquiry {
    @Id
    private String id;
    @Column
    private String code;
    @Column
    private BigDecimal totalPriceVat;
    @Column
    private String counts;
}
