package com.linzhi.gongfu.vo;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于响应前端对于公司基本信息的预加载请求
 *
 * @author xutao
 * @create_at 2022-01-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@SuperBuilder(toBuilder = true)
public class VPreloadCompanyInfoResponse extends VBaseResponse {
    /**
     * 公司全名
     */
    private String companyName;

    /**
     * 公司简称
     */
    private String companyShortName;

    /**
     * 公司所具备的功能场景
     */
    @Singular
    private List<String> companyScenes;
}
