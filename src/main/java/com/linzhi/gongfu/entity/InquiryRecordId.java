package com.linzhi.gongfu.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
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

    @Column(name = "inquiry_id",length = 50,nullable = false)
    @NotNull
    @NotBlank
    @NonNull
    private String inquiryId ;
    /**
     * 序号
     */

    @Column(name = "code",nullable = false)
    @NotNull
    @NotBlank
    private Integer code ;
}
