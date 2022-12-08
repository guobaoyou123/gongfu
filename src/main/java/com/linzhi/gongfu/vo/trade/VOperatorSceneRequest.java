package com.linzhi.gongfu.vo.trade;

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

    /**
     * 人员编码
     */
    private String code;

    /**
     * 场景编码列表
     */
    private List<String> scenes;
}
