package com.linzhi.gongfu.repository;

import com.linzhi.gongfu.entity.Word;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * 操作文案词汇内容的Repository
 *
 * @author xutao
 * @create_at 2022-01-21
 */
public interface WordRepository extends CrudRepository<Word, String>, QuerydslPredicateExecutor<Word> {

}
