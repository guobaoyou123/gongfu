package com.linzhi.gongfu.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
@NoArgsConstructor
public abstract class PageTools {

    public static <T> Page<T> listConvertToPage(List<T> list, Pageable pageable) {
        Long start = pageable.getOffset();
        Long end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        return new PageImpl<T>(list.subList(start.intValue(), end.intValue()),pageable,  list.size());
    }


}
