package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于接前端修改状态的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VStateRequest {

    String state;

}
