package com.linzhi.gongfu.vo.warehousing;

import com.linzhi.gongfu.vo.VBaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于添加库房的请求体
 *
 * @author zhangguanghua
 * @create_at 2022-12-09
 */
@Data
@Jacksonized
@NoArgsConstructor
public class VWareHouseRequest{

        /**
         * 仓库名称
         */
        private String name;

        /**
         * 库房面积
         */
        private BigDecimal acreage;

        /**
         * 区域编码
         */
        private String  areaCode;

        /**
         * 详细地址
         */
        private String  address;

        /**
         * 授权操作员列表
         */
        private List<String> authorizedOperators;

}
