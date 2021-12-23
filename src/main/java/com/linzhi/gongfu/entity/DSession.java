package com.linzhi.gongfu.entity;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

/**
 * 用户会话信息
 * @author xutao
 * @create_at 2021-12-22
 */
@Jacksonized
@Builder
public record DSession (
        String companyCode,
        String companyName,
        String operatorCode,
        String operatorName,
        LocalDateTime expriesAt
) {}
