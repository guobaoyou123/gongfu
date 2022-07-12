package com.linzhi.gongfu.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * 用于接前端添加、修改本公司信息的请求
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VCompanyVisibleRequest {


    private  Boolean visible;

    private  String content;
}
