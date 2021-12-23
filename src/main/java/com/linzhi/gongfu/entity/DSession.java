package com.linzhi.gongfu.entity;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

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
    private String token;
    private String companyCode;
    private String companyName;
    private String operatorCode;
    private String operatorName;
    private LocalDateTime expriesAt;
}
