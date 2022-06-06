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
public class DeliverRecordId implements Serializable {
    @Column(name = "deliver_code", length = 50, nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String deliverCode;

    @Column(name = "code",  nullable = false)
    @NonNull
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private int code;
}
