package com.linzhi.gongfu.vo;

import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于前端预加载文案词汇的响应体组建
 *
 * @author xutao
 * @create_at 2022-01-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VPreloadWordsResponse extends VBaseResponse {
    /**
     * 文案词汇集合
     */
    @Singular
    private Set<VWord> words;

    /**
     * 前端预加载文案词汇描述
     *
     * @author xutao
     * @create_at 2022-01-21
     */
    @Data
    public static class VWord {
        private String code;
        private String name;
        private String parent;
        private String key;
        private String type;
        private String word;
    }
}
