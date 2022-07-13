package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端对于场景列表的加载请求
 *
 * @author zgh
 * @create_at 2022-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VSceneListResponse extends VBaseResponse{

    List<VScene> scenes;
    @Data
    public static class VScene{
        private String code;
        private String name;
        private String suggestion;
    }
}
