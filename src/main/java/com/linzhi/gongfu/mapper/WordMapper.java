package com.linzhi.gongfu.mapper;

import com.linzhi.gongfu.dto.TWord;
import com.linzhi.gongfu.entity.Word;
import com.linzhi.gongfu.vo.VPreloadWordsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用于转换前端文案词汇相关的数据结构
 *
 * @author xutao
 * @create_at 2022-01-21
 */
@Mapper(componentModel = "spring")
public interface WordMapper {
    @Mapping(target = "key", source = "wordKey")
    TWord toDTO(Word word);

    @Mapping(target = "parent", source = "parentName")
    VPreloadWordsResponse.VWord toVO(TWord word);
}
