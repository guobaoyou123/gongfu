package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于接前端修改人员场景信息的请求
 */
@Jacksonized
@Data
@NoArgsConstructor
public class VOperatorSceneRequest {
    private String code;
    private List<String> scenes;
}
