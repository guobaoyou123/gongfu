package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.Availability;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库房授权操作员表
 *
 * @author zhangguanghua
 * @create_at 2022-12-08
 */
@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comp_warehouse_operator")
public class WareHouseOperator {

    @EmbeddedId
    private WareHouseOperatorId wareHouseOperatorId;
}
