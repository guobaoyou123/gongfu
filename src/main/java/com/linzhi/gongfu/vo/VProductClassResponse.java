package com.linzhi.gongfu.vo;

import com.linzhi.gongfu.dto.TProductClass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VProductClassResponse extends VBaseResponse{
    List<VProductClass> classes;
    @Data
    public static class VProductClass{
        private String code;
        private String name;
        private List<VSubProductClass> children;
    }
    @Data
    public static class VSubProductClass{
        private String code;
        private String name;
    }
}
