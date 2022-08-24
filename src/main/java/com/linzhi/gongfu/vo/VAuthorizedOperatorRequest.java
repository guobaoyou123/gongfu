package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于接前端授权操作员的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VAuthorizedOperatorRequest {
    private  String operators;
}
