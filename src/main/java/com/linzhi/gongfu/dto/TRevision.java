package com.linzhi.gongfu.dto;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 用于转移合同版本信息
 *
 * @author zgh
 * @create_at 2022-05-27
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TRevision {

    private String createdAt;

    private int revision;
}
