package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * 用于响应前端对于驱动方式的预加载请求
 *
 * @author zgh
 * @create_at 2022-02-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VMaterialResponse extends VBaseResponse{

    List<VMaterial> materials;
    @Data
    public static class VMaterial{
        private String code;
        private String name;
        private List<VSubMaterial> children;
    }
    @Data
    public static class VSubMaterial{
        private String code;
        private String name;
    }
}
