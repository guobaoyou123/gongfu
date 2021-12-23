package com.linzhi.gongfu.entity;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户会话信息
 * @author xutao
 * @create_at 2021-12-22
 */
@Jacksonized
@Data
@With
@Builder
public class DSession implements Serializable {
    @NotNull
    @NotBlank
    private String token;

    @NotNull
    @NotBlank
    private String companyCode;

    @NotNull
    @NotBlank
    private String companyName;

    @NotNull
    @NotBlank
    private String operatorCode;

    @NotNull
    @NotBlank
    private String operatorName;

    @NotNull
    @NotBlank
    @FutureOrPresent
    private LocalDateTime expriesAt;
}
