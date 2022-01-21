package com.linzhi.gongfu.service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.linzhi.gongfu.dto.TWord;
import com.linzhi.gongfu.mapper.WordMapper;
import com.linzhi.gongfu.repository.WordRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * 前端文案词汇相关处理服务
 *
 * @author xutao
 * @create_at 2022-01-21
 */
@RequiredArgsConstructor
@Service
public class WordService {
    private final WordRepository wordRepository;
    private final WordMapper wordMapper;

    /**
     * 获取全部前端文案词汇
     *
     * @return 前端文案词汇集合
     */
    @Cacheable("Word;1800")
    public Set<TWord> fetchAllWords() {
        return StreamSupport.stream(wordRepository.findAll().spliterator(), false)
                .map(wordMapper::toDTO)
                .collect(Collectors.toSet());
    }
}
