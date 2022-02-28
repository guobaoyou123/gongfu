package com.linzhi.gongfu.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.regex.Pattern;

@NoArgsConstructor
public abstract class PageTools {

    public static <T> Page<T> listConvertToPage(List<T> list, Pageable pageable) {
        Long start = pageable.getOffset();
        Integer size = pageable.getPageSize();
        Long end = (start + size) > list.size() ? list.size() : (start + size);
        if(end<start){
            start=end;
        }
        return new PageImpl<T>(list.subList(start.intValue(), end.intValue()),pageable,  list.size());
    }
    //验证页码
    public static Integer verificationPageNum(String pageNum){
        //判断是否为数字
        if(!(Pattern.compile("[0-9]*")).matcher(pageNum).matches())
            return 0;

        if(Integer.valueOf(pageNum)<0)
            return 0;

        return Integer.valueOf(pageNum)-1;
    }
    //验证条数
    public static Integer verificationPageSize(String pageSize){
        if(!(Pattern.compile("[0-9]*")).matcher(pageSize).matches())
            return 1;
        if(Integer.valueOf(pageSize)<0)
            return 1;
        return Integer.valueOf(pageSize);
    }
}
