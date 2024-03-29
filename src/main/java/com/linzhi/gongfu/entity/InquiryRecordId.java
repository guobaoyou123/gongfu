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
public class InquiryRecordId implements Serializable {
    /**
     * 询价单唯一id
     */

    @Column(name = "inquiry_id", length = 50, nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String inquiryId;
    /**
     * 序号
     */

    @Column(name = "code", nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private Integer code;
}
