package com.linzhi.gongfu.vo;

import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VPreloadMenuResponse extends VBaseResponse {
    private Set<VMainMenu> menus;

    @Data
    public static class VMainMenu {
        private String code;
        private String name;
        private Integer sort;
        private String location;
        private String scene;
        private Set<VSubMenu> children;
    }

    @Data
    public static class VSubMenu {
        private String code;
        private String name;
        private Integer sort;
        private String location;
        private String scene;
    }
}
