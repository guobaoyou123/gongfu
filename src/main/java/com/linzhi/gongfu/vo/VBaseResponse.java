package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 服务端RESTful API返回前端的基础响应体
 * @author xutao
 * @create_at 2021-12-23
 */
@Jacksonized
@Data
@SuperBuilder(toBuilder = true)
public class VBaseResponse {
    /**
     * 响应状态码
     */
    @NotNull
    @Min(value = 0)
    private int code;

    /**
     * 响应状态具体信息
     */
    @NonNull
    @NotNull
    @NotBlank
    private String message;
}
