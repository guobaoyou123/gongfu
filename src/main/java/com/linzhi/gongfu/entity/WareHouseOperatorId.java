package com.linzhi.gongfu.entity;

import com.linzhi.gongfu.enumeration.Availability;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库房授权操作员主键
 *
 * @author zhangguanghua
 * @create_at 2022-12-08
 */
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class WareHouseOperatorId implements Serializable {

    /**
     * 库房编号
     */
    @Column(name = "code", length = 20, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String code;

    /**
     * 单位编码
     */
    @Column(name = "comp_id", length = 12, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String compId;

    /**
     * 操作员编码
     */
    @Column(name = "operator_code", length = 10, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String operatorCode;


}
