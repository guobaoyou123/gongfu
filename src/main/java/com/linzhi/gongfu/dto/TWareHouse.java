package com.linzhi.gongfu.dto;

import com.linzhi.gongfu.vo.warehousing.VWareHouseListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用于转移入仓库信息
 *
 * @author zgh
 * @create_at 2022-12-09
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TWareHouse {

    /**
     * 仓库编码
     */
    private String code;

    /**
     * 仓库名称
     */
    private String name;

    /**
     * 库房面积
     */
    private BigDecimal acreage;

    /**
     * 创建时间
     */
    private String  createdAt;

    /**
     * 授权操作员列表
     */
    private List<TOperatorInfo> AuthorizedOperators;
}
