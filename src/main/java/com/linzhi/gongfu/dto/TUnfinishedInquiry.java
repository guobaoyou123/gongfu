package com.linzhi.gongfu.dto;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * 未完成的询价单信信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TUnfinishedInquiry {

    private String id;

    private String code;

    private BigDecimal totalPriceVat;

    private Long counts;
}
