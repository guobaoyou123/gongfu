package com.linzhi.gongfu.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ImportProductStockTempId implements Serializable {

    /**
     * 仓库编码
     */
    @Column(name = "werahouse_code", length = 50, nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String werahouseCode;

    /**
     * 单位id
     */
    @Column(name = "dc_comp_id", length = 20, nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String dcCompId;

    /**
     * 操作员id
     */
    @Column(name = "operator", length = 20, nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String operator;

    /**
     * 序号
     */
    @Column(name = "item_no", nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private Integer itemNo;

    /**
     * 类型
     */
    @Column(name = "type", nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String type;

}
